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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.document.IDocument;
import net.refractions.udig.catalog.document.IHotlinkSource;
import net.refractions.udig.catalog.document.IDocument.Type;
import net.refractions.udig.catalog.internal.document.LinkInfo;
import net.refractions.udig.catalog.internal.shp.ShpGeoResourceImpl;

import org.opengis.feature.simple.SimpleFeature;

/**
 * Provides shapefile hotlink support. This should only be available when
 * we can determine what attribute as storing hotlinks.
 * <p>
 * Provides an implementation of getters and setters to the document linked attributes and their values.
 * 
 * @author Naz Chan
 */
public class ShpHotlinkSource extends AbstractShpDocumentSource implements IHotlinkSource {

    public ShpHotlinkSource(ShpGeoResourceImpl geoResource) {
        super(geoResource);
    }

    @Override
    public List<HotlinkDescriptor> getHotlinkDescriptors() {
        final List<HotlinkDescriptor> hotlinkDescriptors = new ArrayList<IHotlinkSource.HotlinkDescriptor>();
        for (LinkInfo info : propParser.getFeatureLinkInfos()) {
            HotlinkDescriptor descriptor = new HotlinkDescriptor(info.getInfo(), info.getType());
            hotlinkDescriptors.add(descriptor);
        }
        return hotlinkDescriptors;
    }

    @Override
    public List<IDocument> getDocuments(SimpleFeature feature) {
        return propParser.getFeatureLinks(feature);
    }

    @Override
    public IDocument getDocument(SimpleFeature feature, String attributeName) {
        final String spec = (String) feature.getAttribute(attributeName);
        final Type type = propParser.getFeatureLinkInfo(attributeName).getType();
        return docFactory.create(url, spec, type);
    }

    @Override
    public IDocument setFile(SimpleFeature feature, String attributeName, File file) {
        feature.setAttribute(attributeName, propParser.getFileLinkValue(file));
        return docFactory.create(file);
    }

    @Override
    public IDocument setLink(SimpleFeature feature, String attributeName, URL url) {
        feature.setAttribute(attributeName, propParser.getUrlLinkValue(url));
        return docFactory.create(url);
    }

    @Override
    public IDocument clear(SimpleFeature feature, String attributeName) {
        feature.setAttribute(attributeName, null);
        return null;
    }
    
}
