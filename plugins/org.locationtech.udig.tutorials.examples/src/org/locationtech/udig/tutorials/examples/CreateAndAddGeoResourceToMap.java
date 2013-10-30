/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tutorials.examples;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceInfo;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ui.ApplicationGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * How to create a temporary layer.
 * 
 * @author Jesse Eichar
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
    public void example(URL url, IProgressMonitor progressMonitor, int addPosition, IMap map) throws IOException {
        progressMonitor.beginTask("task", 6); //$NON-NLS-1$
        progressMonitor.worked(1);

        try {

            ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();

            // first search the local catalog.
            List<IResolve> matches = catalog.find(url, new SubProgressMonitor(progressMonitor, 2));

            for (IResolve resolve : matches) {
                if (resolve instanceof ExpectedService) {
                    // found the resource now we have to search it for the
                    // resource we want
                    if (searchServiceForResource(new SubProgressMonitor(progressMonitor, 2), addPosition, map,
                            (IService) resolve))
                        return;
                } else if (resolve instanceof ExpectedGeoResource) {
                    // yay we found the resource this is too easy:)

                    ApplicationGIS.addLayersToMap(map, Collections.singletonList((IGeoResource) resolve), addPosition);
                    return;
                }
            }

            // usually only returns 1 service but it may be that multiple
            // Services know how to interpret the URL
            List<IService> services = CatalogPlugin.getDefault().getServiceFactory().createService(url);
            IService found = null;
            progressMonitor.worked(1);

            // find the service you want
            for (IService service : services) {
                // determine if the service is the type you are expecting;
                if (service instanceof ExpectedService) {
                    found = service;
                    break;
                }
            }

            catalog.add(found);
            searchServiceForResource(new SubProgressMonitor(progressMonitor, 2), addPosition, map, found);

        } finally {
            progressMonitor.done();
        }

    }

    private boolean searchServiceForResource(IProgressMonitor progressMonitor, int addPosition, IMap map, IService found)
            throws IOException {
        List<? extends IGeoResource> resources = found.resources(progressMonitor);

        // now find the resource you want.
        for (IGeoResource resource : resources) {
            if (someLogic(resource)) {
                // ok we've found it
                // add the resource to the map and return
                ApplicationGIS.addLayersToMap(map, Collections.singletonList(resource), addPosition);
                return true;
            }
        }
        return false;
    }

    private boolean someLogic(IGeoResource resource) {
        return false;
    }

    private class ExpectedService extends IService {

        @Override
        public Map<String, Serializable> getConnectionParams() {
            return null;
        }

        @Override
        public List<? extends IGeoResource> resources(IProgressMonitor monitor) throws IOException {
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
        protected IServiceInfo createInfo(IProgressMonitor monitor) throws IOException {
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
        protected IGeoResourceInfo createInfo(IProgressMonitor monitor) throws IOException {
            return null;
        }

    }
}
