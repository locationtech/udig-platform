/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
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
package net.refractions.udig.catalog.shp;

import java.io.File;
import java.io.IOException;

import net.refractions.udig.catalog.IDocumentSource;
import net.refractions.udig.catalog.IHotlink;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveAdapterFactory;
import net.refractions.udig.catalog.internal.shp.ShpGeoResourceImpl;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Teach ShpGeoResource how to support hotlinks.
 * 
 * @author Jody Garnett
 * @since 1.3.2
 */
public class ShpDocumentResolveFactory implements IResolveAdapterFactory {
    @Override
    public boolean canAdapt(IResolve resolve, Class<? extends Object> adapter) {
        if (resolve instanceof ShpGeoResourceImpl) {
            ShpGeoResourceImpl shpGeoResource = (ShpGeoResourceImpl) resolve;
            final File file = ShpDocPropertyParser.getPropertiesFile(shpGeoResource.getID());
            if( file != null ){
                if( adapter.isAssignableFrom(IDocumentSource.class) || adapter.isAssignableFrom(IHotlink.class) ){
                    return file.exists(); // put off exist check until last as it involves IO
                }
            }
        }
        return false;
    }
    
    @Override
    public Object adapt(IResolve resolve, Class<? extends Object> adapter, IProgressMonitor monitor)
            throws IOException {
        if (adapter.isAssignableFrom(IDocumentSource.class)) {
            if (resolve instanceof ShpGeoResourceImpl) {
                ShpGeoResourceImpl shpGeoResource = (ShpGeoResourceImpl) resolve;
                IDocumentSource documentSource = document(shpGeoResource,monitor);
                if (documentSource != null) {
                    return adapter.cast(documentSource);
                }
            }
        }
        if (adapter.isAssignableFrom(IHotlink.class)) {
            if (resolve instanceof ShpGeoResourceImpl) {
                ShpGeoResourceImpl shpGeoResource = (ShpGeoResourceImpl) resolve;
                IHotlink hotlink = hotlink(shpGeoResource,monitor);
                if (hotlink != null) {
                    return adapter.cast(hotlink);
                }
            }
        }
        return null;
    }

    /**
     * Resolves to a hotlink source.
     * <p>
     * This method is package visible for testing; to access this value use:
     * <code>resolveFacotry.adapt( shpGeoResouce, IHotlinkSource.class)</code>
     * 
     * @param monitor
     * @return hotlink source used to access attribute links and file referenes
     */
    private IHotlink hotlink(ShpGeoResourceImpl shpGeoResource, IProgressMonitor monitor) {
        final File file = ShpDocPropertyParser.getPropertiesFile(shpGeoResource.getID());
        if (file != null && file.exists()) {
            return new ShpHotlinkSource(shpGeoResource);
        }
        return null; // not available
    }

    /**
     * Resolves to a document source.
     * <p>
     * This method is package visible for testing; to access this value use:
     * <code>resolveFacotry.adapt( shpGeoResouce, IDocumentSource.class)</code>
     * @param shpGeoResource 
     * 
     * @param monitor
     * @return document source to access shapefile sidecar files
     */
    private IDocumentSource document(ShpGeoResourceImpl shpGeoResource, IProgressMonitor monitor) {
        final File file = ShpDocPropertyParser.getPropertiesFile(shpGeoResource.getID());
        if( file != null && file.exists()){
            return new ShpDocumentSource( shpGeoResource );
        }
        return null; // not available
    }

}
