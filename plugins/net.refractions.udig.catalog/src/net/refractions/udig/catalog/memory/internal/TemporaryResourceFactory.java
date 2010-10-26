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
            net.refractions.udig.catalog.TemporaryResourceFactory {

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
