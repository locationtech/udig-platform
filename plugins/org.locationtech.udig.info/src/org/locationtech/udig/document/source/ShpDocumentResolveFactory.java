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

import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IResolveAdapterFactory;
import org.locationtech.udig.catalog.document.IAttachmentSource;
import org.locationtech.udig.catalog.document.IDocumentSource;
import org.locationtech.udig.catalog.document.IHotlinkSource;
import org.locationtech.udig.catalog.internal.shp.ShpGeoResourceImpl;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Teach ShpGeoResource how to support documents, hotlinks and attachments.
 * 
 * @author Jody Garnett
 * @since 1.3.2
 */
public class ShpDocumentResolveFactory implements IResolveAdapterFactory {

    @Override
    public boolean canAdapt(IResolve resolve, Class<?> adapter) {
        if (resolve instanceof ShpGeoResourceImpl) {
            if (adapter.isAssignableFrom(IDocumentSource.class)
                    || adapter.isAssignableFrom(IHotlinkSource.class)
                    || adapter.isAssignableFrom(IAttachmentSource.class)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public <T> T adapt(IResolve resolve, Class<T> adapter, IProgressMonitor monitor)
            throws IOException {

        if (resolve instanceof ShpGeoResourceImpl) {
            final ShpGeoResourceImpl shpGeoResource = (ShpGeoResourceImpl) resolve;
            if (adapter.isAssignableFrom(IDocumentSource.class)) {
                IDocumentSource documentSource = new ShpDocumentSource(shpGeoResource);
                if (documentSource != null) {
                    return adapter.cast(documentSource);
                }
            }
            if (adapter.isAssignableFrom(IHotlinkSource.class)) {
                IHotlinkSource hotlink = new ShpHotlinkSource(shpGeoResource);
                if (hotlink != null) {
                    return adapter.cast(hotlink);
                }
            }
            if (adapter.isAssignableFrom(IAttachmentSource.class)) {
                IAttachmentSource attachmentSource = new ShpAttachmentSource(shpGeoResource);
                if (attachmentSource != null) {
                    return adapter.cast(attachmentSource);
                }
            }
        }
            
        return null;
    }

}
