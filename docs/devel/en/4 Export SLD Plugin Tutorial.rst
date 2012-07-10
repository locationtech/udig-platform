4 Export SLD Plugin Tutorial
============================

Export SLD Operation Plugin Tutorial
====================================

Goals
-----

After completing this tutorial, you will have gained the skills to:

-  Create a new Plugin
-  Define a new Extension
-  Implement a new Operation Extension

Create a New Plugin
===================

#. Open the Plug-in Development perspective (click on |image0|)
#. From the File menu, select **New > Projectâ€¦**. Select Plug-in Project from the dialog, and then
   click the Next button.
    |image1|
    *Figure 1 - New Plug-in Project*
#. Create a name for the plug-in by entering **net.refractions.udig.sld.export** in the **Project
   Name** text field and click the **Next** button.
    |image2|
    *Figure 2 - Naming the New Plug-in*
#. Accept the default values used to generate the plug-in and click the Finish button.
    |image3|
    *Figure 3 - New Plug-in Details*
    At this point the feature editor plug-in is created. Configuring the plug-in is the focus of the
   next section.

Configuring Your New Plug-in
----------------------------

In this section you will configure the feature editor plug-in. Specifically, you will specify
dependencies on other plug-ins in order to create a new feature editor.

#. Open the Plug-in Development perspective.
#. In the Package Explorer navigate to the plug-in created in the previous section. Open the plug-in
   manifest by navigating to the META-INF/MANIFEST.MF file under the root of the feature editor
   plug-in. Double click on the file to open the plug-in manifest editor.
    |image4|
    *Figure 4 - Plug-in Manifest Editor*
#. Open the plug-in dependencies by clicking on the Dependencies tab located at the bottom of the
   editor area.
#. Click the Addâ€¦ button in the Required plugins column and add the following plugin:

-  **net.refractions.udig.project**
-  **net.refractions.udig.project.ui**
-  **net.refractions.udig.style.sld**
-  **net.refractions.udig.udig.ui**
    |image5|
    *Figure 5 - Add New Plugin Dependencies*

#. At this point it is critical that you |image6| **Save** your work as the dependencies need to
   propagate into the project.
    |image7|
    *Figure 6 - Plug-in Dependencies*

Import Resources Into Project
-----------------------------

In this section we are going to set up an icon directory and import the pictures we will use for our
export SLD operation.

#. Download [^] and [^] to your desktop (Right click and **Save Link As...**)
#. Select your plug-in project, **net.refractions.udig.tutorials.distancetool**, in the **Package
   Explorer**.
#. Select File â†' New â†' Folder from the Menubar.
#. Enter **icons/etool16** as the folder name.
#. Click the Finish button.
#. Select **icons** directory.
#. Select File â†' New â†' Folder from the Menubar.
#. Enter **pointers** as the folder name.
#. Click the Finish button.
    |image8|
#. Right click on etool16 and select **Import**.
#. Select File System.
    |image9|
#. Click on browse and choose Desktop from the list (this will populate the directory field).
#. Select the **measure\_mode.gif** file and press Finish.
    |image10|
#. Import the **measure\_source.gif** file into the **pointers** directory following the same steps.

Define a New Extension
----------------------

#. Open the extensions page by clicking on the **Extensions** tab

#. Click the Addâ€¦ button
#. Select the **net.refractions.udig.ui.operation** extension point from the list.
    |image11|
#. Click the **Finish** button.
#. Enter the following Extention Details:

   -  ID: *net.refractions.udig.sld.export*
   -  Name: *SLD Export to File*
       |image12|

Create a New Operation
----------------------

#. Right click on newly added extension, **net.refractions.udig.ui.operation**, and select **New >
   Operation**
#. Replace the default data in the id field with
   **net.refractions.udig.style.sld.export.ExportSLD**.
#. Enter **net.refractions.udig.sld.export.ExportSLD** into the class field.
#. Enter **icons/etool16/** into the icon field.
    (Or press the Browse button and locate the icon)
#. Enter **Export SLD** into the name field.
#. Enter **net.refractions.udig.project.ui.exportOps** into the categoryId field.
    |image13|

Implementing a export operation
===============================

#. Select net.refractions.udig.style.sld.export.ExportSLD (operation) in the Extensions editor.
#. It is a child of the net.refractions.udig.style.sld.export.
#. Click the class hotlink.

#. A dialog is brought up describing the class to be created, Check Generate comments and Inherited
   abstract methods.
    |image14|
#. Press **Finish**, if not available ensure that all the information is in agreement with the
   picture above.

Add the following code to your created class

::

    public class ExportSLD implements IOp {

        public class QueryAndSave implements Runnable {

            private String out;
            private Layer layer;
            private IProgressMonitor monitor;
            private Display display;

            public QueryAndSave( Layer layer, String out, Display display, IProgressMonitor monitor ) {
                this.layer=layer;
                this.out=out;
                this.display=display;
                this.monitor=monitor;
            }
            File file;
            public void run() {
                do{
                    if (!getFile())
                        return;
                    boolean write=true;
                    if( file.exists() ){
                    write=MessageDialog.openConfirm(display.getActiveShell(), Messages.getString("ExportSLD.saveAs"),  //$NON-NLS-1$
                                file.getAbsolutePath()+Messages.getString("ExportSLD.exists") + //$NON-NLS-1$
                                        Messages.getString("ExportSLD.replace")); //$NON-NLS-1$
                    }
                    
                    if( write ){
                    try {
                        FileWriter writer=new FileWriter(file, false);
                        writer.write(out);
                        writer.close();
                    } catch (Exception e) {
                        file=null;
                        MessageDialog.openError(display.getActiveShell(), Messages.getString("ExportSLD.saveAs"),//$NON-NLS-1$
                                Messages.getString("ExportSLD.modifyError")); //$NON-NLS-1$
                    }
                    }else{
                        file=null;
                    }
                }while( file==null );
            }

            private boolean getFile() {
                FileDialog fileDialog=new FileDialog(display.getActiveShell(),SWT.SAVE);
                fileDialog.setFilterExtensions(new String[]{"*.sld"}); //$NON-NLS-1$
                fileDialog.setFilterNames(new String[]{Messages.getString("ExportSLD.SLD")}); //$NON-NLS-1$
                String name=layer.getName();
                if( name==null ){
                    try {
                        name=layer.getGeoResource().getInfo(monitor).getTitle();
                    } catch (IOException e) {
                        ExportPlugin.log("error getting name from layer's georesource", e); //$NON-NLS-1$
                    }
                    if( name==null ){
                        try {
                            name=layer.getGeoResource().getInfo(monitor).getName();
                        } catch (IOException e) {
                            ExportPlugin.log("error getting name from layer's georesource", e); //$NON-NLS-1$
                        }
                    }
                }
                 if( name!=null)
                     fileDialog.setFileName(name+".sld"); //$NON-NLS-1$
                String path=fileDialog.open();
                if( path==null)
                    return false;
                file=new File(path);
                return true;
            }
        }
        public void op( Display display, Object target, IProgressMonitor monitor ) throws Exception {
            Layer layer=(Layer) target;
            Style style=(Style) layer.getStyleBlackboard().get(SLDContent.ID);

            // serialize out the style objects
            SLDTransformer sldWriter = new SLDTransformer();
            String out = ""; //$NON-NLS-1$
            try {
                out = sldWriter.transform(style);
            } catch (TransformerException e) {
                ExportPlugin.log(null, e);
                e.printStackTrace();
            } catch (Exception e) {
                ExportPlugin.log(null, e);
            }
            display.asyncExec(new QueryAndSave(layer, out, display, monitor));
        }
    }

#. Click the **OK** button.

#. 

   #. And then **save** the file. This should refresh the project and clear up any error markers
      left behind.

.. figure:: images/icons/emoticons/check.gif
   :align: center
   :alt: 

**Optional**

You may check your work against the completed plugin code available here:

#. Download:
   :doc:`net.refractions.udig.sld.export\_1.0.0.jar`

#. Save in your udig plugins directory.
#. Right clicking on any layer will now let you export a SLD file

   :doc:`net.refractions.udig.sld.export\_1.0.0.jar`

   archive file.
    This file is suitable for use with GeoServer.

Source code Avaiable here:

:doc:`http://svn.geotools.org/udig/trunk/community/jesse/net.refractions.udig.sld.export/`


svn co
:doc:`http://svn.geotools.org/udig/trunk/community/jesse/net.refractions.udig.sld.export/`


Testing The Plug-in
===================

#. From the Project menu select **Run** and choose the configuration you set-up in the previous
   tutorial (see `1 SDK Quickstart <1%20SDK%20Quickstart.html>`_).

.. |image0| image:: /images/4_export_sld_plugin_tutorial/0Prespective.gif
.. |image1| image:: /images/4_export_sld_plugin_tutorial/1NewProject.png
.. |image2| image:: /images/4_export_sld_plugin_tutorial/2NewPluginProject.png
.. |image3| image:: /images/4_export_sld_plugin_tutorial/3NewPluginContent.png
.. |image4| image:: /images/4_export_sld_plugin_tutorial/4PluginManifest.png
.. |image5| image:: /images/4_export_sld_plugin_tutorial/5AddDependencies.png
.. |image6| image:: /images/4_export_sld_plugin_tutorial/save.gif
.. |image7| image:: /images/4_export_sld_plugin_tutorial/6PluginDependencies.png
.. |image8| image:: /images/4_export_sld_plugin_tutorial/7NewFolder.png
.. |image9| image:: /images/4_export_sld_plugin_tutorial/8Import.png
.. |image10| image:: /images/4_export_sld_plugin_tutorial/9ImportToolIcon.png
.. |image11| image:: /images/4_export_sld_plugin_tutorial/10Extention.png
.. |image12| image:: /images/4_export_sld_plugin_tutorial/11NewExtention.png
.. |image13| image:: /images/4_export_sld_plugin_tutorial/12ExtentionDetails.png
.. |image14| image:: /images/4_export_sld_plugin_tutorial/15NewClassWizard.png
