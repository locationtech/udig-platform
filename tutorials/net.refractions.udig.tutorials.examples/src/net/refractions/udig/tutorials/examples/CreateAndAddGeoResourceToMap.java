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
package net.refractions.udig.tutorials.examples;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * How to create a temporary layer
 *
 * @author Jesse
 * @since 1.1.0
 */
public class CreateAndAddGeoResourceToMap {


    /**
     * Q:
     *
     * I want to add an image to the map, how do I do that?
     *
     *
     * A:
     *
     * This example shows how to create an IGeoResource from a URL and add it as a layer to the current map.
     *
     */
    public void example(URL url, IProgressMonitor progressMonitor,
            int addPosition, IMap map ) throws IOException {
        progressMonitor.beginTask("task", 6); //$NON-NLS-1$
        progressMonitor.worked(1);

        try{

        ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();

        // first search the local catalog.
        List<IResolve> matches = catalog.find(url, new SubProgressMonitor(progressMonitor, 2));

        for( IResolve resolve : matches ) {
            if( resolve instanceof ExpectedService ){
                // found the resource now we have to search it for the resource we want
                if( searchServiceForResource(new SubProgressMonitor(progressMonitor, 2), addPosition, map, (IService)resolve) )
                        return;
            }else if (resolve instanceof ExpectedGeoResource ){
                // yay we found the resource this is too easy:)

                ApplicationGIS.addLayersToMap(map, Collections.singletonList((IGeoResource)resolve), addPosition);
                return;
            }
        }


        // usually only returns 1 service but it may be that multiple Services know how to interpret the URL
        List<IService> services = CatalogPlugin.getDefault().getServiceFactory().createService(url);
        IService found=null;
        progressMonitor.worked(1);

        // find the service you want
        for( IService service : services ) {
            // determine if the service is the type you are expecting;
            if( service instanceof ExpectedService ){
                found=service;
                break;
            }
        }

        catalog.add(found);
        searchServiceForResource(new SubProgressMonitor(progressMonitor,2), addPosition, map, found);

        }finally{
            progressMonitor.done();
        }

    }

    private boolean searchServiceForResource( IProgressMonitor progressMonitor, int addPosition, IMap map, IService found ) throws IOException {
        List< ? extends IGeoResource> resources = found.resources(progressMonitor);

        // now find the resource you want.
        for( IGeoResource resource : resources ) {
            if( someLogic(resource) ){
                // ok we've found it
                // add the resource to the map and return
                ApplicationGIS.addLayersToMap(map, Collections.singletonList(resource), addPosition);
                return true;
            }
        }
        return false;
    }

    private boolean someLogic( IGeoResource resource ) {
        return false;
    }

    private class ExpectedService extends IService{

        @Override
        public Map<String, Serializable> getConnectionParams() {
            return null;
        }

        @Override
        public List< ? extends IGeoResource> resources( IProgressMonitor monitor ) throws IOException {
            return null;
        }

        public URL getIdentifier() {
            return null;
        }

        public Throwable getMessage() {
            return null;
        }

        public Status getStatus() {
            return null;
        }

        @Override
        public IServiceInfo getInfo( IProgressMonitor monitor ) throws IOException {
            return null;
        }

    }

    public class ExpectedGeoResource extends IGeoResource {

        @Override
        public URL getIdentifier() {
            return null;
        }

        public Throwable getMessage() {
            return null;
        }

        public Status getStatus() {
            return null;
        }

        @Override
        public IGeoResourceInfo getInfo( IProgressMonitor monitor ) throws IOException {
            return null;
        }

        @Override
        public IService service( IProgressMonitor monitor ) throws IOException {
            return null;
        }

    }
}
