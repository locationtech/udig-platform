Naming Conventions
==================

Welcome to the next level, naming stuff. Naming is one of the hardest things to do when developing
and the Eclipse project has offered us the following guidelines.

Conventions from Eclipse Plug-in Developers Guide
-------------------------------------------------

The following package name segments are reserved:

*  internal - indicates an internal implementation package that contains no API
*  tests - indicates a non-API package that contains only test suites
*  examples - indicates a non-API package that contains only examples

These name are used as qualifiers, and must only appear following the major package name. For
example,

*  org.eclipse.core.internal.resources - Correct usage
*  org.eclipse.internal.core.resources - Incorrect. internal precedes major package name.
*  org.eclipse.core.resources.internal - Incorrect. internal does not immediately follow major
   package name.

They also have a convention of seperating out model from ui, so ui plugins get a **.ui** package
name segment.

There is an example from udig:

*  org.locationtech.udig (model or core)
*  org.locationtech.udig.ui (user interface)

As a clue anything that is *core* can run in headless mode.

Related reference
-----------------

* :doc:`api_rules_of_engagement`
* `Naming Conventions (eclipse.org) <http://help.eclipse.org/galileo/topic/org.eclipse.platform.doc.isv/reference/misc/naming.html>`_

