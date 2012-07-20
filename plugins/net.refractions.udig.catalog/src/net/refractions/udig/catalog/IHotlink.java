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
package net.refractions.udig.catalog;

import java.io.File;
import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.IDocument.Type;

import org.opengis.feature.simple.SimpleFeature;

/**
 * Hotlink support for IGeoResource.
 * <p>
 * Hotlinks are used to record a document reference feature attributes. The feature attributes that
 * are used for this purpose are made available through this interface, along with helper methods
 * allowing you to update these links correctly.
 * <p>
 * 
 * @author Jody Garnett
 */
public interface IHotlink extends IAbstractDocumentSource {

    /**
     * Used to record additional AttributeDescriptor information marking attributes suitable to
     * store hotlink information.
     */
    public class HotlinkDescriptor {
        
        private String attributeName;
        private Type type;
        
        public HotlinkDescriptor(String attributeName, IDocument.Type type) {
            this.attributeName = attributeName;
            this.type = type;
        }
        /**
         * HotlinkDescriptor represented as a string.
         * 
         * @param definition Definition of the form "name:type"
         */
        public HotlinkDescriptor(String definition) {
            int split = definition.lastIndexOf(":");
            this.attributeName = split == -1 ? definition : definition.substring(0,split);
            this.type = split == -1 ? Type.WEB : Type.valueOf(definition.substring(split));
        }
        
        public String getAttributeName() {
            return attributeName;
        }

        public Type getType() {
            return type;
        }
        @Override
        public String toString() {
            return attributeName + ":"+type;
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
