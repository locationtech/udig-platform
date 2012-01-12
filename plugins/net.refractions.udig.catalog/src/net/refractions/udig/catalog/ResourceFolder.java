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
package net.refractions.udig.catalog;

import java.util.List;

/**
 * A folder that contains documents associated to all the features
 * 
 * @author paul.pfeiffer
 * @version 1.3.1
 *
 */
public class ResourceFolder implements IFolder {

    private static final String RESOURCE_DOCUMENT = "Resource Documents";
    private IDocumentSource documentSource;

    public ResourceFolder( IDocumentSource documentSource ) {
        this.documentSource = documentSource;
    }

    @Override
    public IFolder getRow( int index ) {
        List<IDocument> documents = documentSource.findDocuments();
        IDocument document = documents.get(index);
        return (AbstractDocument) document;
    }

    @Override
    public void setCount( int count ) {
        // TODO Auto-generated method stub
    }

    @Override
    public int getCount() {
        List<IDocument> documents = documentSource.findDocuments();
        return documents.size();
    }

    @Override
    public String getName() {
        return RESOURCE_DOCUMENT;
    }

}
