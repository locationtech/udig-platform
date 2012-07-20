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
package net.refractions.udig.catalog.shp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.refractions.udig.catalog.DocumentFactory;
import net.refractions.udig.catalog.FileDocument;
import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IDocument;
import net.refractions.udig.catalog.LinkInfo;
import net.refractions.udig.catalog.URLDocument;

import org.opengis.feature.simple.SimpleFeature;

/**
 * Utility class to read and write a shapefile's properties file. Check examples below and the
 * string constants at the top of the class for expected string formatters and constants in the
 * property files.
 * <p>
 * <b>General property format:</b>
 * <p>
 * <code>
 * ex. <i>property_name</i>=<i>label</i>|<i>info</i>|<i>type</i>||<i>label</i>|<i>info</i>|<i>type</i>
 * </code>
 * <p>
 * <b>For shapefile level</b> - list of documents and types
 * <p>
 * <code>
 * ex. shp_links=Report|attachment.txt|FILE||Website|http://www.google.com|WEB 
 * </code>
 * <p>
 * <b>For feature hotlinks</b> - defines the attributes that have document links
 * <p>
 * <code>
 * ex. link_attributes=Contact Us|website|WEB||Map|attachment1|FILE
 * </code>
 * <p>
 * <b>For feature attachments</b> - list of documents and types, where the property name is relative
 * to the feature ID
 * <p>
 * <code>
 * ex. countries.123=Daily File|attachment.txt|FILE||Website|http://www.google.com|WEB
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
     * The delimiter used to separate link info pairs. 
     */
    private static final String LINK_PAIR_DELIMITER = "||"; //$NON-NLS-1$
    private static final String LINK_PAIR_REGEX = "\\|\\|"; //$NON-NLS-1$
    /**
     * The delimiter used to separate link info value and type
     */
    private static final String LINK_VALUE_DELIMITER = "|"; //$NON-NLS-1$
    private static final String LINK_VALUE_REGEX = "\\|"; //$NON-NLS-1$

    /**
     * The property name for the shapefile level documents
     */
    public static final String SHAPE_ATTACHMENTS = "shp_links"; //$NON-NLS-1$
    /**
     * The property name for the attributes linked to documents
     */
    public static final String LINK_ATTRIBUTES = "link_attributes"; //$NON-NLS-1$
    /**
     * The property name format for attribute attachments
     */
    public static final String ATTRIBUTE_ATTACHMENTS = "%s_links"; //$NON-NLS-1$

    private static final String PROP_FILE_EXT = "properties"; //$NON-NLS-1$
    
    private URL url;
    private Properties properties;
    private DocumentFactory docFactory;

    public ShpDocPropertyParser(URL url, DocumentFactory docFactory) {
        this.url = url;
        this.docFactory = docFactory;
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
     * Caches the property file into a properties util for easy access.
     * 
     * @return properties util
     */
    private Properties getProperties() {
        if (properties == null) {
            final File propertiesFile = getPropertiesFile();
            if (propertiesFile != null) {
                FileInputStream inStream = null;
                try {
                    inStream = new FileInputStream(propertiesFile);
                    properties = new Properties();
                    properties.load(inStream);
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
        }
        return properties;
    }
    
    /**
     * Writes the properties into a file.
     */
    private void writeProperties() {
        
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
     * Lookup the properties sidecar file for the indicate shapefile.
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
     * Lookup the properties sidecar file for the indicate shapefile.
     * 
     * @param url
     * @return properties file
     */
    public static File getPropertiesFile(URL url) {
        final ID id = new ID(url);
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
     * Gets the list of link values related to the property.
     * 
     * @param property
     * @return list of link values
     */
    private List<LinkInfo> getLinkInfos(String property) {
        
        final String propertyStr = getProperty(property);
        if (propertyStr != null && !propertyStr.isEmpty()) {
            final List<LinkInfo> linkInfos = new ArrayList<LinkInfo>();
            final String[] properties = propertyStr.split(LINK_PAIR_REGEX);
            for (String propertyPair: properties) {
                final String[] propertyPairComponents = propertyPair.split(LINK_VALUE_REGEX);
                final String rawlabel = propertyPairComponents[0];
                final String label = rawlabel.isEmpty() ? null : rawlabel;
                final String info = propertyPairComponents[1];
                final String rawType = propertyPairComponents[2];
                if (typeExists(rawType)) {
                    final IDocument.TYPE type = IDocument.TYPE.valueOf(rawType);
                    linkInfos.add(new LinkInfo(label, info, type));
                }
            }    
            return linkInfos;
        }
        
        return null;
    }
    
    /**
     * Checks if the type string exists in the {@link IDocument.TYPE} enum.
     * 
     * @param type
     * @return true if exists, otherwise false
     */
    private boolean typeExists(String type) {
        for (IDocument.TYPE c : IDocument.TYPE.values()) {
            if (c.name().equals(type)) {
                return true;
            }
        }
        return false;
    }

    
    /**
     * Writes the link info back to the property file.
     * 
     * @param property
     * @param docs
     */
    private void setLinkInfos(String property, List<IDocument> docs) {
        
        int count = 0;
        final StringBuilder sb = new StringBuilder();
        for (IDocument doc : docs) {
            count++;
            final String label = doc.getLabel();
            if (label != null) {
                sb.append(label);    
            }
            sb.append(LINK_VALUE_DELIMITER);
            final String linkValue = getLinkValue(doc); 
            if (linkValue != null) {
                sb.append(linkValue);    
            }
            sb.append(LINK_VALUE_DELIMITER);
            sb.append(doc.getType().name());
            if (count < docs.size()) {
                sb.append(LINK_PAIR_DELIMITER);
            }
        }
        getProperties().setProperty(property, sb.toString());
        writeProperties();
        
    }
    
    /**
     * Gets the link value from the document.
     * 
     * @param doc
     * @return link value
     */
    public String getLinkValue(IDocument doc) {
        if (!doc.isEmpty()) {
            switch (doc.getType()) {
            case FILE:
                return getFileLinkValue(((FileDocument) doc).getFile());
            case WEB:
                return getUrlLinkValue(((URLDocument) doc).getUrl());
            default:
                break;
            }    
        }
        return null;
    }
    
    /**
     * Gets the link value from the file.
     * 
     * @param file
     * @return link value
     */
    public String getFileLinkValue(File file) {
        try {
            final File base = new File(new URI(url.toString()));
            final File baseDir = base.getParentFile();
            return baseDir.toURI().relativize(file.toURI()).getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets the link value from the URL.
     * 
     * @param url
     * @return link value
     */
    public String getUrlLinkValue(URL url) {
        return url.toString();
    }
    
    /**
     * Gets the list of shape attachment info.
     * 
     * @return list of shape attachment info
     */
    private List<LinkInfo> getShapeAttachmentInfos() {
        return getLinkInfos(SHAPE_ATTACHMENTS);
    }
    
    /**
     * Gets the list of shape attachments. 
     * 
     * @return list of shape attachments
     */
    public List<IDocument> getShapeAttachments() {
        final List<LinkInfo> infos = getShapeAttachmentInfos();
        return docFactory.createList(url, infos);
    }
    
    /**
     * Writes the list of shape attachments back to the property file.
     * 
     * @param docs
     */
    public void setShapeAttachments(List<IDocument> docs) {
        setLinkInfos(SHAPE_ATTACHMENTS, docs);
    }
    
    /**
     * Gets the feature link info of the attribute.
     * 
     * @param attributeName
     * @return feature link info
     */
    public LinkInfo getFeatureLinkInfo(String attributeName) {
        final List<LinkInfo> infos = getFeatureLinkInfos();
        for (LinkInfo info : infos) {
            if (attributeName.equals(info.getInfo())) {
                return info;
            }
        }
        return null;
    }
    
    /**
     * Gets the list of feature link info.
     * 
     * @return list of feature link info
     */
    public List<LinkInfo> getFeatureLinkInfos() {
        return getLinkInfos(LINK_ATTRIBUTES);
    }
    
    /**
     * Gets the list of feature links.
     * 
     * @param feature
     * @return list of feature links
     */
    public List<IDocument> getFeatureLinks(SimpleFeature feature) {
        final List<LinkInfo> infos = getFeatureLinkInfos();
        return docFactory.createList(url, feature, infos);
    }
    
    /**
     * Gets the list of feature attachment info.
     * 
     * @param fid
     * @return list of feature attachment info
     */
    private List<LinkInfo> getFeatureAttachmentInfos(String fid) {
        final String property = String.format(ATTRIBUTE_ATTACHMENTS, fid);
        return getLinkInfos(property);
    }
    
    /**
     * Gets the list of feature attachments.
     * 
     * @param fid
     * @return list of feature attachments
     */
    public List<IDocument> getFeatureAttachments(String fid) {
        final List<LinkInfo> infos = getFeatureAttachmentInfos(fid);
        return docFactory.createList(url, infos);
    }
    
    /**
     * Writes the list of feature attachments back to the property file.
     * 
     * @param fid
     * @param docs
     */
    public void setFeatureAttachments(String fid, List<IDocument> docs) {
        final String property = String.format(ATTRIBUTE_ATTACHMENTS, fid);
        setLinkInfos(property, docs);
    }
    
}
