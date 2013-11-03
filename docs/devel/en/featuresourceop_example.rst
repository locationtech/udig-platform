FeatureSourceOp Example
~~~~~~~~~~~~~~~~~~~~~~~

IOps are included in the context menu in the Local Registry. Open Local Registry Dialog. Expand an
entry and right click on the child item. A menu will appear with a Count item. The count item is a
FeatureSourceOp and is the same Operation that is created by this tutorial. After this tutorial an
addition Count item should appear in the Menu, unless you change the name of the extension in the
plugin.xml.

This tutorial creates a simple FeatureSourceOp implementation that counts all the features in a
FeatureSource and displays the features in a dialog.

In a eclipse plugin the following lines must be added to the plugin.xml file:

**plugin.xml fragment**

::

    <extension
             point="org.locationtech.udig.registry.ui.featureType">
          <process
                class="xyz.examplepackage.CountOp"
                name="Count">
             <tooltip>
                Counts Features in the selection
             </tooltip>
          </process>
       </extension>

The xml fragment above declares an extension of the org.locationtech.udig.registry.ui.featureType.
The type of operation is a process operation (A child of the <extension> tag is the <process> tag).
The name of the process is Count and the operation class is xyz.examplepackage.CountOp. The
<tooltip> tag provides the plugin with a tooltip, this is optional.

Next the operation class must be defined:

**CountOp.java**

::

    public class CountOp extends FeatureSourceOp {

        /**
         * Counts the features in the feature source
         * 
         * @see org.locationtech.udig.registry.ui.FeatureSourceOp#op(org.geotools.data.FeatureSource)
         */
        public void op( FeatureSource source ) throws Exception {
            FeatureReader reader=source.getFeatures().reader();
            int i=0;
            while( reader.hasNext()){
                i++;
                reader.next();
            }
            reader.close();
            MessageDialog.openInformation(Display.getDefault().getActiveShell(),
                    "Feature Count", "Number of Features: "+i);
        }
    }

Once the plugin is exported (Export->Deployable plug-ins and features) into the uDig plugin
directory and uDig is started, the operation will be loaded.
