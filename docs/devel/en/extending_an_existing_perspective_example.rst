Extending An Existing Perspective Example
#########################################

Extending an existing perspective
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

This example continues the `View Extension Point
Example <View%20Extension%20Point%20Example.html>`_, it assumes that a view and a plugin have both
be created.

Declare Extension in plugin.xml:
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Edit the plugin.xml file of your plugin. Add a extension to the org.eclipse.ui.perspectiveExtensions
extension point. The following snippet of xml should be added to the plugin.xml file:

::

    <extension
             point="org.eclipse.ui.perspectiveExtensions">
          <perspectiveExtension targetID="org.eclipse.ui.resourcePerspective">
             <view
                   id="tutorial.view1"
                   relationship="stack"
                   relative="org.eclipse.ui.views.ResourceNavigator"/>
          </perspectiveExtension>
       </extension>

The extensions tab of the plugin.xml editor should now look similar to the following image:

.. figure:: /images/extending_an_existing_perspective_example/perspectiveExtension.gif
   :align: center
   :alt: 

Run Application
^^^^^^^^^^^^^^^

Create a new Application Launcher by doing the following:

-  Right click on your new plugin and select Run As > Eclipse Application.
-  Your view should open up and you can click you button (which does nothing currently).
-  Close the intro page

The workbench should now look similar to the following image:

.. figure:: /images/extending_an_existing_perspective_example/running.gif
   :align: center
   :alt: 


