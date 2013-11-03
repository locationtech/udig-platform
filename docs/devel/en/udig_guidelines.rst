uDig Guidelines
~~~~~~~~~~~~~~~

The uDig application is guided by a small number of general principles. These are simply guidelines
and are not required, any plug-in that becomes part of the core uDig project should consider these
points.

Guidelines
~~~~~~~~~~

**Sensible Defaults**

-  This is a User-friendly application, make the right choices rather than complicate matters.

The idea here is to optimize for the 90% use case; it is okay have to more options available for
"advanced" functionality, but please make them just that - optional.

**Code for Convenience**

-  principle of "least surprise" applies to developers as well as users

This is a developer-friendly platform, designed to be extended by others.

**Code like you mean it**

-  if you change your mind change your code

This project is commercial, ie always on a short timeline, don't waste time in "Analysis Paralysis".
You have better things to do. There is time enough for the code base to be entrenched later

**No API Before Its Time**

Additional plug-ins and functionality can be added at the request of the developer community (this
means you). We try not to add any functionality without a solid use-case from a real developer,
preferably with a running example in a community module.

If what you are looking for is not here - please ask on the developer list.

We Trust You
~~~~~~~~~~~~

You may look at the above list and think "Those are not Guidelines", and you are right. We trust you
on the coding side of things.

We don't want to waste your time, or ours, deciding where spaces should go we have tools for that.

**Use the Tools to keep things Sane**

-  Use the **code template** to supply file headers and decide
-  Use the **code formatter** to decide where the spaces go
-  Please use JUnit rather than test everything by Hand
-  Please run FindBugs it will perform static analysis and find those NPE for you

You are a Java developer right? These are best practices.

**Using Find Bugs**

You can run FindBugs on each plug-in when it is committed (FindBugs is included in the extras pack).

To run FindBugs:

#. Right clicking on your project
#. Selecting **Find Bugs > Find Bugs**.
#. It will add a few more warnings for you to review in the "Problems" view

**Provide warnings to Developers when you have to Guess**

When you make an **extention point** for other developers to use we have a small favour to ask:

-  When you are forced to use a default (because a developer did not give you a real value) please
   provide a warning. This way the developers know that they **can** provide their own value (or
   implementation, etc.)

