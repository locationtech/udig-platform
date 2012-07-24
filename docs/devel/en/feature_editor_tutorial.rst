Feature Editor Tutorial
=======================

Description of tutorial focused on goal.

.. figure:: /images/feature_editor_tutorial/FeatureEditTutorial.png
   :alt: 

This workbook is part of our commercial training materials.

-  See the main `uDig website <http://udig.refractions.net/users/>`_ for details on training and
   support
-  Commercial Training materials are available to Academic students on request

Source code:

-  Available in your uDig SDK (import the :doc:`source code <code_examples>` from the plugins
   view)
-  plugin:
   `net.refractions.udig.tutorials.featureeditor <https://github.com/uDig/udig-platform/tree/master/tutorials/net.refractions.udig.tutorials.featureeditor>`_
   (github)

Introduction
------------

When creating your own application, a common challenge is making a specific user interface for
editing your data. Out of the box uDig provides several generic feature editors (the Table view and
the Default Feature editor).

In this work book we will make our own Feature Editor. Our editor will only function on the
countries.shp file provided as part of the sample data set.

Eclipse RCP concepts covered in this workbook:

-  Creating a user interface using SWT widgets
-  Implementing a View
-  Implementing a Dialog

One thing to pay attention to is the use of Commands. Commands are created to interact with the uDig
application from a user interface. Commands dispatched to the GISPlatform are executed with write
access to the data model (of Maps, Layers and blackboards).

The Feature Editor we construct in this tutorial will issue several edit commands modifying a
feature that has been loaded onto the "Edit Blackboard" - this is the same feature being used by the
edit tools.

.. figure:: /images/feature_editor_tutorial/FeatureEditor.jpg
   :align: center
   :alt: 

What to Do Next
---------------

Okay, I am sure you can think of lots of things to try:

Eclipse WindowBuilder
^^^^^^^^^^^^^^^^^^^^^

Eclipse now provides an out of the box WindowBuilder; can you quickly draw up an alternate form of
this FeatureEditor?

MIGLayout
^^^^^^^^^

The MIG Layout manager is a very nice trade-off between readability and risk of run time errors. For
more information on the layout manager visit: `<http://www.miglayout.com/>`_

In particular the website documentation to get you started and a couple of JavaWebStart demo
applications.

FormLayout
^^^^^^^^^^

Can you redo the layout of this view using the Eclipse FormLayout? This layout manager makes the
opposite trade off - you have real Java data structures to fill in for your layout data.

Here is a small example to get you started.

.. code-block:: java

    parent.setLayout(new GridLayout(2, false));
        // SWT Widgets
     Label label = new Label(parent, SWT.SHADOW_IN);
        label.setLayoutData(new GridData(SWT.NONE, SWT.FILL));
        label.setText("Country:");
    MiGLayout has now added the use of java beans as a type safe options.
            name = new Text(parent, SWT.SHADOW_IN | SWT.BORDER);
            CC cc = new CC();
            cc.spanX(3);
            cc.growX();
            cc.wrap();
            name.setLayoutData( cc );
            name.addKeyListener(this);

You can use this technique to make custom forms for your own data using the full facilities of SWT
and Jface (say stars for restaurant reviews).

New to SWT
^^^^^^^^^^

If this is your first time using SWT try experimenting with the different Layouts:
 GridLayout, TableLayout, and FormLayout.

View Map Summary
^^^^^^^^^^^^^^^^

Advanced: You can make many Views - try making one that provides a summary of the Map. You should be
able to list the number of layers, and listen to events to notice when layers are added and removed.

(Hint: Look at EMF Notifier for very low level events beyond what the listeners provide)

Tips, Tricks and Suggestions
----------------------------

The following tips have been provided by the udig-devel list; please stop by and introduce yourself.

Is there a form builder?
^^^^^^^^^^^^^^^^^^^^^^^^

Eclipse includes WindowBuilder which you can use to visually layout your form.

* `http://www.eclipse.org/windowbuilder/ <http://www.eclipse.org/windowbuilder/>`_

Commercial Training Materials
-----------------------------

Please contact any of the organisations listed on the main `uDig support
page <http://udig.refractions.net/users/>`_ for details on uDig training.

The workbooks and slides for the training course are available here:

* `http://svn.refractions.net/udig\_training/trunk <http://svn.refractions.net/udig_training/trunk>`_

This is a private svn repository that is open to those who have taken the training course.

Academic Access
^^^^^^^^^^^^^^^

The course materials can be made available to those working at academic institutions - we ask for an
email from your Professor.

Please ask your professor to email admin@refractions.net with the request.
