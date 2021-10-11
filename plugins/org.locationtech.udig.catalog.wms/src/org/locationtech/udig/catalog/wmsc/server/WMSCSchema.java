/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.wmsc.server;

import java.awt.RenderingHints.Key;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

import org.geotools.ows.wms.xml.WMSSchema;
import org.geotools.xml.schema.Attribute;
import org.geotools.xml.schema.AttributeGroup;
import org.geotools.xml.schema.ComplexType;
import org.geotools.xml.schema.Element;
import org.geotools.xml.schema.ElementValue;
import org.geotools.xml.schema.Group;
import org.geotools.xml.schema.Schema;
import org.geotools.xml.schema.SimpleType;
import org.geotools.xml.schema.Type;
import org.geotools.xml.schema.impl.AttributeGT;
import org.geotools.xml.xLink.XLinkSchema;
import org.geotools.xml.xsi.XSISimpleTypes;
import org.locationtech.udig.catalog.wmsc.server.WMSCComplexTypes._BoundingBoxType;
import org.locationtech.udig.catalog.wmsc.server.WMSCComplexTypes._CapabilityType;
import org.locationtech.udig.catalog.wmsc.server.WMSCComplexTypes._ServiceType;
import org.locationtech.udig.catalog.wmsc.server.WMSCComplexTypes._TileSetType;
import org.locationtech.udig.catalog.wmsc.server.WMSCComplexTypes._VendorSpecificCapabilitiesType;
import org.locationtech.udig.catalog.wmsc.server.WMSCComplexTypes._WMT_MS_CapabilitiesType;

/**
 * A WMSC Schema as defined at:
 * <p>
 * http://wiki.osgeo.org/wiki/WMS_Tiling_Client_Recommendation#GetCapabilities_Responses
 * </p>
 *
 * @author Emily Gouge (Refractions Research, Inc)
 * @since 1.1.0
 */
public class WMSCSchema implements Schema {

    private static Schema instance = new WMSCSchema();

    public static final URI NAMESPACE = null;// makeURI();

    static final Element[] elements = new Element[] {

            new WMSCElement("WMT_MS_Capabilities", _WMT_MS_CapabilitiesType.getInstance()), //$NON-NLS-1$

            new WMSCElement("VendorSpecificCapabilities", //$NON-NLS-1$
                    _VendorSpecificCapabilitiesType.getInstance()),

            new WMSCElement("TileSet", _TileSetType.getInstance()), //$NON-NLS-1$
            new WMSCElement("Service", _ServiceType.getInstance()), //$NON-NLS-1$
            new WMSCElement("Capability", _CapabilityType.getInstance()), //$NON-NLS-1$

            new WMSCElement("SRS", XSISimpleTypes.String.getInstance()), //$NON-NLS-1$
            new WMSCElement("BoundingBox", _BoundingBoxType.getInstance()), //$NON-NLS-1$
            new WMSCElement("Resolutions", XSISimpleTypes.String.getInstance()), //$NON-NLS-1$

            new WMSCElement("Width", XSISimpleTypes.Integer.getInstance()), //$NON-NLS-1$
            new WMSCElement("Height", XSISimpleTypes.Integer.getInstance()), //$NON-NLS-1$
            new WMSCElement("Format", XSISimpleTypes.String.getInstance()), //$NON-NLS-1$
            new WMSCElement("Layers", XSISimpleTypes.String.getInstance()), //$NON-NLS-1$
            new WMSCElement("Styles", XSISimpleTypes.String.getInstance()), // _StyleType.getInstance()) //$NON-NLS-1$

            new WMSCElement("Name", XSISimpleTypes.String.getInstance()), //$NON-NLS-1$
            new WMSCElement("Title", XSISimpleTypes.String.getInstance()), //$NON-NLS-1$
            new WMSCElement("OnlineResource", XSISimpleTypes.String.getInstance()) //$NON-NLS-1$
    };

    @Override
    public AttributeGroup[] getAttributeGroups() {
        return new AttributeGroup[0];
    }

    @Override
    public Attribute[] getAttributes() {
        return new Attribute[0];
    }

    @Override
    public int getBlockDefault() {
        return NONE;
    }

    @Override
    public ComplexType[] getComplexTypes() {
        return new ComplexType[0];
    }

    @Override
    public Element[] getElements() {
        return elements;
    }

    @Override
    public int getFinalDefault() {
        return NONE;
    }

    @Override
    public Group[] getGroups() {
        return new Group[0];
    }

    @Override
    public String getId() {
        return null;
    }

    private static Schema[] imports = new Schema[] { XLinkSchema.getInstance(),
            WMSSchema.getInstance() };

    @Override
    public Schema[] getImports() {
        return imports;
    }

    @Override
    public String getPrefix() {
        return "wmsc"; //$NON-NLS-1$
    }

    @Override
    public SimpleType[] getSimpleTypes() {
        return new SimpleType[0];
    }

    @Override
    public URI getTargetNamespace() {
        return NAMESPACE;
    }

    @Override
    public URI getURI() {
        return NAMESPACE;
    }

    @Override
    public String getVersion() {
        return "1.1.0"; //$NON-NLS-1$
    }

    @Override
    public boolean includesURI(URI arg0) {
        // We don't need to read the definition at all
        // --this is a specification, it shouldn't change.
        return true;
    }

    @Override
    public boolean isAttributeFormDefault() {
        return true;
    }

    @Override
    public boolean isElementFormDefault() {
        return true;
    }

    @Override
    public Map<Key, ?> getImplementationHints() {
        return Collections.emptyMap();
    }

    public static Schema getInstance() {
        return instance;
    }

    static class WMSCElement implements Element {

        private int max;

        private int min;

        private String name;

        private Type type;

        /**
         * @param name
         * @param type
         */
        public WMSCElement(String name, Type type) {
            super();
            this.name = name;
            this.type = type;
            this.min = 1;
            this.max = 1;
        }

        /**
         * @param max
         * @param min
         * @param name
         * @param type
         */
        public WMSCElement(String name, Type type, int min, int max) {
            super();
            this.max = max;
            this.min = min;
            this.name = name;
            this.type = type;
        }

        @Override
        public boolean isAbstract() {
            return false;
        }

        @Override
        public int getBlock() {
            return NONE;
        }

        @Override
        public String getDefault() {
            // TODO terminate
            return null;
        }

        @Override
        public int getFinal() {
            return NONE;
        }

        @Override
        public String getFixed() {
            // TODO Terminate
            return null;
        }

        @Override
        public boolean isForm() {
            // TODO Terminate
            return false;
        }

        @Override
        public String getId() {
            return null;
        }

        @Override
        public int getMaxOccurs() {
            // TODO Terminate
            return max;
        }

        @Override
        public int getMinOccurs() {
            // TODO Terminate
            return min;
        }

        @Override
        public String getName() {
            // TODO Terminate
            return name;
        }

        @Override
        public URI getNamespace() {
            return NAMESPACE;
        }

        @Override
        public boolean isNillable() {
            // TODO Terminate
            return false;
        }

        @Override
        public Element getSubstitutionGroup() {
            // TODO Terminate
            return null;
        }

        @Override
        public Type getType() {
            // TODO Terminate
            return type;
        }

        @Override
        public int getGrouping() {
            return ELEMENT;
        }

        @Override
        public Element findChildElement(String name) {
            return (this.name != null && this.name.equals(name)) ? this : null;
        }

        @Override
        public Element findChildElement(String localName, URI namespaceURI) {
            return (this.name != null && this.name.equals(localName)) ? this : null;
        }
    }

    static abstract class WMSCComplexType implements ComplexType {

        @Override
        public Type getParent() {
            return null;
        }

        @Override
        public boolean isAbstract() {
            return false;
        }

        @Override
        public String getAnyAttributeNameSpace() {
            return null;
        }

        @Override
        public int getBlock() {
            return NONE;
        }

        @Override
        public int getFinal() {
            return NONE;
        }

        @Override
        public String getId() {
            return null;
        }

        @Override
        public boolean isMixed() {
            return false;
        }

        @Override
        public boolean isDerived() {
            return false;
        }

        @Override
        public boolean cache(Element element, Map hints) {
            return true;
        }

        @Override
        public URI getNamespace() {
            return NAMESPACE;
        }

        @Override
        public Element findChildElement(String name) {
            return (getChild() == null) ? null : getChild().findChildElement(name);
        }

        protected boolean sameName(Element element, ElementValue value) {
            return element.getName().equals(value.getElement().getName());
        }
    }

    static class WMSCAttribute extends AttributeGT {

        public WMSCAttribute(String id, String name, URI namespace, SimpleType type, int use,
                String _default, String fixed, boolean form) {
            super(id, name, namespace, type, use, _default, fixed, form);
        }

        public WMSCAttribute(String name, SimpleType simpleType) {
            super(null, name, WMSCSchema.NAMESPACE, simpleType, OPTIONAL, null, null, false);
        }
    }

}
