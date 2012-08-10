/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
package net.refractions.udig.catalog.document;

import java.io.File;
import java.net.URL;
import java.util.List;


/**
 * Access to documents associated with a resource.
 * <p>
 * The files made available are dependent on the implementation - some possible examples are:
 * <ul>
 * <li>Support files associated with an IGeoResource</li>
 * <li>Additional sidecar files associated with a shapefile</li>
 * </ul>
 * 
 * @author nchan
 * @since 1.3.2
 */
public interface IDocumentSource extends IAbstractDocumentSource {

    /**
     * Gets the list of documents associated with this feature type.
     * <p>
     * As an example this will return a SHAPEFILENAME.TXT file that is associated (ie a sidecar
     * file) with the provided shapefile. We may also wish to list a README.txt file in the same
     * directory (as it is the habbit of GIS professionals to record fun information about the
     * entire dataset.
     * </p>
     * 
     * @return documents
     */
    public List<IDocument> getDocuments();

    /**
     * Checks if the source allows adding new documents.
     * 
     * @return true if allows adding, otherwise false
     */
    public boolean canAdd();

    /**
     * Adds the link.
     * 
     * @param url
     */
    public IDocument addLink(URL url);

    /**
     * Adds the file.
     * 
     * @param file
     */
    public IDocument addFile(File file);

    /**
     * Adds the list of files.
     * 
     * @param files
     */
    public List<IDocument> addFiles(List<File> files);

    /**
     * Checks if the source allows removing.
     * 
     * @return true if allows removing, otherwise false
     */
    public boolean canRemove();

    /**
     * Removes the document.
     * 
     * @param doc
     */
    public void remove(IDocument doc);

    /**
     * Removes the list of documents.
     * 
     * @param docs
     */
    public void remove(List<IDocument> docs);

    /**
     * Checks if the source allows updating.
     * 
     * @return true if allows updating, otherwise false
     */
    public boolean canUpdate();
    
    /**
     * Updates the file of the document which is required to be
     * of type {@link IDocument.Type#FILE}.
     * 
     * @param doc
     * @param file
     * @return true successful, otherwise false
     */
    public boolean updateFile(IDocument doc, File file);
    
    /**
     * Updates the url of the document which is required to be
     * of type {@link IDocument.Type#FILE}.
     * 
     * @param doc
     * @param url
     * @return true successful, otherwise false
     */
    public boolean updateLink(IDocument doc, URL url);
    
}
