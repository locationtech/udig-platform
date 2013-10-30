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

import org.locationtech.udig.catalog.document.IDocument.ContentType;
import org.locationtech.udig.catalog.internal.shp.ShpGeoResourceImpl;

/**
 * Shapefile feature hotlink support. This implements getter and setters for hotlinks.
 * <p>
 * Provides an implementation of getters and setters to the document linked attributes and their
 * values.
 * 
 * @author Naz Chan
 */
public class ShpHotlinkSource extends BasicHotlinkSource {

    public ShpHotlinkSource(ShpGeoResourceImpl resource) {
        super(resource);
        this.url = resource.service().getIdentifier();
    }

    @Override
    protected String decodeInfo(ContentType contentType, String featureInfo) {
        if (ContentType.FILE == contentType) {
            return ShpDocUtils.getAbsolutePath(url, featureInfo);
        }
        return super.decodeInfo(contentType, featureInfo);
    }

    @Override
    protected String encodeInfo(ContentType contentType, String documentInfo) {
        if (ContentType.FILE == contentType) {
            return ShpDocUtils.getRelativePath(url, documentInfo);
        }
        return super.encodeInfo(contentType, documentInfo);
    }
    
}
