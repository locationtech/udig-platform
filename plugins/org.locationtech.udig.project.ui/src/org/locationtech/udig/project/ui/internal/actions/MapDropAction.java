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
package org.locationtech.udig.project.ui.internal.actions;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.internal.ui.actions.CatalogImportDropAction;
import org.locationtech.udig.catalog.ui.workflow.ResourceSelectionState;
import org.locationtech.udig.catalog.ui.workflow.State;
import org.locationtech.udig.catalog.ui.workflow.Workflow;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizard;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardPageProvider;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.impl.LayerResource;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.project.ui.internal.Trace;
import org.locationtech.udig.project.ui.internal.wizard.MapImport;
import org.locationtech.udig.project.ui.internal.wizard.MapImportWizard;
import org.locationtech.udig.ui.ViewerDropLocation;

public class MapDropAction extends CatalogImportDropAction {

    @Override
    public boolean accept() {
        Object data2 = getData();
        if ( data2.getClass().isArray() ){
            Object[] objects = ((Object[])data2);
            for( Object object : objects ) {
                if( canAccept(object) ){
                    return true;
                }
            }
            return false;
        }else {
            return canAccept(data2);
        }
        
    }

    private boolean canAccept( Object data2 ) {
        if( data2 instanceof LayerResource ){
            ILayer layer=((LayerResource) data2).getLayer();
            
            // Do not accept layer from same map, user is just changing order
            if ( desinationContainsLayer(layer) ){
                return false;
            }
            if ( destinationLayerMapContainsLayer(layer)){ 
                return false;
            }
        }
        if( data2 instanceof IGeoResource ){
            return true;
        }
        if( data2 instanceof IAdaptable ){
            IAdaptable adaptable=(IAdaptable) data2;
            if( adaptable.getAdapter(IGeoResource.class)!=null ){
                return true;
            }
        }
        if (data2 instanceof IResolve){
            return true; // may have to prompt the user to choose 
        }
        return canImport(data2);
    }

    private boolean destinationLayerMapContainsLayer( ILayer layer ) {
        if( getDestination() instanceof ILayer ){
            ILayer dest=(ILayer) getDestination();
            if( dest.getMap().getMapLayers().contains(layer) )
                return true;
        }
        return false;
    }

    private boolean desinationContainsLayer( ILayer layer) {
        if( getDestination() instanceof IMap ){
        IMap map=(IMap) getDestination();
        if( map.getMapLayers().contains(layer) )
            return true;
        }
        return false;
    }
    
    @Override
    public void perform(IProgressMonitor monitor) {
        List<IGeoResource> resources=new ArrayList<IGeoResource>();
        List<Object> otherData=new ArrayList<Object>();
        
        Object data2 = getData();
        Object[] array;
        if( data2.getClass().isArray() ){
            array=(Object[]) data2;
            for( int i = 0; i < array.length; i++ ) {
                Object object = array[i];
                if(canAccept(object)){
                    seperateGeoResources(resources, otherData, object);
                }
            }
        }else{
            seperateGeoResources(resources, otherData, data2);
        }
        
        int layerpos=-1;
        layerpos = calculateDropPosition();
        IMap map=null;
        if( !otherData.isEmpty() ){
            for( Object object : otherData ) {
                Collection<IGeoResource> additionalResources = toResources(monitor, object, getClass());
                
                ProjectUIPlugin.trace(Trace.DND, MapDropAction.class,
                        "Converted from DnD "+object.getClass().getSimpleName()+" data to:"+additionalResources,null);
                resources.addAll(additionalResources);
            }
        }
        
        if( !resources.isEmpty() ){
            addResourcesToMap(resources, layerpos, map);
        }
    }

    static Collection<IGeoResource> toResources( IProgressMonitor monitor, Object object, Class<?> callingClass ) {
        // create a wizard that does not add to map.  We will add all resources at once.
        MapImport mapImport = new MapImport(){

            @Override
            protected WorkflowWizard createWorkflowWizard( Workflow workflow,
                    java.util.Map<Class< ? extends State>, WorkflowWizardPageProvider> map ) {
                return new MapImportWizard(workflow, map){
                    @Override
                    protected boolean performFinish( IProgressMonitor monitor ) {
                        return true;
                    }
                };
           }
        };
        if (mapImport.run(monitor, object)) {

            ResourceSelectionState state = mapImport.getDialog().getWorkflowWizard().getWorkflow()
                    .getState(ResourceSelectionState.class);

            Set<IGeoResource> keySet = state.getResources().keySet();

            ProjectUIPlugin.trace(Trace.DND, callingClass,
                    "converted " + object + " to " + keySet, null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

            return keySet;
        } else {
            return Collections.emptyList();
        }
    }

    private void addResourcesToMap( List<IGeoResource> resources, int layerpos,IMap map2 ) {
        IMap map=map2;
        if( getDestination() instanceof Layer){
            Layer layer=(Layer) getDestination();
            map=layer.getMap();
        } else if( getDestination() instanceof IMap ){
            map = (IMap) getDestination();
        }
        if (map==null )
            map = ApplicationGIS.getActiveMap();
        if( map==ApplicationGIS.NO_MAP ){
            ProjectUIPlugin.trace(Trace.DND, getClass(), "Creating new Map with from resources: "+resources, null); //$NON-NLS-1$
            ApplicationGIS.addLayersToMap((IMap)null, resources, layerpos);
        }else{
            ProjectUIPlugin.trace(Trace.DND, getClass(), "Add layers to "+map.getName()+" from resources: "+resources, null);  //$NON-NLS-1$//$NON-NLS-2$
            ApplicationGIS.addLayersToMap(map, resources, layerpos, null, true);
        }
    }
    /**
     * Places the object into the appropriate resources or otherData list.
     * 
     * @param resources
     * @param otherData
     * @param object
     */
    private void seperateGeoResources( List<IGeoResource> resources, List<Object> otherData, Object object ) {
        if( object instanceof IGeoResource ){
            resources.add((IGeoResource) object);
        }
        else {
            Object processed = processDropItem(object);
            otherData.add(processed);
        }
    }

    private int calculateDropPosition( ) {
        int layerpos=-1;
        if( getDestination() instanceof ILayer ){
            ILayer target=(ILayer) getDestination();
            ViewerDropLocation location = getViewerLocation();
            layerpos = target.getZorder();
            
            if (location == ViewerDropLocation.NONE) {
                layerpos=0;
            }

            // Moving something AFTER a layer is the same as moving something BEFORE a layer.
            // So we will use BEFORE as much as possible to prevent duplication here.
            // This code will retrieve the layer before. Or the first one, if we are at the
            // beginning of the list.
            if( location == ViewerDropLocation.BEFORE ){
                layerpos ++;
            }
            if( location == ViewerDropLocation.ON ){
                layerpos++;
            }
        }
        return layerpos;
    }
    /**
     * Tries to return a File or URL if possible from provided concreteData2
     * @param concreteData2
     * @return
     */
    private Object processDropItem( Object concreteData2 ) {
        Object concreteData = concreteData2;
        if (concreteData instanceof String) {
            URL url = extractURL((String) concreteData);
            if (url != null) {
                concreteData = url;
            }
        }
        return concreteData;
    }

}
