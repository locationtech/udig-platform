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

import net.refractions.udig.catalog.document.IDocument.ContentType;
import net.refractions.udig.catalog.internal.shp.ShpGeoResourceImpl;

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
        // if (ContentType.FILE == contentType) {
        // return ShpDocUtils.getAbsolutePath(url, featureInfo);
        // }
        return super.decodeInfo(contentType, featureInfo);
    }
    
    @Override
    protected String encodeInfo(ContentType contentType, String documentInfo) {
        // if (ContentType.FILE == contentType) {
        // return ShpDocUtils.getRelativePath(url, documentInfo);
        // }
        return super.encodeInfo(contentType, documentInfo);
    }
    
}
