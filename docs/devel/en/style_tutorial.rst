Style Tutorial
==============

Control the rendering process by adding a custom style for the style blackboard.

.. figure:: /images/style_tutorial/StyleTutorial.png
   :alt: 

This workbook is part of our commercial training materials.

-  See the main `uDig website <http://udig.refractions.net/users/>`_ for details on training and
   support
-  Commercial Training materials are available to Academic students on request

Source code:

-  Available in your uDig SDK (import the :doc:`source code <code_examples>` from the plugins
   view)
-  plugin:
   `org.locationtech.udig.tutorials.style.color <https://github.com/uDig/udig-platform/tree/master/tutorials/org.locationtech.udig.tutorials.style.color>`_
   (github)

Introduction
------------

In this workbook we are going to create our own style content; and use it to control the CSVRenderer
created earlier.

We will be covering the following ideas:

-  styleContent - used to host a value on the style blackboard
-  stylePage - contributed to the style editor allowing the user to change values on the style
   blackboard
-  IMemento - eclipse interface used to advertise persistent storage

We will also revisit the RenderingMetrics contract covered in the last tutorial.

What to Do Next
---------------

For better understanding of these facilities please try the following:

Switch
^^^^^^

Can you arrange to switch between the ColorCSVRenderer and the CSVRenderer? What code do you need to
add to make this happen?

Hint: Consider what is going on with the style blackboard

User supplied Default Color
^^^^^^^^^^^^^^^^^^^^^^^^^^^

Can you modify the code to read in a default color stored in a CSV file comment?

::

    # ff00cc

SLD
^^^

Advanced: The org.locationtech.udig.style.sld plug-in holds a GeoTools FeatureTypeStyle object on the
blackboard using the key SLDStyle.ID.

Adjust your ColorCSVRenderer to recognize **SLDStyle.ID** as well.

Hint: Use the SLD utility class to obtain a color from the first PointSymbolizer.

Hint: You should be able to drag and drop an sld file onto the layer to have it stored on the
blackboard. If you do not have an sld - open up a point layer and visit the advanced page of the
style editor. You can export this xml as an "sld" document.

Tips, Tricks and Suggestions
----------------------------

The following tips have been provided by the udig-devel list; please stop by and introduce yourself.

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
