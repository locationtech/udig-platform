Layer Summary Operation
=======================

Layer Summary Operation
-----------------------

Plugin.xml extension declaration
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

::

    <extension
       point="net.refractions.udig.ui.operation">
       <operation
          targetClass="net.refractions.udig.project.ILayer"
          class="net.refractions.udig.project.ui.LayerSummary"
          categoryId="net.refractions.udig.project.ui.informationOperations"
          name="Layer Summary"
          id="net.refractions.udig.project.ui.Operation1"/>
       <category
          name="Information"
          id="net.refractions.udig.project.ui.informationOperations"/>
    </extension>

The **targetClass** indicates which type of objects the operation can operate on. The an instance of
the targetClass will be passed to the operation as one of the parameters of the op(...) method.
 The **class** declares the operation class. It must implement the IOp interface.
 The **categoryId** declares which category the operation is part of. This determines where in the
operations menu the operation is located.

The **category** declaration declares a new category called Information. If there are any operations
in the category the category will appear in all the Operations menus.

Layer Summary Class
^^^^^^^^^^^^^^^^^^^

**LayerSummary.java**

::

    /**
     * Displays a summary of the layer in a dialog.
     * 
     * @author jeichar
     * @since 0.6.0
     */
    public class LayerSummary implements IOp {
        /**
         * @see net.refractions.udig.ui.operations.IOp#op(org.eclipse.swt.widgets.Display,
             * java.lang.Object, org.eclipse.core.runtime.IProgressMonitor)
         */
        public void op(final Display display, Object target, IProgressMonitor monitor) throws Exception {
               final Layer layer=(Layer) target;
           monitor.beginTask("Layer Summary", 1);
               Envelope bounds=layer.getBounds(monitor, layer.getMap().getViewportModel().getCRS());
               final StringBuffer buffer=new StringBuffer();
               buffer.append("Name: "+layer.getName()+"\n");
               buffer.append("ID: "+layer.getID()+"\n");
               buffer.append("z-order: "+layer.getZorder()+"\n");
               buffer.append("Data CRS: "+layer.getCRS(monitor).getName()+"\n");
               buffer.append("Bounds: ("+bounds.getMinX()+","+bounds.getMinY()+")\n");
               buffer.append("            ("+bounds.getMaxX()+","+bounds.getMaxY()+"\n");
               buffer.append("Selection Filter: "+layer.getFilter()+"\n");
               display.asyncExec(new Runnable() {
                public void run() {
                    MessageDialog.openInformation(display.getActiveShell(), "Summary of "+
                                   layer.getName(), buffer.toString());
                }
            });
           monitor.internalWorked(1);
           monitor.done();
            }

    }

The parameter **display** is a display object the operation can use in order to interact with the
User interface. The **target** parameter is guaranteed to be of the same type declared in the
targetClass attribute of the xml extension declaration. The **monitor** parameter allows the
operation to provide feedback to the UI on how much of the operation has been completed.
