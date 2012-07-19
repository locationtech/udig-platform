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
 * This is the hotlink source interface. This is designed to be implemented by feature level
 * "hotlink" document sources.
 * 
 * @author nchan
 */
public interface IHotlinkSource extends IAbstractDocumentSource {

    /**
     * Gets the list of documents related to the feature. The list of documents is wrapped in a
     * document folder with the folder name.
     * 
     * @param folderName
     * @param fid
     * @return document folder
     */
    public IDocumentFolder getDocumentsInFolder(String folderName, FeatureId fid);

    /**
     * Gets the list of documents related to the feature. The list of documents is wrapped in a
     * document folder with the default folder name. 
     * 
     * @param fid
     * @return document folder
     */
    public IDocumentFolder getDocumentsInFolder(FeatureId fid);

    /**
     * Gets the list of documents related to the feature.
     * 
     * @param fid
     * @return list of documents
     */
    public List<IDocument> getDocuments(FeatureId fid);

    /**
     * Gets the document.
     * 
     * @param fid
     * @param attributeName
     * @return document
     */
    public IDocument getDocument(FeatureId fid, String attributeName);

    /**
     * Sets the file as the document linked to the attribute.
     * 
     * @param fid
     * @param attributeName
     * @param file
     * @return document
     */
    public IDocument setFile(FeatureId fid, String attributeName, File file);

    /**
     * Sets the link as the document linked to the attribute.
     * 
     * @param fid
     * @param attributeName
     * @param url
     * @return document
     */
    public IDocument setLink(FeatureId fid, String attributeName, URL url);

    /**
     * Removes the document from the attribute. This sets the attribute value to null.
     * 
     * @param fid
     * @param attributeName
     */
    public void remove(FeatureId fid, String attributeName);

}
