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
package net.refractions.udig.catalog.tests;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.memory.ActiveMemoryDataStore;
import net.refractions.udig.catalog.memory.MemoryServiceExtensionImpl;
import net.refractions.udig.catalog.memory.internal.MemoryServiceImpl;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.SchemaNotFoundException;
import org.geotools.data.memory.MemoryDataStore;
import org.geotools.feature.Feature;
import org.geotools.feature.IllegalAttributeException;
import org.geotools.feature.SchemaException;
import org.geotools.filter.Filter;

/**
 * Provides useful methods for creating catalog tests
 * @author Jesse
 * @since 1.1.0
 */
public class CatalogTests {

    /**
     * Calls createService and finds the georesource containing the features.
     */
    public static IGeoResource createGeoResource(Feature [] features, boolean deleteService) throws IOException{
    	IService service = getService(features, deleteService);

    	List<? extends IGeoResource> resources = service.resources(null);
    	for (IGeoResource resource : resources) {
    		if( resource.resolve(FeatureSource.class, null).getSchema().getTypeName().
    				equals(features[0].getFeatureType().getTypeName()))
    			return resource;
    	}
    	// hopefully will never happen.
    	return null;

    }

    /**
     * Creates a memory service and ads it to the catalog.  If there is already one there then it will be replaced
     *
     * @param catalog
     * @return
     */
    public static IService createService(ICatalog catalog) {
    	IService service;
    	MemoryServiceExtensionImpl ext=new MemoryServiceExtensionImpl();

    	java.util.Map<String, Serializable> params = ext.createParams(MemoryServiceExtensionImpl.URL);
    	service=(MemoryServiceImpl) ext.createService(MemoryServiceExtensionImpl.URL, params);
    	catalog.add(service);
    	return service;
    }

    /**
    	 * Creates a MemoryDatastore service from an array of features.  Does not add to catalog.
    	 * @param deleteService
    	 */
    	public static IService getService(Feature[] features, boolean deleteService) throws IOException {
    		ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
    		List<IResolve> services = catalog.find(MemoryServiceExtensionImpl.URL, new NullProgressMonitor());
    		IService service=null;
    		if( services.isEmpty() ){
    			service = createService(catalog);
    		}else{
    			IResolve resolve=services.get(0);
    			if (resolve instanceof IGeoResource) {
    				IGeoResource resource = (IGeoResource) resolve;
    				service=resource.service(null);
    			}else if (resolve instanceof IService) {
    				service=(IService) services.get(0);
    			}
    		}
    		if( deleteService ){
                if( service.resolve(MemoryDataStore.class, null) instanceof ActiveMemoryDataStore ){
                	ActiveMemoryDataStore ds=(ActiveMemoryDataStore) service.resolve(MemoryDataStore.class, null);
                    ds.removeSchema(features[0].getFeatureType().getTypeName());
                } else {
                    List< ? extends IGeoResource> members = service.resources(new NullProgressMonitor());
                    for( IGeoResource resource : members ) {
                        FeatureStore s = resource
                                .resolve(FeatureStore.class, new NullProgressMonitor());
                        if (s.getSchema().getTypeName().equals(
                                features[0].getFeatureType().getTypeName()))
                            s.removeFeatures(Filter.NONE);
                    }
                }
    		}

    		MemoryDataStore ds=service.resolve(MemoryDataStore.class, null);
    		try{
    			ds.getSchema(features[0].getFeatureType().getTypeName());
    //			if( deleteService)
    //				throw new IOException("FeatureType already exists in Service"); //$NON-NLS-1$
    		}catch( SchemaNotFoundException exception){
    			// verified that schema does not yet exist.
    		}
    		ds.addFeatures(features);
    		return service;
    	}

    public static IGeoResource createGeoResource( String typeName, int numFeatures, boolean deleteService ) throws IOException, SchemaException, IllegalAttributeException {
        return createGeoResource(UDIGTestUtil.createDefaultTestFeatures(typeName, numFeatures), deleteService);
    }

    /**
     * Creates a DummyResource that resolves to the parameter resolve to
     *
     * @param id the id of the parent service, maybe null
     * @param resolveTo the object the resource will resolve to
     * @return an {@link IGeoResource}
     * @throws IOException
     */
    public static IGeoResource createResource( URL id, Object resolveTo ) throws IOException {
        IService service=DummyService.createService(id, null, Collections.singletonList(Collections.singletonList(resolveTo)));

        IGeoResource resource = service.resources(null).get(0);
        return resource;
    }

}
