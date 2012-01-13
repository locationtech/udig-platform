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
import java.io.FileNotFoundException;
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
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.factory.CommonFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

/**
 * Document Source for a shapefile. 
 * This class is responsible for interacting with the properties file 
 * in order to get and modify the Document lists.
 * 
 * Note that currently there is a restriction on the number of attributes that can be added
 * for feature documents: one attribute per document type (which at the time of writing this
 * was FileDocument and URLDocument)
 * 
 * @author paul.pfeiffer
 * @version 1.3.0
 */
public class ShpDocumentSource extends IDocumentSource {
    
    /**
     * The field in the properties file that lists the documents associated to all the features
     * in the shapefile
     */
    public static final String RESOURCE_DOCUMENTS = "shp_resource_documents"; //$NON-NLS-1$
    
    /**
     * The field in the properties file that identifies the attribute in the shapefile 
     * that lists the documents for a specific feature / record
     */
    public static final String ATTRIBUTE_LIST = "shp_document_attributes"; //$NON-NLS-1$
    
    /**
     * The field in the properties file that identifies the attribute types in the shapefile 
     * that lists the documents for a specific feature / record
     */
    public static final String ATTRIBUTE_TYPE_LIST = "shp_document_attribute_types"; //$NON-NLS-1$
    
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
    
    
    /**
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
    
    /**
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

    /**
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
    
    /**
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
        
    /**
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
    
    /**
     * creates a list of attribute types from the string in the properties file
     */
    private List<Class<? extends IDocument>> stringToTypeList(String list) {
        List<Class<? extends IDocument>> returnList = new ArrayList<Class<? extends IDocument>>();
        String[] attributeTypes = list.split("\\|"); //$NON-NLS-1$
        for (String attributeType: attributeTypes) {
            Class< ? extends IDocument> clazz;
            try {
                clazz = Class.forName(attributeType).asSubclass(IDocument.class);
                returnList.add(clazz);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
            }
        }
        return returnList;
    }
    
    /**
     * creates a string to store in the properties file from a list of attribute types
     */
    private String typeListToString(List<Class<? extends IDocument>> attributeTypes) {
        String returnString = ""; //$NON-NLS-1$
        
        int count = 0;      
        for (Class<? extends IDocument> attributeType : attributeTypes) {
            if (count == 0) {
                count++;
            }
            else {
                returnString += "|"; //$NON-NLS-1$
            }
            returnString += attributeType.getName();
        }
        
        return returnString;
    }
    
    /**
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
        // figure out what attribute the document needs to be stored against
        
        // have a look at addAttribute() and getAttributeTypes() to see 
        // how attribute types are stored 
        
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
     * Gets the list of attribute types. The order will match the attributes
     * @return
     */
    public List<Class< ? extends IDocument>> getAttributeTypes() {
        List<Class< ? extends IDocument>> returnList = new ArrayList<Class< ? extends IDocument>>();
        
        String attributeTypes = getProperty(ATTRIBUTE_TYPE_LIST);
        // if the properties file has a document list
        if (attributeTypes != null && attributeTypes.length() > 0) {
            returnList = stringToTypeList(attributeTypes);
        }
        return returnList;
    }
    
    
    /**
     * Adds an attribute that will be used to store documents.
     * 
     * Note that currently there is a restriction on the number of attributes that can be added
     * for feature documents: one attribute per document type (which at the time of writing this
     * was FileDocument and URLDocument)
     * 
     * @param attribute The attribute of the shapefile to be used as a document
     * @param docType The type of document to be stored
     * @throws IllegalStateException If an attribute already exists for this documnent type
     */
    public void addAttribute( String attribute, Class<? extends IDocument> docType ) 
            throws IllegalStateException {
        
        // get the existing attribute types and checks if the type already exists
        List<Class< ? extends IDocument>> attributeTypes = getAttributeTypes();
        if (attributeTypes.contains(docType)) {
            throw new IllegalStateException("An attribute already exists for this documnent type");
        }
        attributeTypes.add(docType);
        String types = typeListToString(attributeTypes);
        // store the type list
        storeProperty(ATTRIBUTE_TYPE_LIST, types);
        
        // get the existing attributes
        List<String> attributeList = getAttributes();
        // adds the attribute
        if (!attributeList.contains(attribute)) {
            attributeList.add(attribute);
        }
        String list = attributeListToString(attributeList);
        // stores the list
        storeProperty(ATTRIBUTE_LIST, list);
    }

    /**
     * Removes an attribute and it's matching type from the list that will be used to store documents
     * @param attribute
     * @throws IllegalStateException if a matching type can not be removed
     */
    public void removeAttribute( String attribute ) throws IllegalStateException {
        // get the existing attributes
        List<String> attributeList = getAttributes();
        int index = attributeList.indexOf(attribute);
        // removes the attribute
        if (index != -1) {
            attributeList.remove(attribute);
            
            //remove the matching attribute type
            List<Class< ? extends IDocument>> attributeTypes = getAttributeTypes();
            if (index >= attributeTypes.size()) {
                throw new IllegalStateException("Could not delete the matching documnent type for this attribute");
            }
            attributeTypes.remove(index);
            String types = typeListToString(attributeTypes);
            // store the type list
            storeProperty(ATTRIBUTE_TYPE_LIST, types);
            
        }
        String list = attributeListToString(attributeList);
        // stores the list
        storeProperty(ATTRIBUTE_LIST, list);
        
        deletePropertiesIfEmpty();
    }
    
    /**
     * deletes the property file if there are no documents
     */
    private void deletePropertiesIfEmpty() {
        if (!hasDocuments()) {
            clean();
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
    
    /**
     * writes the value to the property of the property file
     */
    private void storeProperty(String property, String value) {
        
        Properties properties = new Properties();
        FileInputStream inputStream = null;
        FileOutputStream outStream = null;
        try {
            File propertiesFile = getPropertiesFile();
            if(!propertiesFile.createNewFile()) {
                // Load the existing properties
                inputStream = new FileInputStream(propertiesFile);
                properties.load(inputStream);
            }
            // set the property value
            properties.put(property, value);
            
            // save the property file
            outStream = new FileOutputStream(propertiesFile);
            properties.store(outStream, null);
            
        } 
        catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                   // couldn't close
                }
            }
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

    /**
     * deletes the properties file - used for testing
     */
    public void clean() {
        File propertiesFile = getPropertiesFile();
        if (propertiesFile.exists()) {
            propertiesFile.delete();
        }
    }

}
