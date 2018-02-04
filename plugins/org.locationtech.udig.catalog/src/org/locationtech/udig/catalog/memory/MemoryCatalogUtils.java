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
package org.locationtech.udig.catalog.memory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.memory.MemoryDataStore;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.URLUtils;
import org.locationtech.udig.catalog.memory.internal.MemoryServiceImpl;

/**
 * Utility class to work with catalog resources for MemoryServiceImpl
 * @author fgdrf
 *
 */
public final class MemoryCatalogUtils {

    /**
     * Remove a memory datastore from the catalog, since the update is not possible (Caused by:
     * java.lang.UnsupportedOperationException: Schema modification not supported)
     * 
     * @param typeName the name of the type to remove, if it is there
     */
    public static void removeMemoryServiceByTypeName(String typeName) {
        MemoryServiceImpl service = null;
        try {
            List<? extends IResolve> members = CatalogPlugin.getDefault().getLocalCatalog()
                    .members(new NullProgressMonitor());
            for (IResolve resolve : members) {
                if (resolve instanceof MemoryServiceImpl) {
                    if (URLUtils.urlEquals(resolve.getIdentifier(), MemoryServiceExtensionImpl.URL,
                            true)) {
                        service = (MemoryServiceImpl) resolve;
                        break;
                    }
                }
            }
            if (service == null)
                return;
            MemoryDataStore ds = service.resolve(MemoryDataStore.class, new NullProgressMonitor());
            if (Arrays.asList(ds.getTypeNames()).contains(typeName)) {
                CatalogPlugin.getDefault().getLocalCatalog().remove(service);
            }
        } catch (IOException e) {
            CatalogPlugin.log("Error finding services", e); //$NON-NLS-1$
        }
    }
}
