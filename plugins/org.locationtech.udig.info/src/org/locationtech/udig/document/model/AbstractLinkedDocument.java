/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.document.model;

import org.locationtech.udig.catalog.document.IDocumentSource.DocumentInfo;

/**
 * Abstract model for basic (non-hotlink) documents.
 * 
 * @author Naz Chan
 */
public abstract class AbstractLinkedDocument extends AbstractDocument {

    private DocumentInfo info;

    public AbstractLinkedDocument(DocumentInfo info) {
        setInfo(info);
    }
    
    public DocumentInfo getInfo() {
        return info;
    }

    public void setInfo(DocumentInfo info) {
        this.info = info;
    }

    @Override
    public String getLabel() {
        return info.getLabel();
    }

    @Override
    public String getDescription() {
        return info.getDescription();
    }

    @Override
    public ContentType getContentType() {
        return info.getContentType();
    }

    @Override
    public Type getType() {
        return info.getType();
    }
    
}
