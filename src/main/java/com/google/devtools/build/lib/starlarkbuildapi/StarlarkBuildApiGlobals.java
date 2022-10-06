// Copyright 2018 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.devtools.build.lib.starlarkbuildapi;

import com.google.devtools.build.docgen.annot.DocumentMethods;
import net.starlark.java.annot.Param;
import net.starlark.java.annot.StarlarkMethod;
import net.starlark.java.eval.EvalException;
import net.starlark.java.eval.StarlarkThread;

/** A collection of global Starlark build API functions that belong in the global namespace. */
@DocumentMethods
public interface StarlarkBuildApiGlobals {

  @StarlarkMethod(
      name = "visibility",
      // TODO(b/22193153): Link to a concepts page for bzl-visibility.
      doc =
          "<i>(Experimental; enabled by <code>--experimental_bzl_visibility</code>. This feature's"
              + " API may change. Only packages that appear in"
              + " <code>--experimental_bzl_visibility_allowlist</code> are permitted to call this"
              + " function. Known issue: This feature currently may not work under bzlmod.)</i>"
              + "<p>Sets the bzl-visibility of the .bzl module currently being initialized."
              + "<p>The bzl-visibility of a module governs whether or not other BUILD and .bzl"
              + " files may load it. (This is distinct from the target visibility of the underlying"
              + " .bzl source file, which governs whether the file may appear as a dependency of"
              + " other targets.) Bzl-visibility works at the level of packages: To load a"
              + " module, the file doing the loading must live in a package that has been granted"
              + " visibility to the module. A module can always be loaded within its own package,"
              + " regardless of its visibility."
              + "<p>Generally, <code>visibility()</code> is called at the top of the .bzl file,"
              + " immediately after its <code>load()</code> statements. (It is poor style to put"
              + " this declaration later in the file or in a helper method.) It may not be called"
              + " more than once per .bzl, or after the .bzl's top-level code has finished"
              + " executing.",
      parameters = {
        @Param(
            name = "value",
            named = false,
            doc =
                "A list of package specification strings, or a single package specification string."
                    + "<p>Package specifications follow the same format as for"
                    + " <code><a href='${link functions#package_group}'>package_group</a></code>,"
                    + " except that negative package specifications are not permitted. That is, a"
                    + " specification may have the forms:"
                    + "<ul>"
                    + "<li><code>\"//foo\"</code>: the package <code>//foo</code>" //
                    + "<li><code>\"//foo/...\"</code>: the package <code>//foo</code> and all of"
                    + " its subpackages." //
                    + "<li><code>\"public\"</code> or <code>\"private\"</code>: all packages or no"
                    + " packages, respectively"
                    + "</ul>"
                    + "<p>The \"@\" syntax is not allowed; all specifications are interpreted"
                    + " relative to the current module's repository."
                    + "<p>If <code>value</code> is a list of strings, the set of packages granted"
                    + " visibility to this module is the union of the packages represented by each"
                    + " specification. (An empty list has the same effect as <code>private</code>.)"
                    + " If <code>value</code> is a single string, it is treated as if it were the"
                    + " singleton list <code>[value]</code>."
                    + "<p>Note that the specification <code>\"//...\"</code> is always interpreted"
                    + " as \"all packages in the current repository\", regardless of the value of"
                    + " the <code>--incompatible_fix_package_group_reporoot_syntax</code> flag.")
      },
      // Ordinarily we'd use enableOnlyWithFlag here to gate access on
      // --experimental_bzl_visibility. However, the StarlarkSemantics isn't available at the point
      // where the top-level environment is determined (see StarlarkModules#addPredeclared and
      // notice that it relies on the overload of Starlark#addMethods that uses the default
      // semantics). So instead we make this builtin unconditionally defined, but have it fail at
      // call time if used without the flag.
      useStarlarkThread = true)
  void visibility(Object value, StarlarkThread thread) throws EvalException;

  @StarlarkMethod(
      name = "configuration_field",
      // TODO(cparsons): Provide a link to documentation for available StarlarkConfigurationFields.
      doc =
          "References a late-bound default value for an attribute of type <a"
              + " href=\"attr.html#label\">label</a>. A value is 'late-bound' if it requires the"
              + " configuration to be built before determining the value. Any attribute using this"
              + " as a value must <a href=\"https://bazel.build/rules/rules#private-attributes\">be"
              + " private</a>. <p>Example usage: <p>Defining a rule attribute: <br><pre"
              + " class=language-python>'_foo':"
              + " attr.label(default=configuration_field(fragment='java', "
              + "name='toolchain'))</pre><p>Accessing in rule implementation: <br><pre"
              + " class=language-python>  def _rule_impl(ctx):\n"
              + "    foo_info = ctx.attr._foo\n"
              + "    ...</pre>",
      parameters = {
        @Param(
            name = "fragment",
            named = true,
            doc = "The name of a configuration fragment which contains the late-bound value."),
        @Param(
            name = "name",
            named = true,
            doc = "The name of the value to obtain from the configuration fragment."),
      },
      useStarlarkThread = true)
  LateBoundDefaultApi configurationField(String fragment, String name, StarlarkThread thread)
      throws EvalException;
}
