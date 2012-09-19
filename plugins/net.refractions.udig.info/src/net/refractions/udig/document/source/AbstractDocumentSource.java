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

import java.net.URL;
import java.util.List;

import org.opengis.feature.simple.SimpleFeature;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.document.IAbstractDocumentSource;
import net.refractions.udig.catalog.document.IDocument;
import net.refractions.udig.document.model.AbstractDocument;

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
