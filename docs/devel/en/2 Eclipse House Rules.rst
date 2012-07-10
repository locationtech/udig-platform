2 Eclipse House Rules
=====================

Finally we have gather the **Eclipse House Rules** into one location. These are actual rules that
must be followed by any Eclipse plug-in.

:doc:`Extender Rules`


* :doc:`Contribution Rule`

* :doc:`Conformance Rule`

* :doc:`Sharing Rule`

* :doc:`Monkey See Monkey Do Rule`

* :doc:`Relevance Rule`

* :doc:`Integration Rule`

* :doc:`Responsibility Rule`

* :doc:`Program To API Contract Rule`

* :doc:`Other Rule`

* :doc:`Adapt to IResource Rule`

* :doc:`Strata Rule`

* :doc:`User Continuity Rule`


:doc:`Enabler Rule`


* :doc:`Invitation Rule`

* :doc:`Lazy Loading Rule`

* :doc:`Safe Platform Rule`

* :doc:`Fair Play Rule`

* :doc:`Explicit Extension`

* :doc:`Diversity Rule`

* :doc:`Good Fences`

* :doc:`User Arbitration Rule`

* :doc:`Explicit API Rule`

* :doc:`Stability Rule`

* :doc:`Defensive API Rule`


:doc:`Publisher`


* :doc:`License Rule`


The Eclipse Hourse rules, are rather hard to track down (although they are referred to often). The
best source is the "Contributing to Eclipse" book mentioned on the project reading list.

Extender Rules
==============

These are rules that you **must** follow when implementing any extension point.

Contribution Rule
-----------------

Everything is a contribution

Conformance Rule
----------------

Contributions must conform to expected interfaces

Sharing Rule
------------

Add, don't replace

Monkey See Monkey Do Rule
-------------------------

Always start by copying the structure of a similar plug-in

Relevance Rule
--------------

Contribute only when you can successfully operate

Integration Rule
----------------

Integrate, don't separate

Responsibility Rule
-------------------

Clearly identify your plug-in as the source of problems

Program To API Contract Rule
----------------------------

Check and program to the Eclipse API contract

Other Rule
----------

Make all contributions available, but put those that don't typically apply to the current
perspective in an Other... dialog

Adapt to IResource Rule
-----------------------

Whenever possible, define an IResource adapter for your domain objects

Strata Rule
-----------

Separate language-neutral functionality from language-specific functionality and separate core
functionality from UI functionality

User Continuity Rule
--------------------

Preserve the user interface state across sessions

Enabler Rule
============

These are rules that **must** be followed when providing an extension point for others developers to
implement.

Invitation Rule
---------------

Whenever possible, let others contribute to your contributions

Lazy Loading Rule
-----------------

Contributions are only loaded when they are needed

Safe Platform Rule
------------------

As the provider of an extension point, you must protect yourself against misbehavior on the part of
extenders

Fair Play Rule
--------------

All clients play by the same rules, even me

Explicit Extension
------------------

Declare explicitly where a platform can be extended

Diversity Rule
--------------

Extension points accept multiple extensions

Good Fences
-----------

When passing control outside your code, protect yourself

User Arbitration Rule
---------------------

When there are multiple applicable contributions, let the user decide
 which one to use

Explicit API Rule
-----------------

separate the API from internals

Stability Rule
--------------

Once you invite someone to contribute, don?t change the rules

Defensive API Rule
------------------

Reveal only the API in which you are confident, but be prepared to reveal more API as clients ask
for it

Publisher
=========

When you decide to publish your plugin (say as a community module) .

License Rule
------------

Always supply a license with every contribution
