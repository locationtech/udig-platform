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
 * A folder that contains documents relating to the selected feature.
 * If the selected feature is null then the folder will be empty
 * 
 * @author paul.pfeiffer
 * @version 1.3.1
 *
 */
public class FeatureFolder implements IFolder {

    private static final String FEATURE_DOCUMENT = "Feature Documents";
    private IDocumentSource documentSource;
    private String selectedFeature;

    public FeatureFolder( IDocumentSource documentSource ) {
        this.documentSource = documentSource;
    }

    @Override
    public IFolder getRow( int index ) {
        if (selectedFeature == null || selectedFeature.isEmpty()) {
            return null;
        }
        List<IDocument> documents = documentSource.findDocuments(selectedFeature);
        IDocument document = documents.get(index);
        return (AbstractDocument) document;
    }

    @Override
    public void setCount( int count ) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getCount() {
        if (selectedFeature == null || selectedFeature.isEmpty()) {
            return 0;
        }
        List<IDocument> documents = documentSource.findDocuments(selectedFeature);
        return documents.size();
    }

    @Override
    public String getName() {
        return FEATURE_DOCUMENT;
    }

    public void setSelectedFeature( String selectedFeature ) {
        this.selectedFeature = selectedFeature;
    }

}
