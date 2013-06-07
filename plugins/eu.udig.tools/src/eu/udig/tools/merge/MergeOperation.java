/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
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

/**
 * Supports an Operation-started Workflow for MergeTool usage.
 * 
 * User selects some feature and, through right-click, open the "Operation -> Merge selected" menu:
 * this calls the present class.
 * <p>
 * This class provides a starting point for opening the MergeView in the MERGEMODE_OPERATION
 * status. This field is stored in MergeContext that acts as a blackboard, and is retrieved
 * throughout the whole plug-in whenever a difference in tool behaviour has been introduced
 * to support the operation-mode workflow (against the 'classic' MergeTool one)
 * </p>
 * 
 * @author Marco Foi (www.mcfoi.it)
 */
public class MergeOperation implements IOp {
    
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

    }
}
