/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.document.source;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.document.IDocument.ContentType;
import org.locationtech.udig.catalog.document.IDocumentSource.DocumentInfo;
import org.locationtech.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;
import org.locationtech.udig.document.ui.DocUtils;

import org.opengis.feature.simple.SimpleFeature;

/**
 * Utility parser class to read and write a shapefile's properties file. Check examples below and the
 * string constants at the top of the class for expected string formatters and constants in the
 * property files.
 * <p>
 * <b>General property format:</b>
 * <p>
 * <code>
 * ex. <i>property_name</i>=<i>info</i>|:|<i>info</i>|:|<i>info</i>
 * </code>
 * <p>
 * Note that the property file expected to be in the same directory as the shapefile and must have
 * the same name as the shapefile.
 * <p>
 * <code>
 * ex. countries.shp --> countries.properties
 * </code>
 * 
 * @author Naz Chan
 */
public class ShpDocPropertyParser {
    
    /**
     * The delimiter used to separate link info strings. 
     */
    private static final String DELIMITER = "|:|"; //$NON-NLS-1$
    private static final String DELIMITER_REGEX = "\\|:\\|"; //$NON-NLS-1$

    /**
     * The property name to flag if document are enabled at the shapefile level.
     * <p>
     * Expected values: true or false
     */
    public static final String SHAPE_DOCUMENTS_FLAG = "shp_documents_enabled"; //$NON-NLS-1$
    /**
     * The property name to flag if document are enabled at the feature level.
     * <p>
     * Expected values: true or false
     */
    public static final String FEATURE_DOCUMENTS_FLAG = "feature_documents_enabled"; //$NON-NLS-1$
    /**
     * The property name to flag if hotlinks are enabled at the feature level.
     * <p>
     * Expected values: true or false
     */
    public static final String FEATURE_HOTLINKS_FLAG = "feature_hotlinks_enabled"; //$NON-NLS-1$
    
    /**
     * The property name for the shapefile level documents
     */
    public static final String SHAPE_ATTACHMENTS = "shp_documents"; //$NON-NLS-1$
    /**
     * The property name for the attributes linked to documents
     */
    public static final String HOTLINK_ATTRIBUTES = "hotlink_attributes"; //$NON-NLS-1$
    /**
     * The property name format for attribute attachments
     */
    public static final String ATTRIBUTE_ATTACHMENTS = "%s_documents"; //$NON-NLS-1$
    
    /**
     * The name of the folder that would contain all the shapefile's attachments.
     * <p>
     * Format should be in "[shapefile_name].documents".
     */
    public static final String SHAPE_DOCS_FOLDER = "%s.documents"; //$NON-NLS-1$
    
    /**
     * Property file extension
     */
    private static final String PROP_FILE_EXT = "properties"; //$NON-NLS-1$
    
    private URL url;
    private Properties properties;
    private long propsFileLastUpdate;

    public ShpDocPropertyParser(URL url) {
        this.url = url;
    }

    /**
     * Checks if the shapefile has a properties file. This determines if have display link related
     * metadata of the shapefile.
     * 
     * @return true if shapefile has a properties file, otherwise return false
     */
    public boolean hasProperties() {
        return getProperties() != null;
    }
    
    /**
     * Caches the property file into a properties utility for easy access.
     * 
     * @return properties util
     */
    private Properties getProperties() {
        final File propertiesFile = getPropertiesFile();
        if (propertiesFile != null) {
            if (properties == null) {
                readProperties(propertiesFile);
            } else {
                if (isPropertiesUpdated(propertiesFile)) {
                    readProperties(propertiesFile);
                }
            }
        }
        return properties;
    }
    
    /**
     * Loads the file into the properties utility.
     * 
     * @param propertiesFile
     */
    private void readProperties(File propertiesFile) {
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(propertiesFile);
            properties = new Properties();
            properties.load(inStream);
            propsFileLastUpdate = propertiesFile.lastModified(); 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Checks if the properties file has been updated.
     * 
     * @param currentPropertiesFile
     * @return true if updated, otherwise false
     */
    private boolean isPropertiesUpdated(File currentPropertiesFile) {
        if (propsFileLastUpdate != currentPropertiesFile.lastModified()) {
            return true;
        }
        return false;
    }
    
    /**
     * Writes the properties into a file.
     */
    public void writeProperties() {
        
        final File propertiesFile = getPropertiesFile();
        if (propertiesFile != null) {
            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(propertiesFile);
                properties.store(outStream, ""); //$NON-NLS-1$
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outStream != null) {
                    try {
                        outStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
    }
    
    /**
     * Gets the properties file that contains the shapefile's links definition.
     * 
     * @param url
     * @return properties file
     */
    private File getPropertiesFile() {
        return getPropertiesFile(url);
    }

    /**
     * Create the properties file.
     * 
     * @return properties file
     */
    public File createPropertiesFile() {
        File file = getPropertiesFile();
        if (file == null) {
            final ID fileId = new ID(url);
            file = fileId.toFile(PROP_FILE_EXT);
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
    
    /**
     * Lookup the properties file for the indicate shapefile.
     * 
     * @param url
     * @return properties file
     */
    public static File getPropertiesFile(ID id) {
        File file = id.toFile(PROP_FILE_EXT.toLowerCase());
        if (file.exists()) {
            return file;
        }
        file = id.toFile(PROP_FILE_EXT.toUpperCase());
        if (file.exists()) {
            return file;
        }
        return null;
    }
    
    /**
     * Lookup the properties file for the indicate shapefile.
     * 
     * @param url
     * @return properties file
     */
    public static File getPropertiesFile(URL url) {
        return getPropertiesFile(new ID(url));
    }
    
    /**
     * Gets a property value
     * 
     * @param property
     * @return property value
     */
    private String getProperty(String property) {
        final Properties properties = getProperties();
        if (properties != null) {
            return properties.getProperty(property);
        }
        return null;
    }
    
    /**
     * Sets a property value.
     * 
     * @param property
     * @param value
     */
    private void setProperty(String property, String value) {
        getProperties().setProperty(property, value);
    }
    
    /**
     * Gets the list of document info from the formatted property string.
     * 
     * @param spec
     * @return list of document info
     */
    private List<DocumentInfo> toDocInfoList(String spec) {
        if (spec != null && !spec.isEmpty()) {
            final List<DocumentInfo> docInfos = new ArrayList<DocumentInfo>();
            final String[] docInfoArray = spec.split(DELIMITER_REGEX);
            for (String docInfo : docInfoArray) {
                final DocumentInfo info = new DocumentInfo(docInfo);
                if (ContentType.FILE == info.getContentType()) {
                    info.setInfo(ShpDocUtils.getAbsolutePath(url, info.getInfo()));    
                }
                docInfos.add(info);
            }
            return docInfos;
        }
        return null;
    }
    
    /**
     * Gets the formatted property string from the list of document infos.
     * 
     * @param docInfos
     * @return formatted property string
     */
    private String toDocInfoString(List<DocumentInfo> docInfos) {
        int count = 0;
        final StringBuilder sb = new StringBuilder();
        for (DocumentInfo info : docInfos) {
            count++;
            if (ContentType.FILE == info.getContentType()) {
                final DocumentInfo writeInfo = new DocumentInfo(info.toString());
                writeInfo.setInfo(ShpDocUtils.getRelativePath(url, writeInfo.getInfo()));
                sb.append(writeInfo.toString());
            } else {
                sb.append(info.toString());
            }
            if (count < docInfos.size()) {
                sb.append(DELIMITER);
            }
        }
        return sb.toString();
    }
    
    /**
     * Gets the list of hotlink descriptors from the formatted property string.
     * 
     * @param spec
     * @return list of hotlink descriptors
     */
    private List<HotlinkDescriptor> toHotlinkDescriptorList(String spec) {
        if (spec != null && !spec.isEmpty()) {
            final List<HotlinkDescriptor> descriptors = new ArrayList<HotlinkDescriptor>();
            final String[] desriptorArray = spec.split(DELIMITER_REGEX);
            for (String descriptor : desriptorArray) {
                descriptors.add(new HotlinkDescriptor(descriptor));
            }
            return descriptors;
        }
        return null;
    }
    
    /**
     * Gets the formatted property string from the list of hotlink descriptors.
     * 
     * @param descriptors
     * @return formatted property string
     */
    private String toHotlinkDescriptorString(List<HotlinkDescriptor> descriptors) {
        int count = 0;
        final StringBuilder sb = new StringBuilder();
        for (HotlinkDescriptor descriptor : descriptors) {
            count++;
            sb.append(descriptor.toString());
            if (count < descriptors.size()) {
                sb.append(DELIMITER);
            }
        }
        return sb.toString();
    }
    
    /**
     * Gets the list of shape attachment info.
     * 
     * @return list of shape attachment info
     */
    public List<DocumentInfo> getShapeDocumentInfos() {
        return toDocInfoList(getProperty(SHAPE_ATTACHMENTS));
    }
    
    /**
     * Writes the list of shape attachments back to the property file.
     * 
     * @param docs
     */
    public void setShapeDocmentInfos(List<DocumentInfo> docInfos) {
        setProperty(SHAPE_ATTACHMENTS, toDocInfoString(docInfos));
    }
    
    /**
     * Gets the list of feature link info.
     * 
     * @return list of feature link info
     */
    public List<HotlinkDescriptor> getHotlinkDescriptors() {
        return toHotlinkDescriptorList(getProperty(HOTLINK_ATTRIBUTES));
    }
    
    /**
     * Sets the list of feature links.
     * 
     * @param hotlinks
     */
    public void setHotlinkDescriptors(List<HotlinkDescriptor> descriptors) {
        setProperty(HOTLINK_ATTRIBUTES, toHotlinkDescriptorString(descriptors));
    }
    
    /**
     * Gets the list of feature attachment info.
     * 
     * @param fid
     * @return list of feature attachment info
     */
    public List<DocumentInfo> getFeatureDocumentInfos(SimpleFeature feature) {
        final String property = String.format(ATTRIBUTE_ATTACHMENTS, feature.getIdentifier().getID());
        return toDocInfoList(getProperty(property));
    }
    
    /**
     * Writes the list of feature attachments back to the property file.
     * 
     * @param fid
     * @param docs
     */
    public void setFeatureDocumentInfos(SimpleFeature feature, List<DocumentInfo> docInfos) {
        final String property = String.format(ATTRIBUTE_ATTACHMENTS, feature.getIdentifier().getID());
        setProperty(property, toDocInfoString(docInfos));
    }
    
    /**
     * Gets the directory of the shapefile's attachments.
     * 
     * @return directory of the shapefile's attachments
     */
    public File getShapefileAttachDir() {
        try {
            final File shapeFile = new File(url.toURI());
            final String folderName = String.format(SHAPE_DOCS_FOLDER, DocUtils.getName(shapeFile));
            final File attachDir = new File(shapeFile.getParent(), folderName); 
            return attachDir;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets the directory of feature's attachments.
     * <p>
     * Attachments directory:
     * <ul>
     * <li>[shapefile_dir]/[shapefile_name].documents/featureId</li>
     * </ul>
     * 
     * @param fid
     * @return directory of feature's attachments
     */
    public File getFeatureAttachDir(String fid) {
        final File attachDir = new File(getShapefileAttachDir(), fid);; 
        return attachDir;
    }
    
    public void setShapefileFlag(boolean isEnabled) {
        setFlag(SHAPE_DOCUMENTS_FLAG, isEnabled);
    }
    
    public boolean getShapefileFlag() {
        return getFlag(SHAPE_DOCUMENTS_FLAG);
    }
    
    public void setFeatureDocsFlag(boolean isEnabled) {
        setFlag(FEATURE_DOCUMENTS_FLAG, isEnabled);
    }
    
    public boolean getFeatureDocsFlag() {
        return getFlag(FEATURE_DOCUMENTS_FLAG);
    }
    
    public void setFeatureHotlinksFlag(boolean isEnabled) {
        setFlag(FEATURE_HOTLINKS_FLAG, isEnabled);
    }
    
    public boolean getFeatureHotlinksFlag() {
        return getFlag(FEATURE_HOTLINKS_FLAG);
    }
    
    private void setFlag(String property, boolean isEnabled) {
        setProperty(property, Boolean.toString(isEnabled));
    }
    
    private boolean getFlag(String property) {
        final String flagValue = getProperty(property);
        if (flagValue != null) {
            return Boolean.parseBoolean(flagValue);
        }
        return false;
    }
    
}
