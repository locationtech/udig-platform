/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2011, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.catalog.internal.document;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.refractions.udig.catalog.document.IDocument;
import net.refractions.udig.catalog.document.IDocument.Type;
import net.refractions.udig.catalog.document.IHotlink;

import org.eclipse.swt.program.Program;

/**
 * This is the file document implementation. This acts as a container to the file itself and
 * provides getters for file related metadata.
 * 
 * @author paul.pfeiffer
 * @author Naz Chan
 */
public class FileDocument extends AbstractDocument implements IHotlink {

    private String attributeName;
    private File file;

    private static final String DATE_FORMAT = "dd-MM-yyyy hh:mm:ss"; //$NON-NLS-1$
    
    public FileDocument() {
        // Nothing
    }
    

    @Override
    public String getAttributeName() {
        return attributeName;
    }
    
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
    
    
    public FileDocument(File file) {
        this.file = file;
    }
    
    public File getFile() {
        return file;
    }
    
    public void setFile(File file) {
        this.file = file;
    }
    
    @Override
    public String getName() {
        if (file != null) {
            final String detail = file.getName(); 
            if (label != null) {
                return String.format(LABEL_FORMAT, label, detail); 
            }
            return detail;    
        } else {
            if (label != null) {
                return String.format(LABEL_FORMAT, label, UNASSIGNED); 
            }
            return UNASSIGNED_NO_LABEL;
        }
    }

    @Override
    public String getDescription() {
        if (file != null) {
            final Date modeDate = new Date(file.lastModified());
            return file.getName() + " modified on " //$NON-NLS-1$
                    + (new SimpleDateFormat(DATE_FORMAT)).format(modeDate);    
        }
        return UNASSIGNED_NO_LABEL;
    }

    @Override
    public URI getUri() {
        if (file != null) {
            return file.toURI();    
        }
        return null;
    }

    @Override
    public boolean open() {
        if (file != null) {
            return Program.launch(file.getAbsolutePath(), null);        
        }
        return false;
    }

    @Override
    public Type getType() {
        return Type.FILE;
    }

    @Override
    public boolean isEmpty() {
        return (file == null);
    }

}
