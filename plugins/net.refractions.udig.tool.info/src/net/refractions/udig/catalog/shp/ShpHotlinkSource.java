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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.document.IDocument;
import net.refractions.udig.catalog.document.IDocument.Type;
import net.refractions.udig.catalog.document.IHotlink;
import net.refractions.udig.catalog.document.IHotlinkSource;
import net.refractions.udig.catalog.internal.document.AbstractHotlinkDocument;
import net.refractions.udig.catalog.internal.document.HotlinkActionDocument;
import net.refractions.udig.catalog.internal.document.HotlinkFileDocument;
import net.refractions.udig.catalog.internal.document.HotlinkWebDocument;
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

    protected List<IDocument> docs;
    
    public ShpHotlinkSource(ShpGeoResourceImpl geoResource) {
        super(geoResource);
    }

    @Override
    public List<HotlinkDescriptor> getHotlinkDescriptors() {
        return propParser.getHotlinkDescriptors();
    }

    @Override
    public List<IDocument> getDocuments(SimpleFeature feature) {
        docs = new ArrayList<IDocument>();
        final List<HotlinkDescriptor> featureDescriptors = propParser.getHotlinkDescriptors();
        if (featureDescriptors != null && featureDescriptors.size() > 0) {
            final Map<String, List<HotlinkDescriptor>> descriptorMap = getDescriptorMap(featureDescriptors);
            for (String attributeName : descriptorMap.keySet()) {
                final List<HotlinkDescriptor> attributeDescriptors = descriptorMap.get(attributeName); 
                final String info = getInfo(feature, attributeDescriptors);
                docs.add(docFactory.create(info, attributeDescriptors));
            }                       
        }
        return docs;
    }

    private List<IDocument> getDocsInternal(SimpleFeature feature) {
        if (docs == null) {
            return getDocuments(feature);
        }
        return docs;
    }
    
    private String getInfo(SimpleFeature feature, List<HotlinkDescriptor> descriptors) {
        final HotlinkDescriptor descriptor = descriptors.get(0);
        final String info = (String) feature.getAttribute(descriptor.getAttributeName());
        if (Type.FILE == descriptor.getType()) {
            return ShpDocUtils.getAbsolutePath(url, info);
        }
        return info; 
    }
    
    private Map<String, List<HotlinkDescriptor>> getDescriptorMap(List<HotlinkDescriptor> descriptors) {
        final Map<String, List<HotlinkDescriptor>> descriptorMap = new HashMap<String, List<HotlinkDescriptor>>();
        for (HotlinkDescriptor descriptor : descriptors) {
            final String attributeName = descriptor.getAttributeName();
            if (descriptorMap.containsKey(attributeName)) {
                descriptorMap.get(attributeName).add(descriptor);
            } else {
                final ArrayList<HotlinkDescriptor> attributeDescriptors = new ArrayList<HotlinkDescriptor>();
                attributeDescriptors.add(descriptor);
                descriptorMap.put(attributeName, attributeDescriptors);
            }
        }
        return descriptorMap;
    }
    
    @Override
    public IDocument getDocument(SimpleFeature feature, String attributeName) {
        for (IDocument doc : getDocsInternal(feature)) {
            if (doc instanceof IHotlink) {
                final IHotlink hotlinkDoc = (IHotlink) doc;
                if (attributeName.equals(hotlinkDoc.getAttributeName())) {
                    return hotlinkDoc;
                }                
            }
        }
        return null;
    }

    @Override
    public IDocument setFile(SimpleFeature feature, String attributeName, File file) {
        final HotlinkFileDocument fileDoc = (HotlinkFileDocument) getDocument(feature, attributeName);
        fileDoc.setInfo(file.getAbsolutePath());
        feature.setAttribute(attributeName, ShpDocUtils.getRelativePath(url, file.getAbsolutePath()));
        return fileDoc;
    }

    @Override
    public IDocument setLink(SimpleFeature feature, String attributeName, URL link) {
        final HotlinkWebDocument webDoc = (HotlinkWebDocument) getDocument(feature, attributeName);
        webDoc.setInfo(link.toString());
        feature.setAttribute(attributeName, link.toString());
        return webDoc;
    }

    @Override
    public IDocument setAction(SimpleFeature feature, String attributeName, String action) {
        final HotlinkActionDocument actionDoc = (HotlinkActionDocument) getDocument(feature, attributeName);
        actionDoc.setInfo(action);
        feature.setAttribute(attributeName, action);
        return actionDoc;
    }

    @Override
    public IDocument clear(SimpleFeature feature, String attributeName) {
        final AbstractHotlinkDocument hotlinkDoc = (AbstractHotlinkDocument) getDocument(feature, attributeName);
        hotlinkDoc.setInfo(null);
        feature.setAttribute(attributeName, null);
        return hotlinkDoc;
    }
    
}
