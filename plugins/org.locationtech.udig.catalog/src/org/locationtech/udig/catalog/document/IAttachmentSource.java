/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.document;

import java.util.List;

import org.locationtech.udig.catalog.document.IDocumentSource.DocumentInfo;

import org.eclipse.core.runtime.IProgressMonitor;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Access to documents associated with a feature.
 * 
 * @author Naz Chan (LISAsoft)
 * @since 1.3.2
 */
public interface IAttachmentSource extends IAbstractAttachmentSource {

    /**
     * Gets the list of documents.
     * </p>
     * This method may require server-side processing and should not be ran in the UI thread.
     * </p>
     * 
     * @param feature
     * @param monitor
     * @return documents
     */
    public List<IDocument> getDocuments(SimpleFeature feature, IProgressMonitor monitor);
    
    /**
     * Adds a document.
     * </p>
     * This method may require server-side processing and should not be ran in the UI thread.
     * </p>
     * 
     * @param feature
     * @param info
     * @param monitor
     * @return document
     */
    public IDocument add(SimpleFeature feature, DocumentInfo info, IProgressMonitor monitor);

    /**
     * Adds a list of documents.
     * </p>
     * This method may require server-side processing and should not be ran in the UI thread.
     * </p>
     * 
     * @param feature
     * @param infos
     * @param monitor
     * @return documents
     */
    public List<IDocument> add(SimpleFeature feature, List<DocumentInfo> infos, IProgressMonitor monitor);
    
    /**
     * Updates the document.
     * </p>
     * This method may require server-side processing and should not be ran in the UI thread.
     * </p>
     * 
     * @param feature
     * @param doc
     * @param info
     * @param monitor
     * @return true if successful, otherwise false
     */
    public boolean update(SimpleFeature feature, IDocument doc, DocumentInfo info, IProgressMonitor monitor);
    
    /**
     * Removes the document.
     * </p>
     * This method may require server-side processing and should not be ran in the UI thread.
     * </p>
     * 
     * @param feature
     * @param doc
     * @param monitor
     * @return true if successful, otherwise false
     */
    public boolean remove(SimpleFeature feature, IDocument doc, IProgressMonitor monitor);

    /**
     * Removes the list of documents.
     * </p>
     * This method may require server-side processing and should not be ran in the UI thread.
     * </p>
     * 
     * @param feature
     * @param docs
     * @param monitor
     * @return true if successful, otherwise false
     */
    public boolean remove(SimpleFeature feature, List<IDocument> docs, IProgressMonitor monitor);

}
