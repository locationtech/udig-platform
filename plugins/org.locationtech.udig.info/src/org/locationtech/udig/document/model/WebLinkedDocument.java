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

import java.net.URL;

import org.locationtech.udig.catalog.document.IDocumentSource.DocumentInfo;

/**
 * Document model for web documents.
 * 
 * @author Naz Chan
 */
public class WebLinkedDocument extends AbstractLinkedDocument {

    protected URL url;
    
    public WebLinkedDocument(DocumentInfo info) {
        super(info);
    }
    
    @Override
    public void setInfo(DocumentInfo info) {
        super.setInfo(info);
        if (info != null) {
            url = AbstractDocument.createUrl(info.getInfo());
        }
    }
    
    @Override
    public Object getContent() {
        return url;
    }

    @Override
    public String getContentName() {
        if (!isEmpty()) {
            return url.toString();
        }
        return null;
    }
    
    @Override
    public boolean open() {
        return AbstractDocument.openUrl(url);
    }

    @Override
    public boolean isEmpty() {
        return (url == null);
    }
    
    @Override
    public boolean isTemplate() {
        return false; // Web documents cannot be templates
    }
    
}
