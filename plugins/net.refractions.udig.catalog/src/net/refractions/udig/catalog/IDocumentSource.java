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
package net.refractions.udig.catalog;

import java.util.List;

public abstract class IDocumentSource {
    
    /**
     * This is used to return any documents associated with this feature type.
     * <p>
     * As an example this will return a SHAPEFILENAME.TXT file that is associated
     * (ie a sidecar file) with the provided shapefile. We may also wish to
     * list a README.txt file in the same directory (as it is the habbit of GIS
     * professionals to record fun information about the entire dataset.
     * </p>
     * @return
     */
    public abstract List<IDocument> findDocuments();


    /**
     * This is used to look up documents associated with an individual Feature.
     * <p>
     * As an example the feature type may indicate that the "reference" attribute is
     * actually a String to be handled as a "hotlink". In that case we would
     * return a LinkDocument representing the value of that attribute that would
     * be willing to open the "hotlink" in a web browser when open() is called.
     * 
     * @param fid Indicate the feature we are finding documents for
     * @return List of documents for the indicated feature
     */
    public abstract List<IDocument> findDocuments(String fid);
    
    /**
     * Add the document to the global document list
     * @param doc
     */
    public abstract void add( IDocument doc );
    
    /**
     * Add the document to the FID.
     * @param doc
     * @param fid
     */
    public abstract void add( IDocument doc, String fid );
    
    /**
     * Removes any occurrences of the document in the global document list
     * @param doc
     */
    public abstract void remove( IDocument doc );
    
    /**
     * Removes any occurrences of the document that are associated to the FID
     * @param doc
     * @param fid
     */
    public abstract void remove( IDocument doc, String fid );

    /**
     * Opens the document for viewing or editing
     * @param doc
     */
    public abstract void open( IDocument doc );
    
}
