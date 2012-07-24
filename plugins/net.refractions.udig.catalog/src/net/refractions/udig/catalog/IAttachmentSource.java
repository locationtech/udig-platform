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
package net.refractions.udig.catalog;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.opengis.filter.identity.FeatureId;

/**
 * This is the attachment source interface. This is designed to be implemented by feature level
 * "attachment" document sources.
 * 
 * @author nchan
 */
public interface IAttachmentSource extends IAbstractDocumentSource {

    /**
     * Gets the list of documents related to the feature
     * 
     * @param fid
     * @return list of documents
     */
    public List<IDocument> documents(FeatureId fid);

    /**
     * Adds the file.
     * 
     * @param fid
     * @param file
     * @return document
     */
    public IDocument addFile(FeatureId fid, File file);

    /**
     * Adds the link.
     * 
     * @param fid
     * @param url
     * @return document
     */
    public IDocument addLink(FeatureId fid, URL url);

    /**
     * Updates the url of the document.
     * 
     * @param fid
     * @param urlDoc
     * @param url
     * @return true if successful, otherwise false
     */
    public boolean updateLink(FeatureId fid, URLDocument urlDoc, URL url);

    /**
     * Deletes the document
     * 
     * @param fid
     * @param doc
     * @return true if successful, otherwise false
     */
    public boolean removeDoc(FeatureId fid, IDocument doc);

}
