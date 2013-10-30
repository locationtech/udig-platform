/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.memory.internal;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.URLUtils;
import org.locationtech.udig.catalog.memory.MemoryServiceExtensionImpl;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.memory.MemoryDataStore;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

/**
 * Creates a MemoryGeoResource
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class TemporaryResourceFactory
        implements
            org.locationtech.udig.catalog.TemporaryResourceFactory {

    public IGeoResource createResource( Object param ) throws IOException {
        SimpleFeatureType featureType = (SimpleFeatureType) param;

        IService service = getMemoryService();

        MemoryDataStore ds = service.resolve(MemoryDataStore.class, new NullProgressMonitor());
        List<String> typeNamesList = Arrays.asList(ds.getTypeNames());
        String localPart = featureType.getName().getLocalPart();
        if (typeNamesList.contains(localPart)) {
            try {
                ds.updateSchema(localPart, featureType);
            } catch (Exception e) {
                // some datastores do not support schema update, try a name change
                // create the feature type
                String name = checkSameName(typeNamesList, localPart);
                SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
                b.setName(name);
                b.setCRS(featureType.getCoordinateReferenceSystem());
                List<AttributeDescriptor> attributeDescriptors = featureType.getAttributeDescriptors();
                b.addAll(attributeDescriptors);
                featureType = b.buildFeatureType();
                ds.createSchema(featureType);
            }
        } else {
            ds.createSchema(featureType);
        }

        IGeoResource resource = null;
        for( IResolve resolve : service.resources(new NullProgressMonitor()) ) {
            if (resolve instanceof IGeoResource) {
                IGeoResource r = (IGeoResource) resolve;
                if (r.resolve(SimpleFeatureType.class, new NullProgressMonitor()).getName().getLocalPart()
                        .equals(featureType.getName().getLocalPart())) {
                    resource = r;
                    break;
                }
            }
        }
        return resource;
    }
    
    /**
     * Checks if the list of typeNames supplied contains the supplied typeName.
     * 
     * <p>If the rule is contained it adds an index to the name.
     * 
     * @param typeNamesList the list of existing typenames.
     * @param typeName the proposed typename, to be changed if colliding.
     * @return the new non-colliding name for the type.
     */
    @SuppressWarnings("nls")
    private String checkSameName( List<String> typeNamesList, String typeName ) {
        int index = 1;
        for( int i = 0; i < typeNamesList.size(); i++ ) {
            String existingTypeName = typeNamesList.get(i);
            existingTypeName = existingTypeName.trim();
            if (existingTypeName.equals(typeName)) {
                // name exists, change the name of the entering
                if (typeName.endsWith(")")) {
                    typeName = typeName.trim().replaceFirst("\\([0-9]+\\)$", "(" + (index++) + ")");
                } else {
                    typeName = typeName + " (" + (index++) + ")";
                }
                // start again
                i = -1;
            }
            if (index == 1000) {
                // something odd is going on
                throw new RuntimeException();
            }
        }
        return typeName;
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
