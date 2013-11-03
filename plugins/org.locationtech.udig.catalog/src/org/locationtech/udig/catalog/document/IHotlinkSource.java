/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.document;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.locationtech.udig.catalog.document.IDocument.ContentType;

import org.eclipse.core.runtime.IProgressMonitor;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Hotlink support for IGeoResource.
 * <p>
 * {@link Hotlink} documents are used to record a document references in the attributes of the provided feature.
 * <p>
 * The list of feature attributes that are used for this purpose are made available through this interface, along with helper methods
 * allowing you to update these links correctly.
 * <p>
 * 
 * @author Jody Garnett (LISAsoft)
 */
public interface IHotlinkSource extends IAbstractDocumentSource {


    /**
     * Checks if the source allows setting the hotlinks.
     * 
     * @return true if allowed, otherwise false
     */
    public boolean canSetHotlink();

    /**
     * Checks if the source allows clearing the hotlink.
     * 
     * @return true if allowed, otherwise false
     */
    public boolean canClearHotlink();
    
    /**
     * Gets the hotlink descriptors.
     * </p>
     * This method may require server-side processing and should not be ran in the UI thread.
     * </p>
     * 
     * @param feature
     * @param monitor
     * @return hotlink descriptors
     */
    public List<HotlinkDescriptor> getHotlinkDescriptors(SimpleFeature feature, IProgressMonitor monitor);

    /**
     * Gets the hotlink documents.
     * </p>
     * This method may require server-side processing and should not be ran in the UI thread.
     * </p>
     * 
     * @param feature
     * @param monitor
     * @return documents
     */
    public List<IDocument> getDocuments(SimpleFeature feature, IProgressMonitor monitor);
    
    /**
     * Gets the hotlink document.
     * 
     * @param feature - Feature under study, either retrieved directly from featureSource or a live
     *        EditFeature
     * @param attributeName - Attribute to decode document reference
     * @param monitor
     * @return document
     */
    public IDocument getDocument(SimpleFeature feature, String attributeName, IProgressMonitor monitor);
    
    /**
     * Used to encode the file and set it as the content of the hotlink attribute.
     * <p>
     * It is the callers responsibility to record this changed value, either by using featureStore
     * to write out the changed value, or by passing in a live EditFeature for modification.
     * 
     * @param feature Feature under study, either retrieved directly from featureSource or a live
     *        EditFeature
     * @param attributeName Attribute used to store the document reference
     * @param file File to encode as a reference
     * @param monitor
     * @return true if successful, otherwise false
     */
    public boolean setFile(SimpleFeature feature, String attributeName, File file, IProgressMonitor monitor);

    /**
     * Used to encode the URL and set it as the content of the hotlink attribute.
     * <p>
     * It is the callers responsibility to record this changed value, either by using featureStore
     * to write out the changed value, or by passing in a live EditFeature for modification.
     * 
     * @param feature Feature under study, either retrieved directly from featureSource or a live
     *        EditFeature
     * @param attributeName Attribute used to store the document reference
     * @param link URL to encode as a reference
     * @param monitor
     * @return true if successful, otherwise false
     */
    public boolean setLink(SimpleFeature feature, String attributeName, URL link, IProgressMonitor monitor);
    
    /**
     * Used to encode the action string and set it as the content of the hotlink attribute.
     * <p>
     * It is the callers responsibility to record this changed value, either by using featureStore
     * to write out the changed value, or by passing in a live EditFeature for modification.
     * 
     * @param feature
     * @param attributeName
     * @param action
     * @param monitor
     * @return true if successful, otherwise false
     */
    public boolean setAction(SimpleFeature feature, String attributeName, String action, IProgressMonitor monitor);

    /**
     * Used to clear a hotlink in the provided feature.
     * <p>
     * It is the callers responsibility to record this changed value, either by using featureStore
     * to write out the changed value, or by passing in a live EditFeature for modification.
     * 
     * @param feature Feature under study, either retrieved directly from featureSource or a live
     *        EditFeature
     * @param attributeName Attribute used to store the document reference
     * @param monitor
     * @return true if successful, otherwise false
     */
    public boolean clear(SimpleFeature feature, String attributeName, IProgressMonitor monitor);

    /**
     * Used to record additional AttributeDescriptor information marking attributes suitable to
     * store hotlink information.
     */
    public class HotlinkDescriptor {

        private final String label;
        private final String description;
        private final String attributeName;
        private final ContentType type;
        private final String config;

        public static final String DELIMITER = "|~|"; //$NON-NLS-1$
        public static final String DELIMITER_REGEX = "\\|~\\|"; //$NON-NLS-1$
        
        /**
         * Create an empty descriptor.
         */
        public HotlinkDescriptor() {
            label = null;
            description = null;
            attributeName = null;
            type = ContentType.FILE;
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

        public HotlinkDescriptor(String attributeName, IDocument.ContentType type) {
            this.label = null;
            this.description = null;
            this.attributeName = attributeName;
            this.type = type;
            this.config = null;
        }

        public HotlinkDescriptor(String label, String description, String attributeName,
                IDocument.ContentType type, String config) {
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
            
            final String[] defValues = definition.split(DELIMITER_REGEX);
            attributeName = getCleanValue(defValues[0]);
            type = ContentType.valueOf(defValues[1]);
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

        public ContentType getType() {
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

}
