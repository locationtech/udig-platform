package eu.udig.tools.merge;

import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ui.AnimationUpdater;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.tools.edit.animation.MessageBubble;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

import eu.udig.tools.merge.internal.view.MergeView;

public class MergeOperation implements IOp {

    private MergeContext mergeContext;
    private MergeView mergeView = null;

    @Override
    public void op(Display display, Object target, IProgressMonitor monitor) throws Exception {

        //Get the selected layer
        ILayer selectedLayer = ApplicationGIS.getActiveMap().getEditManager().getSelectedLayer();
        //Get the filter actively working on it (that is the selection)
        Filter filterSelectedFeatures = selectedLayer.getFilter();
        //Turn the filter into a List of features
        List<SimpleFeature> selectedFeatures = Util.retrieveFeatures(filterSelectedFeatures,
                selectedLayer);

        this.mergeContext = MergeContext.getInstance();

        try {
            //mergeView = (MergeView) ApplicationGIS.getView(true, MergeView.ID);
            if (mergeView == null) {
                // crates a new merge view
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                mergeView = (MergeView) page.findView(MergeView.ID);
            }
            assert mergeView != null : "view is null"; //$NON-NLS-1$

            // associates this the merge view with the merge context
            mergeView.setMergeContext(mergeContext);
            mergeContext.activeMergeView(mergeView);
            
            mergeView.addSourceFeatures(selectedFeatures);

            mergeView.display();

        } catch (Exception ex) {
        }

        

    }

}
