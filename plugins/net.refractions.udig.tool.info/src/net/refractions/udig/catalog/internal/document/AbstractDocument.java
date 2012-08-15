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
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.swt.program.Program;

import net.refractions.udig.catalog.document.IAbstractDocumentSource;
import net.refractions.udig.catalog.document.IDocument;

/**
 * Abstract model for documents.
 * 
 * @author paul.pfeiffer
 * @author Naz Chan 
 */
public abstract class AbstractDocument implements IDocument {
    
    protected IAbstractDocumentSource source;

    public IAbstractDocumentSource getSource() {
        return source;
    }

    public void setSource(IAbstractDocumentSource source) {
        this.source = source;
    }
    
    @Override
    public boolean isEmpty() {
        return (getValue() == null);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IDocument) {
            final IDocument doc = (IDocument) obj;
            final boolean isEqualDocType = getDocType() == doc.getDocType();  
            final boolean isEqualType = getType() == doc.getType();
            final boolean isEqualLabel = getLabel() == doc.getLabel();
            final boolean isEqualDescription = getDescription() == doc.getDescription();
            final boolean isEqualSource = getSource() == doc.getSource();
            final boolean isEqualValue = getValue() == doc.getValue();
            return isEqualDocType && isEqualType && isEqualLabel && isEqualDescription
                    && isEqualSource && isEqualValue;
        }
        return super.equals(obj);
    }
    
    /**
     * Creates a new file from the file specification.
     * 
     * @param fileSpec
     * @return file
     */
    protected static File createFile(String fileSpec) {
        if (fileSpec != null) {
            final File newFile = new File(fileSpec);
            if (newFile.exists()) {
                return newFile;
            }
        }
        return null;
    }
    
    /**
     * Opens the file with the assigned default program.
     * 
     * @param file
     * @return true if file is opened successfully, otherwise false
     */
    protected static boolean openFile(File file) {
        if (file != null) {
            return Program.launch(file.getAbsolutePath());        
        }
        return false;
    }
    
    /**
     * Creates a new url from the url specification.
     * 
     * @param urlSpec
     * @return url
     */
    protected static URL createUrl(String urlSpec) {
        if (urlSpec != null) {
            try {
                final URL newUrl = new URL(urlSpec);
                return newUrl;
            } catch (MalformedURLException e) {
                // Info is not a valid URL string
            }    
        }
        return null;
    }
    
    /**
     * Opens the url with the assigned default web browser.
     * 
     * @param url
     * @return true if url is opened successfully, otherwise false
     */
    protected static boolean openUrl(URL url) {
        if (url != null) {
            return Program.launch(url.toString());        
        }
        return false;
    }
    
}
