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

import org.opengis.feature.simple.SimpleFeature;

/**
 * Attachment support for a IGeoResource providing the ability to record IAttachment documents
 * against individual features.
 * 
 * @author Naz Chan (LISAsoft)
 * @since 1.3.2
 */
public interface IAttachmentSource extends IHotlinkSource {

    /**
     * Gets the list of attachments related to the feature.
     * 
     * @param fid
     * @return list of documents
     */
    public List<IDocument> getDocuments(SimpleFeature feature);
    
    /**
     * Checks if the source allows attaching new documents.
     * 
     * @return true if allows attaching, otherwise false
     */
    public boolean canAttach();
    
    /**
     * Checks if the source allows linking file documents.
     * 
     * @return true if allows linking, otherwise false
     */
    public boolean canLinkFile();
    
    /**
     * Checks if the source allows linking web documents.
     * 
     * @return true if allows linking, otherwise false
     */
    public boolean canLinkWeb();
    
    /**
     * Adds the document.
     * 
     * @param fid
     * @param file
     * @return document
     */
    public IDocument add(SimpleFeature feature, DocumentInfo info);

    /**
     * Adds the list of documents.
     * 
     * @param fid
     * @param infos
     * @return list of documents
     */
    public List<IDocument> add(SimpleFeature feature, List<DocumentInfo> infos);
    
    /**
     * Checks if the source allows updating documents.
     * 
     * @return true if allows updating, otherwise false
     */
    public boolean canUpdate();
    
    /**
     * Updates the document info.
     * 
     * @param fid
     * @param fileDoc
     * @param file
     * @return updated document
     */
    public IDocument update(SimpleFeature feature, IDocument doc, DocumentInfo info);
    
    /**
     * Checks if the source allows removing documents.
     * 
     * @return true if allows removing or clearing, otherwise false
     */
    public boolean canRemove();
    
    /**
     * Removes the document.
     * 
     * @param fid
     * @param doc
     * @return true if successful, otherwise false
     */
    public boolean remove(SimpleFeature feature, IDocument doc);

    /**
     * Removes the list of documents.
     * 
     * @param fid
     * @param docs
     * @return true if successfull, otherwise false
     */
    public boolean remove(SimpleFeature feature, List<IDocument> docs);

}
