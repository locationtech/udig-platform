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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import net.refractions.udig.catalog.document.IAbstractDocumentSource;
import net.refractions.udig.catalog.document.IDocument;
import net.refractions.udig.catalog.document.IDocumentFolder;
import net.refractions.udig.catalog.document.IDocument.Type;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

/**
 * Utility class that creates documents and document folders.
 * 
 * @author Naz Chan
 */
public class DocumentFactory {

    private IAbstractDocumentSource source;
    
    public DocumentFactory(IAbstractDocumentSource source) {
        this.source = source;
    }
    
    /**
     * Create a document from the URL.
     * 
     * @param url
     * @return document
     */
    public IDocument create(URL url) {
        return createUrlDoc(url, null);
    }

    /**
     * Create a document from the file.
     * 
     * @param file
     * @return document
     */
    public IDocument create(File file) {
        return createFileDoc(file, null);
    }

    /**
     * Create a document from the metadata.
     * 
     * @param url
     * @param info
     * @param type
     * @return document
     */
    public IDocument create(URL url, String info, IDocument.Type type) {
        return createDoc(url, null, info, type);
    }
    
    /**
     * Create a list of documents from the feature and metadata.
     * 
     * @param url
     * @param feature
     * @param infos
     * @return list of documents
     */
    public List<IDocument> createList(URL url, SimpleFeature feature, List<LinkInfo> infos) {
        final List<IDocument> docs = new ArrayList<IDocument>();
        if (infos != null && infos.size() > 0) {
            for (LinkInfo info : infos) {
                final String attributeName = info.getInfo();
                if (attributeExists(feature, attributeName)) {
                    final String spec = (String) feature.getAttribute(attributeName);
                    final AbstractDocument doc = createDoc(url, info.getLabel(), spec, info.getType());
                    if( doc instanceof FileDocument ){
                        ((FileDocument)doc).setAttributeName(attributeName);
                    }
                    if( doc instanceof URLDocument ){
                        ((URLDocument)doc).setAttributeName(attributeName);
                    }
                    docs.add(doc);    
                }
            }
        }
        return docs;
    }
    
    /**
     * Checks if the attribute exists in the feature.
     * 
     * @param feature
     * @param attributeName
     * @return true if exists, otherwise false
     */
    private boolean attributeExists(SimpleFeature feature, String attributeName) {
        final SimpleFeatureType featureType = feature.getFeatureType();
        final AttributeDescriptor attributeDescriptor = featureType.getDescriptor(attributeName); 
        if (attributeDescriptor != null) {
            return true;
        }
        return false;
    }
    
    /**
     * Create a list of documents from the metadata.
     * 
     * @param url
     * @param infos
     * @return list of documents
     */
    public List<IDocument> createList(URL url, List<LinkInfo> infos) {
        final List<IDocument> docs = new ArrayList<IDocument>();
        if (infos != null && infos.size() > 0) {
            for (LinkInfo info : infos) {
                String attributeName = info.getInfo();
                Type type = info.getType();
                AbstractDocument document = createDoc(url, info.getLabel(), attributeName, type);
                
                docs.add(document);
            }
        }
        return docs;
    }

    /**
     * Create a document folder.
     * 
     * @param name
     * @return document folder
     */
    public static IDocumentFolder createFolder(String name, IAbstractDocumentSource source) {
        final DocumentFolder folder = new DocumentFolder(name, source); 
        return  folder;
    }
    
    /**
     * Creates a URL document.
     * 
     * @param url
     * @param label
     * @return URL document
     */
    private URLDocument createUrlDoc(URL url, String label) {
        URLDocument urlDoc = new URLDocument();
        if (url != null) {
            urlDoc = new URLDocument((URL) url);
        }
        urlDoc.setLabel(label);
        urlDoc.setSource(source);
        return urlDoc;
    }

    /**
     * Creates a file document.
     * 
     * @param file
     * @param label
     * @return file document
     */
    private FileDocument createFileDoc(File file, String label) {
        FileDocument fileDoc = new FileDocument();
        if (file != null) {
            fileDoc = new FileDocument((File) file);
        }
        fileDoc.setLabel(label);
        fileDoc.setSource(source);
        return fileDoc;
    }
    
    /**
     * Creates an abstract document from the metadata.
     * 
     * @param url
     * @param label
     * @param info
     * @param type
     * @return abstract document
     */
    public AbstractDocument createDoc(URL url, String label, String info, IDocument.Type type) {
        switch (type) {
        case FILE:
            try {
                if (info != null && info.length() > 0) {
                    final File parentFile = new File(new URI(url.toString()));
                    final File childFile = new File(parentFile.getParent(), info);
                    return createFileDoc(childFile, label);    
                }
            } catch (URISyntaxException e) {
                // e.printStackTrace();
            }
            return createFileDoc(null, label);
        case WEB:
            try {
                if (info != null && info.length() > 0) {
                    return createUrlDoc(new URL(info), label);    
                }
            } catch (MalformedURLException e) {
                // e.printStackTrace();
            }
            return createUrlDoc(null, label);
        default:
            return null;
        }
    }
    
}

