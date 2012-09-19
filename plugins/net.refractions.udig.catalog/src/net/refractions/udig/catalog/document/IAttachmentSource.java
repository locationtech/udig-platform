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
package net.refractions.udig.catalog.document;

import java.util.List;

import net.refractions.udig.catalog.document.IDocumentSource.DocumentInfo;

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
