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

import java.net.URL;
import java.util.List;

import org.opengis.feature.simple.SimpleFeature;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.document.IAbstractDocumentSource;
import org.locationtech.udig.catalog.document.IDocument;
import org.locationtech.udig.document.model.AbstractDocument;

/**
 * The base class for document sources.
 * 
 * @author Naz Chan
 */
public abstract class AbstractDocumentSource implements IAbstractDocumentSource {

    protected URL url;
    protected IGeoResource resource;
    
    protected List<IDocument> docs;
    
    public AbstractDocumentSource(IGeoResource resource) {
        this.url = resource.getIdentifier();
        this.resource = resource;
    }
    
    /**
     * Sets the related feature to the document.
     * 
     * @param doc
     * @param feature
     */
    protected void setFeature(IDocument doc, SimpleFeature feature) {
        ((AbstractDocument) doc).setFeature(feature);
    }
    
    /**
     * Sets the related feature to the documents.
     * 
     * @param docs
     * @param feature
     */
    protected void setFeature(List<IDocument> docs, SimpleFeature feature) {
        for (IDocument doc : docs) {
            setFeature(doc, feature);
        }
    }
    
}
