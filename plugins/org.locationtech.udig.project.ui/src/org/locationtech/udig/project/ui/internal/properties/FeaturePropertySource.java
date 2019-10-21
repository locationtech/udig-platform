/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.internal.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.command.EditCommand;
import org.locationtech.udig.project.command.factory.EditCommandFactory;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.Messages;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.util.CodeList;

/**
 * An adapter that allows features to act as a property source for a property sheet. The sheet
 * allows the victim's attributes to be edited and viewed.
 * 
 * @author jeichar
 * @since 0.3
 */
public class FeaturePropertySource implements IPropertySource2 {
    private static final String ID = "ID"; //$NON-NLS-1$
    private static final String DEFAULT_GEOM = "DEFAULT_GEOM"; //$NON-NLS-1$
    private static final String BOUNDING_BOX = "BOUNDING_BOX"; //$NON-NLS-1$
    private static final String FEATURE = "FEATURE"; //$NON-NLS-1$
    
    private SimpleFeature feature = null;
    private SimpleFeature old = null;
    
    private Map<Geometry, Object> geomProperties = new HashMap<Geometry, Object>();
    private Map<AttributeDescriptor,Object> attrProperties = new HashMap<AttributeDescriptor,Object>();
    private List<AttributeDescriptor> attrs;
    private IPropertyDescriptor[] descriptors;
    private boolean attribute;
    
    /** Are the attributes editable in cell editors. */ 
    private boolean editable = true;
    private IMap map;

    /**
     * Creates a new instance of FeaturePropertySource
     * 
     * @param feature The feature that this property source refers to.
     */
    public FeaturePropertySource( SimpleFeature feature ) {
        this(feature, false);
    }
    /**
     * Creates a new instance of FeaturePropertySource
     * 
     * @param feature2
     * @param attribute
     */
    public FeaturePropertySource( SimpleFeature feature2, boolean attribute ) {
        boolean editable=false;
        if( feature2 instanceof IAdaptable ){
            IAdaptable adaptable = (IAdaptable)feature2;
            if( adaptable.getAdapter(ILayer.class)!=null 
                    || adaptable.getAdapter(IMap.class)!=null ){
                editable=true;
            }
        }
    	init( feature2, attribute, editable);

    }
    
    public FeaturePropertySource( SimpleFeature feature2, boolean attribute, boolean editable ){
        init(feature2, attribute, editable);
    }
    
    private void init( SimpleFeature feature2, boolean attribute, boolean editable ) {
        this.feature = feature2;
        try {
            if (feature2 != null)
                old = SimpleFeatureBuilder.copy(feature);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.attribute = attribute;
        this.editable = editable;
        if( editable ){
            IAdaptable adaptable = (IAdaptable)feature2;
            if( adaptable.getAdapter(ILayer.class)!=null ){
                ILayer layer=(ILayer) adaptable.getAdapter(ILayer.class);
                map=layer.getMap();
            }else if( adaptable.getAdapter(IMap.class)!=null ){
                map = (IMap) adaptable.getAdapter(IMap.class);
            }else{
                map=ApplicationGIS.getActiveMap();
            }

        }
    }
    
    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
     */
    public Object getEditableValue() {
        return ""; //$NON-NLS-1$
    }
    
    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        if (descriptors == null) {
            boolean hasAttrs = false;
            List<IPropertyDescriptor> descrps = new ArrayList<IPropertyDescriptor>();
            PropertyDescriptor d = new PropertyDescriptor(ID, "ID"); //$NON-NLS-1$
            d.setCategory(Messages.FeaturePropertySource_feature); 
            descrps.add(d);
            d = new GeometryPropertyDescriptor(DEFAULT_GEOM, 
            		Messages.FeaturePropertySource_defaultGeometry);
            d.setCategory(Messages.FeaturePropertySource_geometries); 
            descrps.add(d);
            d = new PropertyDescriptor(BOUNDING_BOX, Messages.FeaturePropertySource_bounds); 
            d.setCategory(Messages.FeaturePropertySource_feature); 
            d.setLabelProvider(new LabelProvider(){
                /**
                 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
                 */
                public String getText( Object element ) {
                    Envelope bbox = (Envelope) element;
                    String minx = String.valueOf(bbox.getMinX());
                    minx = minx.substring(0, Math.min(10, minx.length()));
                    String maxx = String.valueOf(bbox.getMaxX());
                    maxx = maxx.substring(0, Math.min(10, maxx.length()));
                    String miny = String.valueOf(bbox.getMinY());
                    miny = miny.substring(0, Math.min(10, miny.length()));
                    String maxy = String.valueOf(bbox.getMaxY());
                    maxy = maxy.substring(0, Math.min(10, maxy.length()));
                    return "(" + minx + "," + miny + "), (" + maxx + "," + maxy + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                }
            });
            descrps.add(d);
            if (!attribute) {
                SimpleFeatureType ft = feature.getFeatureType();
                attrs = ft.getAttributeDescriptors();
                int i = -1;
                for (AttributeDescriptor at : attrs) {
                	i++;
                    String name = at.getName().getLocalPart().toLowerCase();
                    name = name.substring(0, 1).toUpperCase() + name.substring(1);
                    if ( at instanceof GeometryDescriptor ) {
                        if (feature.getAttribute(at.getLocalName()) != feature.getDefaultGeometry()) {
                            d = new GeometryPropertyDescriptor(Integer.valueOf(i), name
                                    + Messages.FeaturePropertySource_geometry); 
                            d.setCategory(Messages.FeaturePropertySource_geometries); 
                            descrps.add(d);
                        }
                    } else {
                        if (SimpleFeature.class.isAssignableFrom(at.getType().getBinding())) {
                            d = new PropertyDescriptor(FEATURE + Integer.valueOf(i), name);
                        } else if (Collection.class.isAssignableFrom(at.getType().getBinding()))
                            d = new PropertyDescriptor(Integer.valueOf(i), name);
                        else {
                            d = new AttributePropertyDescriptor(Integer.valueOf(i), name, at, ft, editable);
                            // if (String.class.isAssignableFrom(at.getType()))
                            // d = new TextPropertyDescriptor(Integer.valueOf(i), name);
                            // if (Integer.class.isAssignableFrom(at.getType()))
                            // d = new TextPropertyDescriptor(Integer.valueOf(i), name);
                            // if (Double.class.isAssignableFrom(at.getType()))
                            // d = new TextPropertyDescriptor(Integer.valueOf(i), name);
                            // if (Boolean.class.isAssignableFrom(at.getType()))
                            // d = new ComboBoxPropertyDescriptor(Integer.valueOf(i), name,
                            // new String[]{"true","false"});
                            // if (Float.class.isAssignableFrom(at.getType()))
                            // d = new TextPropertyDescriptor(Integer.valueOf(i), name);
                        }
                        // d.setValidator(new AttributeValidator(at));

                        if (name.equalsIgnoreCase("name")) { //$NON-NLS-1$
                            d.setCategory(Messages.FeaturePropertySource_feature); 
                            descrps.add(0, d);
                        } else {
                            hasAttrs = true;
                            d.setCategory(Messages.FeaturePropertySource_featureAttributes); 
                            descrps.add(d);
                        }
                    }
                }
            }
            if (!hasAttrs) {
                d = new PropertyDescriptor(
                        "", Messages.FeaturePropertySource_noOtherAttributes);   //$NON-NLS-1$
                d.setCategory(Messages.FeaturePropertySource_featureAttributes); 
                descrps.add(d);
            }
            descriptors = new IPropertyDescriptor[descrps.size()];
            descrps.toArray(descriptors);
        }

        IPropertyDescriptor[] c=new IPropertyDescriptor[descriptors.length];
        System.arraycopy(descriptors, 0, c, 0, c.length);
        return c;
    }
    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
     */
    public Object getPropertyValue( Object id ) {
        if (id instanceof String) {
            String sid = (String) id;
            if (sid.equals(ID))
                return feature.getID();
            if (sid.equals(DEFAULT_GEOM))
                return getGeomProperty((Geometry) feature.getDefaultGeometry());
            if (sid.equals(BOUNDING_BOX))
                return feature.getBounds();
            if (sid.startsWith(FEATURE)) {
                int i = Integer.parseInt(sid.substring(FEATURE.length()));
                return new FeaturePropertySource((SimpleFeature) feature.getAttribute(i), true);
            }
        }
        if (id instanceof Integer) {
            Integer i = (Integer) id;
            AttributeDescriptor attrType = attrs.get(i.intValue());
            if ( attrType instanceof GeometryDescriptor ) 
                return getGeomProperty((Geometry) feature.getAttribute(i.intValue()));
            if (Collection.class.isAssignableFrom(attrType.getType().getBinding()))
                return getAttrProperty(attrType, feature.getAttribute(i.intValue()));
            // return feature.getAttribute(i.intValue()).toString();
            Object attr = feature.getAttribute(i.intValue());

            if (!(attr instanceof Boolean) && !(attr instanceof CodeList)) {
                if (attr != null)
                    return attr.toString();
                return ""; //$NON-NLS-1$
            }

            if (attr instanceof Boolean) {
                if (((Boolean) attr).booleanValue())
                    return Integer.valueOf(1);
                return Integer.valueOf(0);
            }
            if (attr instanceof CodeList) {
                CodeList list = (CodeList) attr;
                return Integer.valueOf(list.ordinal()).toString();
            }
        }
        return null;
    }
    
    public SimpleFeature getFeature() {
        return feature;
    }
    
    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
     */
    public boolean isPropertySet( Object id ) {
        if (id.equals(ID))
            return feature.getID() == old.getID();
        if (id.equals(DEFAULT_GEOM))
            return feature.getDefaultGeometry() == old.getDefaultGeometry();
        if (id.equals(BOUNDING_BOX))
            return feature.getBounds() == old.getBounds();
        if (id instanceof Integer) {
            int i = ((Integer) id).intValue();
            Object attr = feature.getAttribute(i);
            if (attr instanceof String || attr instanceof Integer || attr instanceof Double
                    || attr instanceof Float || attr instanceof Boolean)
                return attr.equals(old.getAttribute(i));
        }
        return false;
    }
    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
     */
    public void resetPropertyValue( Object id ) {
        try {
            if (id instanceof Integer) {
                int i = ((Integer) id).intValue();
                Object attr = feature.getAttribute(i);
                if (attr instanceof String || attr instanceof Integer || attr instanceof Double
                        || attr instanceof Float || attr instanceof Boolean)
                    feature.setAttribute(i, old.getAttribute(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPropertyValue( Object id, Object value ) {
        try {
            if (id instanceof Integer) {
                int i = ((Integer) id).intValue();
                Object attr = feature.getAttribute(i);
                //for String check and nullify if empty
                if (attr instanceof String) {
                    value = StringUtils.trimToNull((String) value);
                }
                if (hasValueChanged(value, attr)) {
                    EditCommand command = (EditCommand) EditCommandFactory.getInstance().createSetAttributeCommand(
                            attrs.get(i).getName().getLocalPart(), value);
                    map.sendCommandASync(command);
                }
                if (attr instanceof String) {
                    feature.setAttribute(i, value);
                } else if (attr instanceof Integer) {
                    feature.setAttribute(i, (Integer)value);
                }else if (attr instanceof Long) {
                    feature.setAttribute(i, (Long) value);
                } else if (attr instanceof Double) {
                    feature.setAttribute(i, (Double) value);
                } else if (attr instanceof Float) {
                    feature.setAttribute(i, (Float) value);
                } else if (attr instanceof Boolean) {
                    feature.setAttribute(i, Boolean.valueOf(((Integer) value).intValue() == 0
                            ? true : false));
                } else if (attr == null) {
                    //if attr value is initially null the feature.getAttribute(i) 
                    //will return null. In this case, set the value by obtaining its name 
                    //from the AttributeDescriptor list
                    feature.setAttribute(attrs.get(i).getName().getLocalPart(), value);
                }
            }
            if (value instanceof Geometry) {
                if (id.equals(DEFAULT_GEOM)){
                    feature.setDefaultGeometry((Geometry) value);

                    EditCommand command = (EditCommand) EditCommandFactory.getInstance().createSetGeometryCommand((Geometry) value);
                    map.sendCommandASync(command);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean hasValueChanged(Object newValue, Object currentValue) {
        if (newValue == null && currentValue == null) {
                return false;
        } if (newValue != null && newValue.equals(currentValue)) {
                return false;
        }
        return true;
    }
    
    private Object getGeomProperty( Geometry id ) {
        Object geom = geomProperties.get(id);
        if (geom == null) {
            geom = new GeomPropertySource(id);
            geomProperties.put(id, geom);
        }
        return geom;
    }
    private Object getAttrProperty( AttributeDescriptor id, Object value ) {
        Object attr = attrProperties.get(id);
        if (attr == null) {
            attr = new AttributePropertySource(id, value);
            attrProperties.put(id, attr);
        }
        return attr;

    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource2#isPropertyResettable(java.lang.Object)
     */
    public boolean isPropertyResettable( Object id ) {
        return true;
    }
}
