Testing Tutorial
================

Bit of Eclipse RCP review showing how to set up tests for your plugin.

.. figure:: /images/testing_tutorial/TestingWorkbook.png
   :align: center
   :alt: 

This workbook is part of our commercial training materials.

-  See the main `uDig website <http://udig.refractions.net/users/>`_ for details on training and
   support
-  Commercial Training materials are available to Academic students on request

Source code:

-  Available in your uDig SDK (import the :doc:`source code <code_examples>` from the plugins
   view)
-  plugin: `net.refractions.udig.tests.tutorials.tests <https://github.com/uDig/udig-platform/tree/master/tutorials/net.refractions.udig.tests.tutorials.tests>`_ (github)

Introduction
------------

In this workbook we are going to look into the testing requirements of a plug-in. Eclipse has a
special JUnit "TestRunner" that can be used to start up a slaved copy of your application; run some
tests; and report back.

This way of testing takes longer than a normal quick JUnit TestCase and as such should be reserved
for Integration Testing; that is make a TestSuite that runs all your Plug-in Tests in one go.

This should not prevent you from writing and using normal JUnit testing for your domain model; data
structures and utility classes. This extra level of testing out plug-ins should strictly be for
integration tests; in normal use:

-  Run your JUnit Tests to confirm you fixed a bug
-  Run your Integration TestSuite before you commit

You can ask the tests to be run as part of the process of making a custom application.

What to Do Next
---------------

Here are some additional challenges for you to try.

TestSuite
`````````

Create a TestSuite; this is how you will start up uDig once and run a series of integration tests.

Faster Testing
``````````````

Right now your test case is loading far more plug-ins then it actually needs; open up Run Dialog and
turn off as many as you can - your test will run much faster.

Keep scrolling down into the Target Platform plug-ins you are not using a lot of this stuff; turn it
off.

Really Fast Testing
```````````````````

Write a (normal) JUnit test for our CSV class; you can run this as a JUnit test without the need to
start a copy of uDig.

Headless
````````

This is a bit more of a research topic; how can you run your tests "headless" (without a monitor).
This is often a requirement of build systems such as Hudson / Jenkins / CurseControl.

Tips, Tricks and Suggestions
----------------------------

The following tips have been provided by the udig-devel list; please stop by and introduce yourself.

What is JUnit
`````````````

JUnit is a unit testing library that is well adopted by the Java development community.

For more information:

* `http://www.junit.org/ <http://www.junit.org/>`_
* `JUnit Cookbook <http://junit.sourceforge.net/doc/cookbook/cookbook.htm>`_

Originally the work of Kent Beck the concept of an "xUnit" testing library was an early object
oriented training exercise.

My Run Configuration Has too much stuff
```````````````````````````````````````

By default when you right click on a test and say run as plugin test it creates a "Run
Configuration" that includes **everything** in your workspace!

Here is a good way to update your run configuration to the minimal number of plugins needed to run
your test:

.. figure:: /images/testing_tutorial/TestRunConfiguration.jpg
   :align: center
   :alt: 

Commercial Training Materials
-----------------------------

Please contact any of the organisations listed on the main `uDig support page <http://udig.refractions.net/users/>`_ 
for details on uDig training.

The workbooks and slides for the training course are available here:

* `http://svn.refractions.net/udig\_training/trunk <http://svn.refractions.net/udig_training/trunk>`_

This is a private svn repository that is open to those who have taken the training course.

Academic Access
```````````````

The course materials can be made available to those working at academic institutions - we ask for an
email from your Professor.

Please ask your professor to email admin@refractions.net with the request.
