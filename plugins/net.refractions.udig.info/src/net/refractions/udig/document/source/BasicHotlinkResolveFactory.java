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
package net.refractions.udig.document.source;

import java.io.IOException;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveAdapterFactory;
import net.refractions.udig.catalog.PostgisGeoResource2;
import net.refractions.udig.catalog.document.IHotlinkSource;
import net.refractions.udig.document.ui.DocumentPropertyPage;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Folds in BasicHotlink implementation for any IGeoResource that lists its hotlink attributes as
 * part of its {@link IGeoResource#getPersistentProperties()}.
 * <p>
 * Contains utility methods to help both {@link BasicHotlinkSource} and {@link DocumentPropertyPage}
 * extract descriptors from a IGeoResource.
 * 
 * @author Jody Garnett (LISAsoft)
 * @since 1.3.2
 */
public class BasicHotlinkResolveFactory implements IResolveAdapterFactory {

    /**
     * {@link IGeoResource#getPersistentProperties()} key used to record hotlink descriptor list.
     */
    final static String HOTLINK = "hotlink"; //$NON-NLS-1$

    @Override
    public boolean canAdapt(IResolve resolve, Class<? extends Object> adapter) {
        if (resolve instanceof PostgisGeoResource2) {
            if (adapter.isAssignableFrom(IHotlinkSource.class)) {
                return true;
            }    
        }
        return false;
    }

    @Override
    public <T> T adapt(IResolve resolve, Class<T> adapter, IProgressMonitor monitor)
            throws IOException {
        if (resolve instanceof PostgisGeoResource2) {
            final PostgisGeoResource2 resource = (PostgisGeoResource2) resolve;
            if (adapter.isAssignableFrom(IHotlinkSource.class)) {
                final IHotlinkSource source = new BasicHotlinkSource(resource);
                return adapter.cast(source);
            }    
        }
        return null;
    }
    
}
