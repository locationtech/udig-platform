package eu.udig.tools.merge;

import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ui.AnimationUpdater;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.tool.ModalTool;
import net.refractions.udig.tools.edit.animation.MessageBubble;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

import eu.udig.tools.merge.internal.view.MergeView;

public class MergeOperation implements IOp {

    private MergeContext mergeContext;

    private MergeView mergeView = new MergeView();

    private List<SimpleFeature> selectedFeatures;

    @Override
    public void op(Display display, Object target, IProgressMonitor monitor) throws Exception {

        //Get the selected layer
        ILayer selectedLayer = ApplicationGIS.getActiveMap().getEditManager().getSelectedLayer();
        //Get the filter actively working on it (that is the selection)
        Filter filterSelectedFeatures = selectedLayer.getFilter();
        //Turn the filter into a List of features
        selectedFeatures = Util.retrieveFeatures(filterSelectedFeatures, selectedLayer);
        
        mergeView = null;
        
        display.asyncExec(new Runnable(){
            public void run() {
                do
                {
                    try {
                        //slow down looping
                        Thread.sleep(3000);
                        System.out.print("Waiting for mergeView to be not null/r");
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    //Just loop
                    //System.out.print("Waiting for mergeView to be not null/n");
                } while (mergeView == null);
                
                mergeView.addSourceFeatures(selectedFeatures);
                
                mergeView.display();
            }
        });
        
        display.asyncExec(new Runnable(){ // <<<== Throws NullPointerException
            
            public void run() {
                try {                   
                    mergeView = (MergeView) ApplicationGIS.getView(true, MergeView.ID);
                }
                    catch (Exception ex) {
                    ex.printStackTrace();
                }
        }});
        
        
        // NO MORE ACTIIVE CODE BEYOND THIS POINT: just cut-n-paste playground
        
        
        /*
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        
        if (activePage.findView(MergeTool.ID) == null) {
        try {
        // open the view
        activePage.showView(MergeTool.ID);
        // and maximize it
        activePage.toggleZoom(activePage.findViewReference(MergeTool.ID));
        } catch (PartInitException es) {
        es.printStackTrace();
        }
        }
        */
        
        // ModalTool mergeTool = (ModalTool) ApplicationGIS.getToolManager().findTool(MergeTool.ID);
        // mergeTool.setActive(true);
        
                
        //mergeView.createPartControl
        
        //this.mergeContext = MergeContext.getInstance();
        
        // Look at the current shell and up its parent
        // hierarchy for a workbench window.
        
        //IWorkbenchPage page = myGetWorkbenchWindow(display).getActivePage();
        // Look for the window that was last known being
        // the active one
        /*
        WorkbenchWindow win = getActivatedWindow();
        if (win != null) {
                return win;
        }
        */
        
        // IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        
        /*
        
        // Look at the current shell and up its parent
        // hierarchy for a workbench window.
        Control shell = display.getActiveShell();
        while (shell != null) {
                Object data = shell.getData();
                if (data instanceof IWorkbenchWindow) {
                        return (IWorkbenchWindow) data;
                }
                shell = shell.getParent();
        }

        // Look for the window that was last known being
        // the active one
        WorkbenchWindow win = getActivatedWindow();
        if (win != null) {
                return win;
        }

        // Look at all the shells and pick the first one
        // that is a workbench window.
        Shell shells[] = display.getShells();
        for (int i = 0; i < shells.length; i++) {
                Object data = shells[i].getData();
                if (data instanceof IWorkbenchWindow) {
                        return (IWorkbenchWindow) data;
                }
        }
        */
        
        /*
        try {
            
            mergeView = (MergeView) ApplicationGIS.getView(true, MergeView.ID);
            if (mergeView == null) {
                // crates a new merge view
                //IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                mergeView = (MergeView) page.findView(MergeView.ID);
            }
            assert mergeView != null : "view is null"; //$NON-NLS-1$

            // associates this the merge view with the merge context
            mergeView.setMergeContext(mergeContext);
            mergeContext.activeMergeView(mergeView);
            
            mergeView = new MergeView();
            //tW.
            
            mergeView.addSourceFeatures(selectedFeatures);

            mergeView.display();
              
        } catch (Exception ex) {
            System.out.print("Error: "+ex.toString());
        }
         */

        

    }
}
