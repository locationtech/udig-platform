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

import net.refractions.udig.catalog.IDocument.TYPE;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.identity.FeatureId;

/**
 * Hotlink support for IGeoResource.
 * <p>
 * Hotlinks are used to record a document reference feature attributes. The feature attributes
 * that are used for this purpose are made available through this interface, along with helper
 * methods allowing you to update these links correctly.
 * <p>
 * 
 * @author nchan
 */
public interface IHotlink {
    /**
     * Used to record additional AttributeDescriptor information marking
     * attributes suitable to store hotlink information.
     */
    public class HotlinkDescriptor {
        private TYPE type;
        private Name name;
        public HotlinkDescriptor( Name name, IDocument.TYPE type ){
            this.name = name;
            this.type = type;
        }
        public HotlinkDescriptor( SimpleFeatureType schema, String name, IDocument.TYPE type ){
            this.name = schema.getDescriptor(name).getName();
            this.type = type;
        }
        public Name getName() {
            return name;
        }
        public TYPE getType() {
            return type;
        }
    }
    /**
     * List of available attributes available to store hotlink information.
     * @return list of available hotlink information, may be empty if hotlinks not available.
     */
    List<HotlinkDescriptor> getHotlinkAttributeList();
    
    /**
     * Used to decode the indicated hotlink value as an IDocument for general use.
     * @param feature Feature under study, either retrieved directly from featureSource or a live EditFeature
     * @param attributeName Attribute to decode document reference
     */
    IDocument document( SimpleFeature feature, Name attributeName );
    
    /**
     * Used to encode the indicated file as an IDocument in the provided feature.
     * <p>
     * It is the callers responsibility to record this changed value, either by using featureStore
     * to write out the changed value, or by passing in a live EditFeature for modification.
     * 
     * @param feature Feature under study, either retrieved directly from featureSource or a live EditFeature
     * @param attributeName Attribute used to store the document reference
     * @param file File to encode as a reference
     * @return The created IDocument, or null if link unsuccessful
     */
    IDocument file( SimpleFeature feature, Name attributeName ,File file );
    /**
     * Used to encode the indicated file as an IDocument in the provided feature.
     * <p>
     * It is the callers responsibility to record this changed value, either by using featureStore
     * to write out the changed value, or by passing in a live EditFeature for modification.
     * 
     * @param feature Feature under study, either retrieved directly from featureSource or a live EditFeature
     * @param attributeName Attribute used to store the document reference
     * @param link URL to encode as a reference
     * @return The created IDocument, or null if link unsuccessful
     */
    IDocument link( SimpleFeature feature, Name attributeName, URL link );
    /**
     * Used to clear a hotlink in the provided feature.
     * <p>
     * It is the callers responsibility to record this changed value, either by using featureStore
     * to write out the changed value, or by passing in a live EditFeature for modification.
     * 
     * @param feature Feature under study, either retrieved directly from featureSource or a live EditFeature
     * @param attributeName Attribute used to store the document reference
     * @return The removed IDocument, or null if not available
     */
    IDocument clear( SimpleFeature feature, Name attributeName );
    
}
