/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.project.ui.internal.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.project.IProject;
import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.ui.IDropAction;
import net.refractions.udig.ui.ViewerDropLocation;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Handles Layers and IGeoResources being dropped on a Project.  It will create a map and add the layer/resource to the
 * map.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class OnProjectDropAction extends IDropAction {

    @SuppressWarnings("unchecked")
    @Override
    public boolean accept() {
        if( getViewerLocation()==ViewerDropLocation.NONE )
            return false;
        if( !(getDestination() instanceof Project) )
            return false;

        if( isLegalType(getData()) )
            return true;

        if( getData() instanceof Collection ){
            Collection<Object> coll=(Collection<Object>) getData();
            for( Object object : coll ) {
                if( isLegalType(object) )
                    return true;
            }
        }
        return false;
    }

    private boolean  isLegalType(Object obj) {
        if( obj instanceof IGeoResource )
            return true;
        if( obj instanceof IService )
            return true;
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void perform( IProgressMonitor monitor ) {
        if( !accept() )
            throw new IllegalStateException("the data or destination is not legal"); //$NON-NLS-1$
        List<IGeoResource> resources=new ArrayList<IGeoResource>();

        if( getData() instanceof IGeoResource ){
            resources.add((IGeoResource) getData());
        } else if( getData() instanceof IService ){
            resources.addAll(MapDropAction.toResources(monitor, getData(), getClass()));
        } else {
            List<Object> list=(List<Object>) getData();
            for( Object object : list ) {
                if( object instanceof IGeoResource ){
                    resources.add((IGeoResource) object);
                } else if( object instanceof IService ){
                    Collection<IGeoResource> toResources = MapDropAction.toResources(monitor, object, getClass());
                    resources.addAll(toResources);
                }
            }
        }

        ApplicationGIS.createAndOpenMap(resources, (IProject) getDestination());
    }

}
