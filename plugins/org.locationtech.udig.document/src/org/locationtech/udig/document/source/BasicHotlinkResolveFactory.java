/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.document.source;

import java.io.IOException;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IResolveAdapterFactory;
import org.locationtech.udig.catalog.document.IHotlinkSource;
import org.locationtech.udig.document.ui.DocumentPropertyPage;

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
        if (resolve instanceof IGeoResource) {
            if (adapter.isAssignableFrom(IHotlinkSource.class)) {
                return true;
            }    
        }
        return false;
    }

    @Override
    public <T> T adapt(IResolve resolve, Class<T> adapter, IProgressMonitor monitor)
            throws IOException {
        if (resolve instanceof IGeoResource) {
            final IGeoResource resource = (IGeoResource) resolve;
            if (adapter.isAssignableFrom(IHotlinkSource.class)) {
                final IHotlinkSource source = new BasicHotlinkSource(resource);
                return adapter.cast(source);
            }    
        }
        return null;
    }
    
}
