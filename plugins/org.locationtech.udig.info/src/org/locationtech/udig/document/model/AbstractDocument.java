/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.document.model;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.locationtech.udig.catalog.document.IDocument;

import org.eclipse.swt.program.Program;

/**
 * Abstract model for documents.
 * 
 * @author paul.pfeiffer
 * @author Naz Chan 
 */
public abstract class AbstractDocument extends AbstractDocumentItem implements IDocument {
        
    @Override
    public boolean isEmpty() {
        return (getContent() == null);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IDocument) {
            final IDocument doc = (IDocument) obj;
            final boolean isEqualDocType = getType() == doc.getType();
            final boolean isEqualType = getContentType() == doc.getContentType();
            final boolean isEqualLabel = isEqual(getLabel(), doc.getLabel());
            final boolean isEqualDescription = isEqual(getDescription(), doc.getDescription());
            final boolean isEqualSource = getSource() == doc.getSource();
            final boolean isEqualValue = getContent() == doc.getContent();
            return isEqualDocType && isEqualType && isEqualLabel && isEqualDescription
                    && isEqualSource && isEqualValue;
        }
        return super.equals(obj);
    }
    
    /**
     * Checks equality between two strings, this checks if the strings are null to avoid NPEs.
     * 
     * @param str1
     * @param str2
     * @return true if equal, otherwise false
     */
    private boolean isEqual(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        } else if (str1 == null && str2 != null) {
            return false;
        } else if (str1 != null && str2 == null) {
            return false;
        } else if (str1 != null && str2 != null) {
            return str1.equals(str2);
        }
        return false;
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
            return newFile;
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
