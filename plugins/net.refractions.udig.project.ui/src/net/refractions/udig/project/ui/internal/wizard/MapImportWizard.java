/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004-2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.project.ui.internal.wizard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ui.wizard.CatalogImportWizard;
import net.refractions.udig.catalog.ui.workflow.EndConnectionState;
import net.refractions.udig.catalog.ui.workflow.ResourceSearchState;
import net.refractions.udig.catalog.ui.workflow.ResourceSelectionState;
import net.refractions.udig.catalog.ui.workflow.State;
import net.refractions.udig.catalog.ui.workflow.Workflow;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardPageProvider;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;

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