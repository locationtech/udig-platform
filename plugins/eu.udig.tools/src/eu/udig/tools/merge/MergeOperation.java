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

    private MergeView mergeView;

    private List<SimpleFeature> selectedFeatures;

    @Override
    public void op(Display display, Object target, IProgressMonitor monitor) throws Exception {
        
        this.mergeContext = MergeContext.getInstance();
        //this.mergeContext.setToolContext(getContext());
        openMergeView(100, 100, mergeContext);

        //Get the selected layer
        ILayer selectedLayer = ApplicationGIS.getActiveMap().getEditManager().getSelectedLayer();
        //Get the filter actively working on it (that is the selection)
        Filter filterSelectedFeatures = selectedLayer.getFilter();
        //Turn the filter into a List of features
        selectedFeatures = Util.retrieveFeatures(filterSelectedFeatures, selectedLayer);
        
        mergeView = mergeContext.getMergeView();

        display.asyncExec(new Runnable(){ // <<<== Throws NullPointerException
            
            public void run() {
                try {                   
                    //mergeView = (MergeView) ApplicationGIS.getView(true, MergeView.ID);
                    
                    mergeView.addSourceFeatures(selectedFeatures);
                    
                    mergeView.display();
                    
                }
                    catch (Exception ex) {
                    ex.printStackTrace();
                }
        }});   
    }

    /**
     * Opens the Merge view
     * 
     * @param eventX
     * @param eventY
     * @param context
     */
    private void openMergeView(int eventX, int eventY, MergeContext mergeContext) {

        MergeView view = null;    
        try {
            view = (MergeView) ApplicationGIS.getView(true, MergeView.ID);
            if (view == null) {
                // crates a new merge view
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getActivePage();
                view = (MergeView) page.findView(MergeView.ID);
            }
            assert view != null : "view is null"; //$NON-NLS-1$

            // associates this the merge view with the merge context
            view.setMergeContext(mergeContext);
            mergeContext.activeMergeView(view);

        } catch (Exception ex) {
            /*AnimationUpdater.runTimer(getContext().getMapDisplay(), new MessageBubble(eventX,
                    eventY, "It cannot be merge", //$NON-NLS-1$
                    PreferenceUtil.instance().getMessageDisplayDelay()));*/
        }
    }

}
