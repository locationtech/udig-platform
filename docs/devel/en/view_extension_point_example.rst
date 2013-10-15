View Extension Point Example
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Step 1:
^^^^^^^

Create a new plugin using the new plugin wizard.

.. figure:: /images/view_extension_point_example/NewPlugin.gif
   :align: center
   :alt: 

Step 2:
^^^^^^^

Ensure that the plugin depends on org.eclipse.core.runtime and org.eclipse.ui. This is done by
opening the plugin.xml file and adding them in the dependencies tab or by hand by adding:

.. figure:: /images/view_extension_point_example/dependencies.gif
   :align: center
   :alt: 

::

    <requires>
          <import plugin="org.eclipse.ui"/>
          <import plugin="org.eclipse.core.runtime"/>
       </requires>

Step 3:
^^^^^^^

Define a new view in the plugin.xml of a plugin using the form in the extensions tab of the
plugin.xml editor:

.. figure:: /images/view_extension_point_example/extensionsForm.gif
   :align: center
   :alt: 

The declaration of the new view can also be done by adding:

::

    <extension
             point="org.eclipse.ui.views">
          <view
                class="tutorial.ViewExample"
                id="tutorial.view1"
                name="tutorial.view1"/>
       </extension>

to the plugin.xml file. The example has defined a view that is has the name and id "tutorial.view1"
and the class that is used is "tutorial.ViewExample". That class will be created by the platform and
used to create the views contents.

Step 4:
^^^^^^^

Create the class "tutorial.ViewExample":

**ViewExample.java**

::

    package tutorial;

    import org.eclipse.swt.SWT;
    import org.eclipse.swt.widgets.Button;
    import org.eclipse.swt.widgets.Composite;
    import org.eclipse.ui.part.ViewPart;


    public class ViewExample extends ViewPart {

        private Button button;

        public ViewExample() {
            super();
        }
        
        @Override
        public void createPartControl(Composite parent) {
            button=new Button(parent,SWT.DEFAULT);
            button.setText("Button In New View");
        }

        @Override
        public void setFocus() {
            button.setFocus();
        }

    }

Checkout view in eclipse
^^^^^^^^^^^^^^^^^^^^^^^^

-  Right click on your new plugin and select Run As > Eclipse Application.
-  A fresh eclipse will open up. Select Menu item Windows > Show View > Other.
-  In folder "Other" select your new view (tutorial.view1)
-  Your view should open up and you can click you button (which does nothing currently).

.. figure:: /images/view_extension_point_example/running.gif
   :align: center
   :alt: 


