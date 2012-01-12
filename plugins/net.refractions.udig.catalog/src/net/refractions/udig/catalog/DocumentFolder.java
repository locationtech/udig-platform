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


/**
 * A folder that contains documents. Used as a data structure for the document view. 
 * A folder may contain other folders. 
 * 
 * @author paul.pfeiffer
 * @version 1.3.1
 *
 */
public class DocumentFolder implements IFolder {

    private GlobalFolder globalDocuments = null;
    private FeatureFolder featureDocuments = null;
    private final String name = "Top Level";
//    private IDocumentSource source;
    private String selectedFeature = null;
    
    public DocumentFolder(IDocumentSource source) {
//        this.source = source;
        globalDocuments = new GlobalFolder(source);
        featureDocuments = new FeatureFolder(source);
    }
    
//    public DocumentFolder(IDocumentSource source, String name) {
//        this(source);
//        this.name = name;
//    }
    
    @Override
    public IFolder getRow( int index ) {
        switch (index) {
        case 0:
            return globalDocuments;
        case 1:
            if (selectedFeature == null || selectedFeature.isEmpty()) {
                return null;
            }
            else {
                return featureDocuments;
            }
        default: 
            return null;
        }
    }
        

    @Override
    public void setCount( int count ) {
        // do nothing - the contents are set manually
    }

    @Override
    public int getCount() {
        if (selectedFeature == null || selectedFeature.isEmpty()) {
            return 1;
        }
        else {
            return 2;
        }
    }
    
    @Override
    public String getName() {
        return name;
    }

    /**
     * Stores the selected feature id and updates the feature document folder
     * @param selectedFeature
     */
    public void setSelectedFeature( String selectedFeature ) {
        this.selectedFeature = selectedFeature;
        if (featureDocuments == null) {
            featureDocuments.setSelectedFeature(selectedFeature);
        }
    }
    

}
