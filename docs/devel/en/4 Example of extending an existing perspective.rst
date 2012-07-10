4 Example of extending an existing perspective
==============================================

Extending an Existing Perspective
---------------------------------

This tutorial continues the `3 Example of creating a
view <3%20Example%20of%20creating%20a%20view.html>`_ tutorial, it assumes that a view and a plugin
have both be created.

Declare Extension in plugin.xml:
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

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

.. figure:: /images/4_example_of_extending_an_existing_perspective/perspectiveExtension.gif
   :align: center
   :alt: 

Run Application
~~~~~~~~~~~~~~~

Create a new Application Launcher by doing the following:

-  Right click on your new plugin and select Run As > Eclipse Application.
-  Your view should open up and you can click you button (which does nothing currently).
-  Close the intro page

The workbench should now look similar to the following image:

.. figure:: /images/4_example_of_extending_an_existing_perspective/running.gif
   :align: center
   :alt: 


