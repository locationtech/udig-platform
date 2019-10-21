/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.tool.display;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.geotools.data.FeatureStore;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.objectproperty.ObjectPropertyCatalogListener;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.ui.operations.AbstractPropertyValue;
import org.locationtech.udig.ui.operations.PropertyValue;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Returns true if the value is a subclass of the layer's schema's default geometry. If the layer
 * does not have a schema false is returned.
 * <p>
 * The value must be a Geometry class fully qualified (org.locationtech.jts.geom.Geometry) unless
 * it is one of the following abbreviations (case is unimportant):
 * <ul>
 * <li>Geometry = org.locationtech.jts.geom.Geometry</li>
 * <li>Polygon = org.locationtech.jts.geom.Polygon</li>
 * <li>Point = org.locationtech.jts.geom.Point</li>
 * <li>MultiPolygon = org.locationtech.jts.geom.MultiPolygon</li>
 * <li>MultiPoint = org.locationtech.jts.geom.MultiPoint</li>
 * <li>MultiLineString, MultiLine = org.locationtech.jts.geom.MultiLineString</li>
 * <li>LinearRing = org.locationtech.jts.geom.LinearRing</li>
 * <li>Line, LineString = org.locationtech.jts.geom.LineString</li>
 * <li>GeometryCollection = org.locationtech.jts.geom.GeometryCollection</li>
 * </ul>
 * </p>
 * 
 * Note: If the object passed into isTrue is not the editLayer, and the editLayer
 * is locked, the editLayer will be used in place of the passed in layer.
 * 
 * TODO change this class name to EditGeometryProperty or something similar
 * 
 * @author jones
 * @since 1.1.0
 */
public class GeometryProperty extends AbstractPropertyValue<ILayer> implements PropertyValue<ILayer> {

	/*
	 * if working with Layer based on e.g. Temporary datastores the id's of the
	 * layers can't be resolved -> blocking operation {@link URL#equals(Object)}
	 * javadoc : 'Since hosts comparison requires name resolution, this
	 * operation is a blocking operation.' 
	 */
    Set<String> ids=new CopyOnWriteArraySet<String>();
    private volatile AtomicBoolean isEvaluating=new AtomicBoolean(false);

    @SuppressWarnings("unchecked")
    public synchronized boolean isTrue( ILayer object, String value ) {
        isEvaluating.set(true);
        
        if (object.getMap() != null && object.getMap().getEditManager().isEditLayerLocked()) {
            object = object.getMap().getEditManager().getEditLayer();
        }
        
        try{
            if( ids.add( object.getID().toString() ) ){
                IGeoResource resource = object.findGeoResource(FeatureStore.class);
                if( resource!=null )
                    CatalogPlugin.getDefault().getLocalCatalog().addCatalogListener(new ObjectPropertyCatalogListener(object, resource, isEvaluating, this));
            }
        SimpleFeatureType schema = object.getSchema();
        if (schema == null || schema.getGeometryDescriptor() == null)
            return false;

        try {

            Class< ? extends Object> declared = parseValue(value);
            Class< ? extends Object> type = schema.getGeometryDescriptor().getType().getBinding();
            return type.isAssignableFrom(declared);
        } catch (ClassNotFoundException e) {
            return false;
        }
        }finally{
            isEvaluating.set(false);
        }
    }

    private Class< ? extends Object> parseValue( String value ) throws ClassNotFoundException {
        Class< ? extends Object> result = null;

        if (value.equalsIgnoreCase("geometry")) //$NON-NLS-1$
            result = Geometry.class;
        else if (value.equalsIgnoreCase("polygon")) //$NON-NLS-1$
            result = Polygon.class;
        else if (value.equalsIgnoreCase("line") || value.equalsIgnoreCase("linestring")) //$NON-NLS-1$ //$NON-NLS-2$
            result = LineString.class;
        else if (value.equalsIgnoreCase("point")) //$NON-NLS-1$
            result = Point.class;
        else if (value.equalsIgnoreCase("multipoint")) //$NON-NLS-1$
            result = MultiPoint.class;
        else if (value.equalsIgnoreCase("multipolygon")) //$NON-NLS-1$
            result = MultiPolygon.class;
        else if (value.equalsIgnoreCase("linearring")) //$NON-NLS-1$
            result = LinearRing.class;
        else if (value.equalsIgnoreCase("multilinestring") || value.equalsIgnoreCase("multiline")) //$NON-NLS-1$ //$NON-NLS-2$
            result = MultiLineString.class;
        else if (value.equalsIgnoreCase("geometrycollection")) //$NON-NLS-1$
            result = GeometryCollection.class;

        if (result == null)
            result = Class.forName(value);
        return result;
    }

    public boolean canCacheResult() {
        return true;
    }

    public boolean isBlocking() {
        return true;
    }
}
