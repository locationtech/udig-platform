package eu.udig.tools.merge;

import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

import eu.udig.tools.merge.internal.view.MergeView;

public class MergeOperation implements IOp {
    
    @SuppressWarnings("unused")
    private MergeView mergeView;

    @Override
    public void op(final Display display, Object target, IProgressMonitor monitor) throws Exception {
        
        //final FeatureSource  preSelectedLayer = (FeatureSource) target;

        Thread t = new Thread() {

            public void run() {
                try {
                    // Set tool mode (also set in MergeTool.setContext to MERGEMODE_TOOL) 
                    MergeContext mergeContextSingleton = MergeContext.getInstance();
                    mergeContextSingleton.setMergeMode(MergeContext.MERGEMODE_OPERATION);
                    // Store eventual pre-selected features for later display in MergeView
                    ILayer preSelectedLayer = ApplicationGIS.getActiveMap().getEditManager().getSelectedLayer();
                    Filter preSelectedFilter = preSelectedLayer.getFilter();
                    if ( preSelectedFilter != Filter.EXCLUDE){
                        List<SimpleFeature> preSelectedFeatures = Util.retrieveFeatures(preSelectedFilter, preSelectedLayer);
                        mergeContextSingleton.addPreselectedFeatures(preSelectedFeatures, preSelectedLayer);
                    }
                    
                    // Open view
                    mergeView = (MergeView) ApplicationGIS.getView(true, MergeView.ID);
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        display.asyncExec(t);
        
        //PlatformGIS.syncInDisplayThread(t);

    }
}
