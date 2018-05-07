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
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import org.locationtech.udig.catalog.document.IAttachment;
import org.locationtech.udig.catalog.document.IDocumentSource.DocumentInfo;

/**
 * Document model for attachment file documents.
 * 
 * @author Naz Chan
 */
public class FileAttachmentDocument extends FileLinkedDocument implements IAttachment {

    public FileAttachmentDocument(DocumentInfo info) {
        super(info);
    }
 
    @Override
    public boolean saveAs(File newfile) {
        try {
            FileUtils.copyFile(file, newfile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Type getType() {
        return Type.ATTACHMENT;
    }
    
}
