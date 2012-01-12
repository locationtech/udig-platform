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
package net.refractions.udig.catalog.internal.shp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.DocumentFactory;
import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IDocument;
import net.refractions.udig.catalog.IDocumentSource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.core.internal.FeatureUtils;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.Query;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;

/**
 * Document Source for a shapefile. 
 * This class is responsible for interacting with the properties file 
 * in order to get and modify the Document lists
 * 
 * @author paul.pfeiffer
 * @version 1.3.0
 */
public class ShpDocumentSource extends IDocumentSource {
    
    /**
     * The field in the properties file that identifies the attribute in the shapefile 
     * that lists the documents for a specific feature / record
     */
    public static final String ATTRIBUTE_LIST = "shp_document_attributes"; //$NON-NLS-1$
    
    /**
     * The field in the properties file that lists the documents associated to all the features
     * in the shapefile
     */
    public static final String RESOURCE_DOCUMENTS = "shp_resource_documents"; //$NON-NLS-1$
    
    private URL url;

    /**
     * Creates a new ShpDocumentSource
     * @param url of the existing .shp file
     * @throws Exception 
     */
    public ShpDocumentSource( URL url ) throws IOException {
        this.url = url;
    }

    /**
     * Returns true if the properties file had hotlinks or files listed
     * @return
     */
    public boolean hasDocuments() {
        List<IDocument> documents = findDocuments();
        if (documents != null && !documents.isEmpty()) {
            return true;
        }
        if (!getAttributes().isEmpty()) {
            return true;
        }
        return false;
    }
    
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
    public List<IDocument> findDocuments() {
        List<IDocument> returnList = new ArrayList<IDocument>();
        
        String documents = getProperty(RESOURCE_DOCUMENTS);
        // if the properties file has a document list
        if (documents != null && documents.length() > 0) {
            returnList = stringToDocumentList(documents);
        }
        
        return returnList;
    }
    
    
    /*
     * Gets the property value from the property file associated with this shapefile.
     * Handles file IO.
     * Can return null.
     */
    private String getProperty(String property) {
        File propertiesFile = getPropertiesFile();
        if (propertiesFile.exists()) {
            // if the properties file exists load it
            Properties properties = new Properties();
            FileInputStream inStream = null;
            try {
                inStream = new FileInputStream(propertiesFile);
                properties.load(inStream);
                return properties.getProperty(property);
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (IOException e) {
                       // couldn't close
                    }
                }
            }
        }
        return null;
    }

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
    public List<IDocument> findDocuments(String fid) {
        
        ArrayList<IDocument> returnList = new ArrayList<IDocument>();
        
        IServiceFactory factory = CatalogPlugin.getDefault().getServiceFactory();
        List<IService> services = factory.createService( url );
        ShpServiceImpl service = (ShpServiceImpl) services.get(0);
        
        IProgressMonitor monitor = new NullProgressMonitor();
        try {
            ShapefileDataStore dataStore = service.getDS(monitor);
            
            // create a filter to get the selected feature
            FilterFactory2 ff = (FilterFactory2) CommonFactoryFinder.getFilterFactory(null);
            Filter fidFilter = ff.id(FeatureUtils.stringToId(ff, fid));
            SimpleFeatureCollection featureCollection = dataStore.getFeatureSource().getFeatures(fidFilter);
            
            if (featureCollection.features().hasNext()) {
                SimpleFeature feature = featureCollection.features().next();
                // get all the document attributes
                List<String> attributes = getAttributes();
                for (String attribute: attributes) {
                    // for every document attribute get the matching feature attribute
                    Object featureAttribute = feature.getAttribute(attribute);
                    String documentString = featureAttribute.toString();
                    if (!documentString.isEmpty()) {
                        // create a document and add it to the list
                        DocumentFactory documentFactory = new DocumentFactory();
                        IDocument document = documentFactory.create(documentString);
                        returnList.add(document);
                    }
                }
            }
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
                
        return returnList;
    }
    
    /*
     * creates a list of documents from the string in the properties file
     */
    private List<IDocument> stringToDocumentList(String list) {
        List<IDocument> returnList = new ArrayList<IDocument>();
        String[] documents = list.split("\\|"); //$NON-NLS-1$
        for (String document: documents) {
            DocumentFactory documentFactory = new DocumentFactory();
            returnList.add(documentFactory.create(document));
        }
        return returnList;
    }

    /*
     * creates a string to store in the properties file from a list of documents
     */
    private String documentListToString(List<IDocument> documents) {
        String returnString = ""; //$NON-NLS-1$
        
        int count = 0;      
        for (IDocument document : documents) {
            if (count == 0) {
                count++;
            }
            else {
                returnString += "|"; //$NON-NLS-1$
            }
            returnString += document.getReferences();
        }
        
        return returnString;
    }
    
    /*
     * creates a list of attributes from the string in the properties file
     */
    private List<String> stringToAttributeList(String list) {
        List<String> returnList = new ArrayList<String>();
        String[] attributes = list.split("\\|"); //$NON-NLS-1$
        for (String attribute: attributes) {
            returnList.add(attribute);
        }
        return returnList;
    }
        
    /*
     * creates a string to store in the properties file from a list of attributes
     */
    private String attributeListToString(List<String> attributes) {
        String returnString = ""; //$NON-NLS-1$
        
        int count = 0;      
        for (String attribute : attributes) {
            if (count == 0) {
                count++;
            }
            else {
                returnString += "|"; //$NON-NLS-1$
            }
            returnString += attribute;
        }
        
        return returnString;
    }
    
    /*
     * Access the properties file (if it exists).
     * <p>
     * Note if the file does not exist use id.toFile("properties")
     * 
     * @return properties file (null if it does not exist yet)
     */
    private File getPropertiesFile(){
        return getPropertiesFile(url); 
    }
    
    /**
     * Returns a file handle to a properties file with the same path as the shpURL
     * @param shpURL the URL of the shapefile
     * @return a file handle to a properties file which may or may not exist
     */
    public static File getPropertiesFile(URL shpURL){
        ID id = new ID( shpURL );
        File file = id.toFile("properties"); //$NON-NLS-1$
        if( file.exists() ){
            return file;
        }
        file = id.toFile("PROPERTIES"); //$NON-NLS-1$
        if( file.exists() ){
            return file;
        }
        return id.toFile("properties"); //$NON-NLS-1$
    }
    
    
    @Override
    public void add( IDocument doc ) {
        
        // get the existing documents
        List<IDocument> documentList = findDocuments();
        documentList.add(doc);
        String documentString = documentListToString(documentList);
        
        storeProperty(RESOURCE_DOCUMENTS, documentString);
    }

    @Override
    public void add( IDocument doc, String fid ) {
        // TODO Auto-generated method stub
        
    }

    /**
     * Gets the list of attributes that store documents in the features
     * @return
     */
    public List<String> getAttributes() {
        List<String> returnList = new ArrayList<String>();
        
        String attributes = getProperty(ATTRIBUTE_LIST);
        // if the properties file has a document list
        if (attributes != null && attributes.length() > 0) {
            returnList = stringToAttributeList(attributes);
        }
        
        return returnList;
    }
    
    /**
     * Adds an attribute that will be used to store documents
     * @param attribute
     */
    public void addAttribute( String attribute ) {
        List<String> attributeList = new ArrayList<String>();
        
        // get the existing attributes
        String list = getProperty(ATTRIBUTE_LIST);
        if (list != null) {
            attributeList = stringToAttributeList(list);
        }
        // adds the attribute
        if (!attributeList.contains(attribute)) {
            attributeList.add(attribute);
        }
        list = attributeListToString(attributeList);
        // stores the list
        storeProperty(ATTRIBUTE_LIST, list);
    }

    /**
     * Removes an attribute from the list that will be used to store documents
     * @param attribute
     */
    public void removeAttribute( String attribute ) {
        // get the existing attributes
        String list = getProperty(ATTRIBUTE_LIST);
        List<String> attributeList = stringToAttributeList(list);
        // removes the attribute
        while(attributeList.remove(attribute));
        list = attributeListToString(attributeList);
        // stores the list
        storeProperty(ATTRIBUTE_LIST, list);
        
        deletePropertiesIfEmpty();
    }
    
    /*
     * deletes the property file if there are no documents
     */
    private void deletePropertiesIfEmpty() {
        if (!hasDocuments()) {
            File propertiesFile = getPropertiesFile();
            if (propertiesFile.exists()) {
                propertiesFile.delete();
            }
            
        }
    }
    
    @Override
    public void remove( IDocument doc ) {

        // get the existing documents
        List<IDocument> documentList = findDocuments();
        while(documentList.remove(doc));
        
        String documentString = documentListToString(documentList);
        
        storeProperty(RESOURCE_DOCUMENTS, documentString);
        
        deletePropertiesIfEmpty();
    }
    
    /*
     * writes the document list to the property of the property file
     */
    private void storeProperty(String property, String documentString) {
        
        Properties properties = new Properties();
        properties.put(property, documentString);
        FileOutputStream outStream = null;
        try {
            File propertiesFile = getPropertiesFile();
            propertiesFile.createNewFile();
            outStream = new FileOutputStream(propertiesFile);
            properties.store(outStream, null);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                   // couldn't close
                }
            }
        }
    }

    @Override
    public void remove( IDocument doc, String fid ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void open( IDocument doc ) {
        doc.open();
    }

}
