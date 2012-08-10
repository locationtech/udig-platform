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
package net.refractions.udig.catalog.document;

import java.io.File;
import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.document.IDocument.Type;

import org.opengis.feature.simple.SimpleFeature;

/**
 * Hotlink support for IGeoResource.
 * <p>
 * {@link Hotlink} documents are used to record a document references in the attribtues of the provided feature.
 * <p>
 * The list of feature attributes that are used for this purpose are made available through this interface, along with helper methods
 * allowing you to update these links correctly.
 * <p>
 * 
 * @author Jody Garnett (LISAsoft)
 */
public interface IHotlinkSource extends IAbstractDocumentSource {

    /**
     * Used to record additional AttributeDescriptor information marking attributes suitable to
     * store hotlink information.
     */
    public class HotlinkDescriptor {

        private final String label;
        private final String description;
        private final String attributeName;
        private final Type type;
        private final String config;

        public static final String DELIMITER = ":"; //$NON-NLS-1$
        
        /**
         * Create an empty descriptor.
         */
        public HotlinkDescriptor() {
            label = null;
            description = null;
            attributeName = null;
            type = Type.FILE;
            config = null;
        }

        /**
         * Direct copy constructor.
         * <p>
         * Provided in case we add additional fields later.
         */
        public HotlinkDescriptor(HotlinkDescriptor descriptor) {
            label = descriptor.getLabel();
            description = descriptor.getDescription();
            attributeName = descriptor.getAttributeName();
            type = descriptor.getType();
            config = descriptor.getConfig();
        }

        public HotlinkDescriptor(String attributeName, IDocument.Type type) {
            this.label = null;
            this.description = null;
            this.attributeName = attributeName;
            this.type = type;
            this.config = null;
        }

        public HotlinkDescriptor(String label, String description, String attributeName,
                IDocument.Type type, String config) {
            this.label = label;
            this.description = description;
            this.attributeName = attributeName;
            this.type = type;
            this.config = config;
        }

        /**
         * HotlinkDescriptor represented as a string.
         * 
         * @param definition Definition of the form "name:type:config:label"
         */
        public HotlinkDescriptor(String definition) {
            
            final String[] defValues = definition.split(DELIMITER);
            attributeName = getCleanValue(defValues[0]);
            type = Type.valueOf(defValues[1]);
            if (defValues.length > 2) {
                config = getCleanValue(defValues[2]);
            } else {
                config = null;    
            }
            if (defValues.length > 3) { 
                label = getCleanValue(defValues[3]);    
            } else {
                label = null;
            }
            if (defValues.length > 4) { 
                description = getCleanValue(defValues[4]);    
            } else {
                description = null;
            }
            
        }

        private String getCleanValue(String text) {
            if (text != null) {
                final String cleanText = text.trim();
                if (cleanText.length() > 0) {
                    return cleanText;
                }
            }
            return null;
        }
        
        public boolean isEmpty() {
            return attributeName == null || attributeName.isEmpty();
        }

        public String getAttributeName() {
            return attributeName;
        }

        public Type getType() {
            return type;
        }

        public String getConfig() {
            return config;
        }
        
        public String getLabel() {
            return label;
        }
        
        public String getDescription() {
            return description;
        } 

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            if (attributeName != null) {
                sb.append(attributeName);
            }
            sb.append(DELIMITER);
            if (type != null) {
                sb.append(type);
            }
            sb.append(DELIMITER);
            if (config != null) {
                sb.append(config);
            }
            sb.append(DELIMITER);
            if (label != null) {
                sb.append(label);
            }
            sb.append(DELIMITER);
            if (description != null) {
                sb.append(description);
            }
            return sb.toString();
        }
    }

    /**
     * List of available attributes available to store hotlink information.
     * 
     * @return list of available hotlink information, may be empty if hotlinks not available.
     */
    public List<HotlinkDescriptor> getHotlinkDescriptors();

    /**
     * Gets the list of documents in the feature.
     * <p>
     * Note the returned list may be a mix of {@link IDocument} types including {@link IHotlink}.
     * 
     * @param fid
     * @return list of documents
     */
    public List<IDocument> getDocuments(SimpleFeature feature);
    
    /**
     * Used to decode the indicated hotlink value as an IDocument for general use.
     * 
     * @param feature Feature under study, either retrieved directly from featureSource or a live
     *        EditFeature
     * @param attributeName Attribute to decode document reference
     */
    public IDocument getDocument(SimpleFeature feature, String attributeName);

    /**
     * Used to encode the indicated file as an IDocument in the provided feature.
     * <p>
     * It is the callers responsibility to record this changed value, either by using featureStore
     * to write out the changed value, or by passing in a live EditFeature for modification.
     * 
     * @param feature Feature under study, either retrieved directly from featureSource or a live
     *        EditFeature
     * @param attributeName Attribute used to store the document reference
     * @param file File to encode as a reference
     * @return The created IDocument, or null if link unsuccessful
     */
    public IDocument setFile(SimpleFeature feature, String attributeName, File file);

    /**
     * Used to encode the indicated file as an IDocument in the provided feature.
     * <p>
     * It is the callers responsibility to record this changed value, either by using featureStore
     * to write out the changed value, or by passing in a live EditFeature for modification.
     * 
     * @param feature Feature under study, either retrieved directly from featureSource or a live
     *        EditFeature
     * @param attributeName Attribute used to store the document reference
     * @param link URL to encode as a reference
     * @return The created IDocument, or null if link unsuccessful
     */
    public IDocument setLink(SimpleFeature feature, String attributeName, URL link);

    /**
     * Used to clear a hotlink in the provided feature.
     * <p>
     * It is the callers responsibility to record this changed value, either by using featureStore
     * to write out the changed value, or by passing in a live EditFeature for modification.
     * 
     * @param feature Feature under study, either retrieved directly from featureSource or a live
     *        EditFeature
     * @param attributeName Attribute used to store the document reference
     * @return The removed IDocument, or null if not available
     */
    public IDocument clear(SimpleFeature feature, String attributeName);

}
