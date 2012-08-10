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

import java.io.File;
import java.net.URL;
import java.util.List;

import org.opengis.filter.identity.FeatureId;

/**
 * Attachment support for a IGeoResource providing the ability to record IAttachment documents
 * against individual features.
 * 
 * @author Naz Chan (LISAsoft)
 * @since 1.3.2
 */
public interface IAttachmentSource extends IAbstractDocumentSource {

    /**
     * Gets the list of documents related to the feature.
     * 
     * @param fid
     * @return list of documents
     */
    public List<IDocument> getDocuments(FeatureId fid);

    /**
     * Adds the file.
     * 
     * @param fid
     * @param file
     * @return document
     */
    public IDocument addFile(FeatureId fid, File file);
    
    public List<IDocument> addFiles(FeatureId fid, List<File> files);

    /**
     * Adds the link.
     * 
     * @param fid
     * @param url
     * @return document
     */
    public IDocument addLink(FeatureId fid, URL url);

    /**
     * Updates the file of the document, doc is required to be of
     * type {@link IDocument.Type#FILE}.
     * 
     * @param fid
     * @param fileDoc
     * @param file
     * @return file set to document
     */
    public File updateFile(FeatureId fid, IDocument doc, File file);
    
    /**
     * Updates the url of the document which is required to be of
     * type {@link IDocument.Type#WEB}.
     * 
     * @param fid
     * @param urlDoc
     * @param url
     * @return true if successful, otherwise false
     */
    public boolean updateLink(FeatureId fid, IDocument doc, URL url);

    /**
     * Deletes the document
     * 
     * @param fid
     * @param doc
     * @return true if successful, otherwise false
     */
    public boolean remove(FeatureId fid, IDocument doc);
    
    /**
     * Deletes the list of documents
     * 
     * @param fid
     * @param docs
     * @return true if successfull, otherwise false
     */
    public boolean remove(FeatureId fid, List<IDocument> docs);

}
