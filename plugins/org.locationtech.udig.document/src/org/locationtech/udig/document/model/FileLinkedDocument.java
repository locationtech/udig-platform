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

import java.io.File;

import org.locationtech.udig.catalog.document.IDocumentSource.DocumentInfo;

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
