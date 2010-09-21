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
package net.refractions.udig.catalog.memory.internal;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.URLUtils;
import net.refractions.udig.catalog.memory.MemoryServiceExtensionImpl;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.memory.MemoryDataStore;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Creates a MemoryGeoResource
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class TemporaryResourceFactory
        implements
            net.refractions.udig.catalog.TemporaryResourceFactory {

    public IGeoResource createResource( Object param ) throws IOException {
        SimpleFeatureType featureType = (SimpleFeatureType) param;

        IService service = getMemoryService();

        MemoryDataStore ds = service.resolve(MemoryDataStore.class, new NullProgressMonitor());
        if( Arrays.asList(ds.getTypeNames()).contains(featureType.getName().getLocalPart()) )
            ds.updateSchema(featureType.getName().getLocalPart(), featureType);
        else
            ds.createSchema(featureType);

        IGeoResource resource = null;
        for( IResolve resolve : service.resources(new NullProgressMonitor()) ) {
            if (resolve instanceof IGeoResource) {
                IGeoResource r = (IGeoResource) resolve;
                if (r.resolve(SimpleFeatureType.class, new NullProgressMonitor()).getName().getLocalPart().equals(
                        featureType.getName().getLocalPart())) {
                    resource = r;
                    break;
                }
            }
        }
        return resource;
    }

    private MemoryServiceImpl getMemoryService() {
        // Make sure the memory service is in the catalog
        MemoryServiceImpl service = null;

        try {
            List< ? extends IResolve> members = CatalogPlugin.getDefault().getLocalCatalog()
                    .members(new NullProgressMonitor());
            for( IResolve resolve : members ) {
                if (resolve instanceof MemoryServiceImpl) {
                    if (URLUtils.urlEquals(resolve.getIdentifier(), MemoryServiceExtensionImpl.URL, true)) { 
                        service = (MemoryServiceImpl) resolve;
                        break;
                    }   
                }
            }
        } catch (IOException e) {
            CatalogPlugin.log("Error finding services", e); //$NON-NLS-1$
        }

        if (service == null) {
            service = new MemoryServiceImpl(MemoryServiceExtensionImpl.URL); 
            CatalogPlugin.getDefault().getLocalCatalog().add(service);

        }
        return service;
    }
}
