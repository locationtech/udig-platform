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
package net.refractions.udig.document.model;

import net.refractions.udig.catalog.document.IDocumentSource.DocumentInfo;

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
