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

import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveAdapterFactory;
import net.refractions.udig.catalog.document.IAttachmentSource;
import net.refractions.udig.catalog.document.IDocumentSource;
import net.refractions.udig.catalog.document.IHotlinkSource;
import net.refractions.udig.catalog.internal.shp.ShpGeoResourceImpl;

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
