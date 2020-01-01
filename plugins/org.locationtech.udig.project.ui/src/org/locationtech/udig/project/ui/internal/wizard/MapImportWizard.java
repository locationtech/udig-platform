/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.internal.wizard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.ui.wizard.CatalogImportWizard;
import org.locationtech.udig.catalog.ui.workflow.EndConnectionState;
import org.locationtech.udig.catalog.ui.workflow.ResourceSelectionState;
import org.locationtech.udig.catalog.ui.workflow.State;
import org.locationtech.udig.catalog.ui.workflow.Workflow;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardPageProvider;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.Messages;

/**
 * Provided workflow is used to add Data to the map.
 */
public class MapImportWizard extends CatalogImportWizard {
    int layerPosition = -1;

    public MapImportWizard(Workflow workflow,
            java.util.Map<Class<? extends State>, WorkflowWizardPageProvider> map) {
        this(workflow, map, -1);
    }

    public MapImportWizard(Workflow workflow,
            java.util.Map<Class<? extends State>, WorkflowWizardPageProvider> map, int layerPosition) {
        super(workflow, map);
        setWindowTitle("Add Data");
        this.layerPosition = layerPosition;
    }

    public void setLayerPosition(int layerPosition) {
        this.layerPosition = layerPosition;
    }

    @Override
    public boolean canFinish() {
        return super.canFinish();
    }

    @Override
    protected boolean performFinish(IProgressMonitor monitor) {
        String name = Messages.MapImport_createMap;
        monitor.beginTask(name, IProgressMonitor.UNKNOWN);
        monitor.setTaskName(name);
        
        EndConnectionState catalogImportState = getWorkflow().getState(EndConnectionState.class);
        if( catalogImportState != null ){
            boolean superFinished = super.performFinish(monitor);
            if (!superFinished){
                return superFinished; // connection failed unable to add to catalog
            }
        }
        
        List<IGeoResource> resourceList = new ArrayList<IGeoResource>();
        /*
        ResourceSearchState search = getWorkflow().getState( ResourceSearchState.class);
        if( search != null ){
            for( IResolve item : search.getSelected() ){
                if( item == ResourceSearchState.IMPORT_PLACEHOLDER ){
                    continue;
                }
                if( item instanceof IGeoResource ){
                    resourceList.add( (IGeoResource) item );
                }
            }
        }
        */
        ResourceSelectionState state = getWorkflow().getState(ResourceSelectionState.class);
        if( state != null ){
            java.util.Map<IGeoResource, IService> resourceMap = state.getResources();
            if (resourceMap != null && !resourceMap.isEmpty() ){
                resourceList.addAll( resourceMap.keySet() );
            }
        }
        if( resourceList.isEmpty() ){
            return false; // nothing to see here
        }
        // add the resources to the map
        
        // reverse the list as ApplicationGIS.addLayersToMap add the first layer at the bottom
        // and so on
        Collections.reverse(resourceList);

        monitor.setTaskName(Messages.MapImport_addingLayersTask);
        ApplicationGIS.addLayersToMap(ApplicationGIS.getActiveMap(), resourceList, layerPosition,
                null, true);

        return true;
    }

    @Override
    protected boolean isShowCatalogView() {
        return false;
    }
}
