Edit Tool Example
~~~~~~~~~~~~~~~~~

This tutorial demonstrates how to make a simple custom edit tool. The tool will select features and
change the geometry of the feature into a star.

Requirements for tutorial
^^^^^^^^^^^^^^^^^^^^^^^^^

Installed eclipse and uDig SDK 1.1 - `SDK Quickstart <SDK%20Quickstart.html>`_
 Completion of the Plugin tutorial - `Tool Plugin Tutorial <Tool%20Plugin%20Tutorial.html>`_

What is a Edit Tool?
^^^^^^^^^^^^^^^^^^^^

Edit tools are another sub-type of tools, however they do not (yet) have their own extension point.
Instead they are extensions of the net.refractions.udig.project.ui.tool extension point. Because
there are a large number of editing tools that can be created an many of them have similar
functionality there is a little framework associated with edit tools development. The design and
more detailed discussion of the edit tool framework can be found at `Edit
Tools <Edit%20Tools.html>`_

Tutorial
^^^^^^^^

1. Create a new Plugin called: **net.refractions.udig.tutorials.tool.star**
 2. Configure your new created plugin

#. Open the Plug-in Development perspective.
#. In the Package Explorer navigate to the plug-in created in the previous section. Open the plug-in
   manifest by navigating to the META-INF/MANIFEST.MF file under the root of the feature editor
   plug-in. Double click on the file to open the plug-in manifest editor.
#. Open the plug-in dependencies by clicking on the Dependencies tab located at the bottom of the
   editor area.
#. Click the Add button in the Required plugins column and add the following plugin:

-  net.refractions.udig.project.ui,
-  net.refractions.udig.ui

#. At this point it is critical that you **Save** your work as the dependencies need to propagate
   into the project.

Import Resources Into Project

Define a New Extension

#. Open the extensions page by clicking on the **Extensions** tab
#. Click the Add button
#. Select the **net.refractions.udig.project.ui.tool** extension point from the list.
#. Click the **Finish** button.
#. Enter the following Extention Details:

   -  ID: *net.refractions.udig.tutorials.tool.star*
   -  Name: *Star Geometry Tool Example*

Create a New Tool

#. Right click on newly added extension, **net.refractions.udig.project.ui.tool**, and select **New
   > modalTool**
#. Replace the default data in the id field with **net.refractions.udig.tutorials.tool.star**.
#. Enter a tool tip message into the tooltip field: **Changes Geometry to Star**
#. Enter **net.refractions.udig.tutorials.tool.move.StarTool** into the class field.
#. Enter **icons/etool16/** into the icon field.
    (Or press the Browse button and locate the icon)
#. Enter **Move** into the name field.
#. Set onToolbar to **true**.
#. Enter **net.refractions.udig.tool.edit.create** into the categoryId field. This ensures that the
   tool will be created in the create Tool category.

