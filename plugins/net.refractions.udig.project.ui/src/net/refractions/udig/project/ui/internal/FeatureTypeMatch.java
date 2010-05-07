/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.project.ui.internal;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;

import sun.tools.tree.ThisExpression;

/**
 * This class is used to determine whether a FeatureEditor can be used to edit a feature of a given
 * SimpleFeatureType.
 * <p>
 * This class uses the SimpleFeatureType element of the FeatureEditor Extension point to determine
 * whether the editor can be used. See the FeatureEditor extension point declaration for more
 * information.
 * </p>
 * 
 * @author jones
 * @since 1.0.0
 */
public class FeatureTypeMatch {

    /**
     * Matches a features attributeType to the featureType declared in the extension.
     * 
     * @author jones
     * @since 1.0.0
     */
    protected static class AttributeMatcher {
        String name;
        Class< ? > type;

        /**
         * New instance.
         */
        public AttributeMatcher( IConfigurationElement attr ) {
            name = attr.getAttribute("name"); //$NON-NLS-1$
            try {
                type = Class.forName(attr.getAttribute("type")); //$NON-NLS-1$
            } catch (Exception e) {
                ProjectUIPlugin
                        .log(
                                "Extension declaration incorrect:" + attr.getDeclaringExtension().getUniqueIdentifier(), e); //$NON-NLS-1$
            }
        }

        /**
         * returns true if the attr.getType == type and (name==null or name.equals(attr.getName))
         * 
         * @param attr
         * @return
         */
        public AttributeDescriptor match( SimpleFeatureType featureType,
                List<AttributeDescriptor> used ) {
            if (name != null) {
                AttributeDescriptor attr = featureType.getDescriptor(name);
                if (type == null || attr == null)
                    return null;
                if (type != attr.getType().getBinding())
                    return null;
                return attr;
            }
            for( int i = 0; i < featureType.getAttributeCount(); i++ ) {
                if (!used.contains(featureType.getDescriptor(i))) {
                    if (type == featureType.getDescriptor(i).getType().getBinding())
                        return featureType.getDescriptor(i);
                }
            }
            return null;
        }
        @Override
        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append("match(");
            if (this.name != null) {
                buf.append(this.name);
            }
            if (this.type != null) {
                buf.append(" ");
                buf.append(type.getSimpleName());
            }
            buf.append(")");
            return buf.toString();
        }
    }
    /** A matcher that matches all FeatureTypes */
    public static final FeatureTypeMatch ALL = new FeatureTypeMatch(){
        public int matches( Object firstElement ) {
            if (firstElement == null)
                return -1;
            return Integer.MAX_VALUE - 1;
        }
    };
    private URI namespace;
    private String typeName;
    private AttributeMatcher[] attributes;

    FeatureTypeMatch() {
        // do nothing
    }

    /**
     * Create a FeatureTypeMatcher Object
     * 
     * @param element
     */
    public FeatureTypeMatch( IConfigurationElement element ) {
        if (element.getChildren("typeName").length == 1) { //$NON-NLS-1$
            IConfigurationElement typeName = element.getChildren("typeName")[0]; //$NON-NLS-1$
            this.typeName = typeName.getAttribute("name"); //$NON-NLS-1$
            try {
                this.namespace = new URI(typeName.getAttribute("namespace")); //$NON-NLS-1$
            } catch (Exception e) {
                ProjectUIPlugin.log(Messages.FeatureTypeMatch_BadURI, e);
            }
        } else {
            IConfigurationElement[] attributes = element.getChildren("attribute"); //$NON-NLS-1$
            this.attributes = new AttributeMatcher[attributes.length];

            for( int i = 0; i < attributes.length; i++ ) {
                IConfigurationElement attr = attributes[i];
                this.attributes[i] = new AttributeMatcher(attr);
            }
        }

    }
    /**
     * @param element
     * @return true if matches( element ) is greater the -1
     */
    public boolean isMatch( Object element ) {
        int matches = matches(element);
        return matches > -1;
    }

    public static int PERFECT = 0;
    public static int NO_MATCH = -1;
    /**
     * Returns >-1 if the editor has specified a SimpleFeatureType declaration that matches the
     * SimpleFeature passed in as a parameter. Each inaccuracy increases the count by 1. a 0 is a
     * perfect match, using the featureType name and namespace. 1 would be all the attributeTypes
     * have a name and type and there are no extra attributes in the feature's feature type.
     * <p>
     * The matching is done as follows:
     * <ul>
     * <li>If the object is not a feature false is returned.</li>
     * <li>If namespace is not null then the matching is done by matching the namespace and name of
     * the featureType</li>
     * <li>Otherwise the attributes used to match. Two passes are made through the attributes
     * declared in then extension:
     * <ul>
     * <li>First all attributes that are declared and have names associated are processed. There
     * must be an exact match between the declared attribute and one of the attributeTypes in the
     * feature</li>
     * <li>Second all the attributes without declared names are processed. only the type is used to
     * find a match and each attributeType may be matched only once</li>
     * </ul>
     * </ul>
     * 
     * @param element
     * @return 0 for a perfect match,
     */
    public int matches( Object element ) {
        SimpleFeatureType schema = null;
        if (element instanceof SimpleFeatureType) {
            schema = (SimpleFeatureType) element;
        } else if (element instanceof SimpleFeatureType) {
            schema = (SimpleFeatureType) element;
        }

        if (schema != null) {
            Name featureName = schema.getName();
            if (namespace != null) {

                if (namespace.equals(featureName.getNamespaceURI())
                        && typeName.equals(featureName.getLocalPart())) {
                    return PERFECT;
                }
                return NO_MATCH;
            }
            if (attributes.length == 0) {
                return NO_MATCH;
            }
            int accuracy = 0;
            accuracy++;
            List<AttributeDescriptor> matched = new ArrayList<AttributeDescriptor>();
            // 1st pass check all named attributes are accounted for
            for( AttributeMatcher current : attributes ) {
                if (current.name == null) {
                    continue; // skip
                }
                AttributeDescriptor currentMatch = current.match(schema, matched);
                if (currentMatch == null) {
                    return NO_MATCH;
                }
                matched.add(currentMatch);
            }
            // section pass check unnamed attributes ... match default geometry type?
            for( AttributeMatcher current : attributes ) {
                if (current.name != null) {
                    continue;
                }
                accuracy++;

                AttributeDescriptor currentMatch = current.match(schema, matched);
                if (currentMatch == null) {
                    return NO_MATCH;
                }
                matched.add(currentMatch);
            }
            accuracy += schema.getAttributeCount() - matched.size();
            return accuracy;
        }
        return NO_MATCH;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("FeatureTypeMatch ");
        if (this.namespace != null) {
            buf.append(this.namespace);
        }
        buf.append(this.typeName);
        if (this.attributes != null) {
            for( int i = 0; i < this.attributes.length; i++ ) {
                buf.append(this.attributes[i]);
                if (i < this.attributes.length - 1) {
                    buf.append(",");
                }
            }
        }
        return buf.toString();
    }
}
