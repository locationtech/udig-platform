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

import java.io.File;

import net.refractions.udig.catalog.document.IDocumentSource.DocumentInfo;

/**
 * Document model for file documents.
 * 
 * @author Naz Chan
 */
public class FileLinkedDocument extends AbstractLinkedDocument {

    protected File file;

    public FileLinkedDocument(DocumentInfo info) {
        super(info);
    }
    
    @Override
    public void setInfo(DocumentInfo info) {
        super.setInfo(info);
        if (info != null) {
            file = AbstractDocument.createFile(info.getInfo());
        }
    }
    
    @Override
    public Object getContent() {
        return file;
    }
    
    @Override
    public String getContentName() {
        if (!isEmpty()) {
            return file.getName();
        }
        return null;
    }
    
    @Override
    public boolean open() {
        return AbstractDocument.openFile(file);
    }

    @Override
    public boolean isEmpty() {
        return (file == null);
    }
    
    @Override
    public boolean isTemplate() {
        return getInfo().isTemplate();
    }
    
    public void setTemplate(boolean isTemplate) {
        getInfo().setTemplate(isTemplate);
    }
    
}
