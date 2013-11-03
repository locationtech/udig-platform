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

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.document.IDocument;
import org.locationtech.udig.catalog.document.IHotlink;
import org.locationtech.udig.catalog.document.IHotlinkSource;
import org.locationtech.udig.catalog.document.IDocument.ContentType;
import org.locationtech.udig.document.model.AbstractDocument;
import org.locationtech.udig.document.model.AbstractHotlinkDocument;
import org.locationtech.udig.document.model.ActionHotlinkDocument;
import org.locationtech.udig.document.model.FileHotlinkDocument;
import org.locationtech.udig.document.model.WebHotlinkDocument;

import org.eclipse.core.runtime.IProgressMonitor;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Basic hotlink document source implementation. This provides methods for hotlink documents access.
 * 
 * @author Naz Chan
 */
public class BasicHotlinkSource extends AbstractDocumentSource implements IHotlinkSource {
    
    private BasicHotlinkDescriptorParser parser;

    private List<HotlinkDescriptor> descriptors;
    private Map<String, List<HotlinkDescriptor>> attributeDescriptorMap;
    
    public BasicHotlinkSource(IGeoResource resource) {
        super(resource);
        this.resource = resource;
        this.parser = new BasicHotlinkDescriptorParser(resource);
    }
    
    @Override
    public boolean isEnabled() {
        return parser.isEnabled();
    }
    
    @Override
    public boolean isEnabledEditable() {
        return true;
    }
    
    @Override
    public boolean canSetHotlink() {
        return true;
    }

    @Override
    public boolean canClearHotlink() {
        return true;
    }
    
    @Override
    public List<HotlinkDescriptor> getHotlinkDescriptors(SimpleFeature feature,
            IProgressMonitor monitor) {
        descriptors = parser.getDescriptors();
        attributeDescriptorMap = getDescriptorMap(descriptors);
        return descriptors;
    }
    
    @Override
    public List<IDocument> getDocuments(SimpleFeature feature, IProgressMonitor monitor) {
        docs = new ArrayList<IDocument>();
        final List<HotlinkDescriptor> featureDescriptors = getHotlinkDescriptors(feature, monitor);
        if (featureDescriptors != null && featureDescriptors.size() > 0) {
            for (String attributeName : attributeDescriptorMap.keySet()) {
                final List<HotlinkDescriptor> attributeDescriptors = attributeDescriptorMap
                        .get(attributeName);
                final String info = (String) feature.getAttribute(attributeName);
                final String decodedInfo = decodeInfo(attributeDescriptors.get(0).getType(), info);
                final IDocument doc = create(decodedInfo, attributeDescriptors);
                setFeature(doc, feature);
                docs.add(doc);
            }
        }
        return docs;
    }

    /**
     * Gets the cached list of documents.
     * 
     * @param feature
     * @param monitor
     * @return documents
     */
    private List<IDocument> getDocsInternal(SimpleFeature feature, IProgressMonitor monitor) {
        if (docs == null) {
            return getDocuments(feature, monitor);
        }
        return docs;
    }
    
    @Override
    public IDocument getDocument(SimpleFeature feature, String attributeName, IProgressMonitor monitor) {
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
    public boolean setFile(SimpleFeature feature, String attributeName, File file,
            IProgressMonitor monitor) {
        return setHotlink(feature, attributeName, file.getAbsolutePath(), monitor);
    }

    @Override
    public boolean setLink(SimpleFeature feature, String attributeName, URL link,
            IProgressMonitor monitor) {
        return setHotlink(feature, attributeName, link.toString(), monitor);
    }

    @Override
    public boolean setAction(SimpleFeature feature, String attributeName, String action,
            IProgressMonitor monitor) {
        return setHotlink(feature, attributeName, action, monitor);
    }

    @Override
    public boolean clear(SimpleFeature feature, String attributeName, IProgressMonitor monitor) {
        return setHotlink(feature, attributeName, null, monitor);
    }
    
    /**
     * Gets the decoded hotlink value to be used by the document. This provides subclasses the
     * utility to customise how the hotlink values are encoded/decoded into the feature.
     * <p>
     * Example. file hotlinks may be stored as relative file path in the feature - this decodes the
     * path to an absolute path
     * 
     * @param contentType
     * @param featureInfo
     * @return decoded hotlink value
     */
    protected String decodeInfo(ContentType contentType, String featureInfo) {
        return featureInfo;
    }

    /**
     * Gets the encoded hotlink value to be used by the feature. This provides subclasses the
     * utility to customise how the hotlink values are encoded/decoded into the feature.
     * <p>
     * Example. file hotlinks may be stored as relative file path in the feature - this encodes the
     * path to a relative path
     * 
     * @param contentType
     * @param documentInfo
     * @return encoded hotlink value
     */
    protected String encodeInfo(ContentType contentType, String documentInfo) {
        return documentInfo;
    }
    
    /**
     * Sets the hotlink attribute value.
     * 
     * @param feature
     * @param attributeName
     * @param value
     * @param monitor
     * @return true if successful, otherwise false
     */
    private boolean setHotlink(SimpleFeature feature, String attributeName, String value,
            IProgressMonitor monitor) {
        final AbstractHotlinkDocument doc = (AbstractHotlinkDocument) getDocument(feature,
                attributeName, monitor);
        final String encodedValue = encodeInfo(doc.getContentType(), value);
        doc.setInfo(value);
        feature.setAttribute(attributeName, encodedValue);
        return true;
    }
    
    /**
     * Maps the descriptors per attribute as an attribute can have more than one descriptor.
     * 
     * @param descriptors
     * @return attribute descriptor map
     */
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
    
    /**
     * Creates a document from the info (is the attribute value) and the list of hotlink descriptors
     * related to an attribute.
     * 
     * @param info
     * @param descriptors
     * @return document
     */
    private IDocument create(String info, List<HotlinkDescriptor> descriptors) {
        AbstractDocument doc = null;
        switch (descriptors.get(0).getType()) {
        case FILE:
            doc = new FileHotlinkDocument(info, descriptors);
            break;
        case WEB:
            doc = new WebHotlinkDocument(info, descriptors);
            break;
        case ACTION:
            doc = new ActionHotlinkDocument(info, descriptors);
            break;
        default:
            break;
        }
        if (doc != null) {
            doc.setSource(this);    
        }
        return doc;
    }
    
}
