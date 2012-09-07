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

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.document.IDocument;
import net.refractions.udig.catalog.document.IDocument.ContentType;
import net.refractions.udig.catalog.document.IHotlink;
import net.refractions.udig.catalog.document.IHotlinkSource;
import net.refractions.udig.catalog.internal.shp.ShpGeoResourceImpl;
import net.refractions.udig.document.model.AbstractHotlinkDocument;
import net.refractions.udig.document.model.ActionHotlinkDocument;
import net.refractions.udig.document.model.FileHotlinkDocument;
import net.refractions.udig.document.model.WebHotlinkDocument;

import org.eclipse.core.runtime.IProgressMonitor;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Provides shapefile hotlink support. This should only be available when we can determine what
 * attribute as storing hotlinks.
 * <p>
 * Provides an implementation of getters and setters to the document linked attributes and their
 * values.
 * 
 * @author Naz Chan
 */
public class ShpHotlinkSource extends AbstractShpDocumentSource implements IHotlinkSource {

    protected List<IDocument> docs;

    public ShpHotlinkSource(ShpGeoResourceImpl geoResource) {
        super(geoResource);
    }

    @Override
    public List<HotlinkDescriptor> getHotlinkDescriptors(SimpleFeature feature,
            IProgressMonitor monitor) {
        return propParser.getHotlinkDescriptors();
    }

    @Override
    public List<IDocument> getDocuments(SimpleFeature feature, IProgressMonitor monitor) {
        docs = new ArrayList<IDocument>();
        final List<HotlinkDescriptor> featureDescriptors = propParser.getHotlinkDescriptors();
        if (featureDescriptors != null && featureDescriptors.size() > 0) {
            final Map<String, List<HotlinkDescriptor>> descriptorMap = getDescriptorMap(featureDescriptors);
            for (String attributeName : descriptorMap.keySet()) {
                final List<HotlinkDescriptor> attributeDescriptors = descriptorMap
                        .get(attributeName);
                final String info = getInfo(feature, attributeDescriptors);
                docs.add(docFactory.create(info, attributeDescriptors));
            }
        }
        return docs;
    }

    private List<IDocument> getDocsInternal(SimpleFeature feature, IProgressMonitor monitor) {
        if (docs == null) {
            return getDocuments(feature, monitor);
        }
        return docs;
    }

    private String getInfo(SimpleFeature feature, List<HotlinkDescriptor> descriptors) {
        final HotlinkDescriptor descriptor = descriptors.get(0);
        final String info = (String) feature.getAttribute(descriptor.getAttributeName());
        if (ContentType.FILE == descriptor.getType()) {
            return ShpDocUtils.getAbsolutePath(url, info);
        }
        return info;
    }

    private Map<String, List<HotlinkDescriptor>> getDescriptorMap(
            List<HotlinkDescriptor> descriptors) {
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
    public IDocument getDocument(SimpleFeature feature, String attributeName,
            IProgressMonitor monitor) {
        for (IDocument doc : getDocsInternal(feature, monitor)) {
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
    public boolean canSetHotlink() {
        return true;
    }

    @Override
    public boolean setFile(SimpleFeature feature, String attributeName, File file,
            IProgressMonitor monitor) {
        final FileHotlinkDocument fileDoc = (FileHotlinkDocument) getDocument(feature,
                attributeName, monitor);
        fileDoc.setInfo(file.getAbsolutePath());
        feature.setAttribute(attributeName,
                ShpDocUtils.getRelativePath(url, file.getAbsolutePath()));
        return true;
    }

    @Override
    public boolean setLink(SimpleFeature feature, String attributeName, URL link,
            IProgressMonitor monitor) {
        final WebHotlinkDocument webDoc = (WebHotlinkDocument) getDocument(feature, attributeName,
                monitor);
        webDoc.setInfo(link.toString());
        feature.setAttribute(attributeName, link.toString());
        return true;
    }

    @Override
    public boolean setAction(SimpleFeature feature, String attributeName, String action,
            IProgressMonitor monitor) {
        final ActionHotlinkDocument actionDoc = (ActionHotlinkDocument) getDocument(feature,
                attributeName, monitor);
        actionDoc.setInfo(action);
        feature.setAttribute(attributeName, action);
        return true;
    }

    @Override
    public boolean canClearHotlink() {
        return true;
    }

    @Override
    public boolean clear(SimpleFeature feature, String attributeName, IProgressMonitor monitor) {
        final AbstractHotlinkDocument hotlinkDoc = (AbstractHotlinkDocument) getDocument(feature,
                attributeName, monitor);
        hotlinkDoc.setInfo(null);
        feature.setAttribute(attributeName, null);
        return true;
    }

}
