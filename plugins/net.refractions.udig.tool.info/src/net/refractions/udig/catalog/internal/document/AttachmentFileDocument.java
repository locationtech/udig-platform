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
package net.refractions.udig.catalog.internal.document;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import net.refractions.udig.catalog.document.IAttachment;
import net.refractions.udig.catalog.document.IDocumentSource.DocumentInfo;

/**
 * Document model for attachment file documents.
 * 
 * @author Naz Chan
 */
public class AttachmentFileDocument extends FileDocument implements IAttachment {

    public AttachmentFileDocument(DocumentInfo info) {
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
