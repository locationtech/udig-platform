/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolveFolder;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.project.IProject;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.ui.IDropAction;
import org.locationtech.udig.ui.ViewerDropLocation;

/**
 * Handles Layers and IGeoResources being dropped on a Project.  It will create a map and add the layer/resource to the
 * map.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class OnProjectDropAction extends IDropAction {

    public OnProjectDropAction() {
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean accept() {
        if( getViewerLocation()==ViewerDropLocation.NONE ){
            return false;
        }
        if( !(getDestination() instanceof Project) ){
            return false;
        }
        
        if( isLegalType(getData()) ){
            return true;
        }
        
        List<Object> obj = toCollection();
        return !obj.isEmpty();
    }

    private List<Object> toCollection() {
        Object[] array=null;
        
        if(getData().getClass().isArray()){
            array=(Object[])getData();
        }
        if( getData() instanceof Collection<?> ){
            Collection<?> coll=(Collection<?>) getData();
            array=coll.toArray();
        }
        List<Object> obj=new ArrayList<Object>();
        if(array!=null){
            for( Object object : array ) {
                if( isLegalType(object) ){
                    obj.add(object);
                }
            }
        }
        return obj;
    }

    private boolean isLegalType( Object obj ) {
        if (obj instanceof IGeoResource) {
            return true;
        }
        if (obj instanceof IResolveFolder) {
            return true;
        }
        if (obj instanceof IService) {
            return true;
        }
        return false;
    }

    @Override
    public void perform( IProgressMonitor monitor ) {
        if (!accept()) {
            throw new IllegalStateException("the data or destination is not legal"); //$NON-NLS-1$
        }
        List<IGeoResource> resources=new ArrayList<IGeoResource>();
        
        Object data = getData();
        if( data instanceof IGeoResource ){
            resources.add((IGeoResource) data);
        } else if( data instanceof IResolveFolder ){
            resources.addAll(MapDropAction.toResources(monitor, data, getClass()));
        } else if( data instanceof IService ){
            resources.addAll(MapDropAction.toResources(monitor, data, getClass()));
        } else if (data instanceof String) {
            new OpenMapAction().loadMapFromString((String) data, null, true);
            return;
        } else {
            List<Object> list=toCollection();
            for( Object object : list ) {
                if( object instanceof IGeoResource ){
                    resources.add((IGeoResource) object);
                } else if( object instanceof IService || object instanceof IResolveFolder){
                    Collection<IGeoResource> toResources = MapDropAction.toResources(monitor, object, getClass());
                    resources.addAll(toResources);
                }
            }
        }

        ApplicationGIS.createAndOpenMap(resources, (IProject) getDestination());
    }



}
