/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.core.internal;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.DataUtilities;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.collection.AdaptorFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.util.factory.GeoTools;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.udig.core.AdapterUtil;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.filter.identity.Identifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.CodeList;
/**
 * A utility class for playing with features.
 * 
 * @author jeichar
 */
public class FeatureUtils {
    
    /**
     * Create a new Features from the provided coordinates and of the type indicated by geomType
     *
     * @param coordCRS The crs of the coordinates provided
     * @param destinationCRS The desired crs of the geometry
     * @param type the feature type of the object created
     * @param coordinates the coordinates that will be used to create the new feature
     * @param geomType the type of geometry that will be created
     * @return A new features.
     * @throws Exception
     */
    public static <T extends Geometry> SimpleFeature createFeature( CoordinateReferenceSystem coordCRS,
            CoordinateReferenceSystem destinationCRS, SimpleFeatureType type, Coordinate[] coordinates,
            Class<T> geomType ) throws Exception {

        transform(coordCRS, destinationCRS, coordinates);
        Object[] attrs = new Object[type.getAttributeCount()];
        for( int i = 0; i < attrs.length; i++ ) {
            attrs[i] = setDefaultValue(type.getDescriptor(i));
        }
        final SimpleFeature newFeature = SimpleFeatureBuilder.build(type,attrs,null);
        // Class geomType = type.getDefaultGeometry().getType();

        T geom = GeometryBuilder.create().safeCreateGeometry(geomType, coordinates);
        newFeature.setDefaultGeometry(geom);

        return newFeature;
    }

    /**
     * @param object
     * @param object2
     */
    private static Object setDefaultValue( AttributeDescriptor type ) {
        if (type.getDefaultValue() != null)
            return type.getDefaultValue();
        if (Boolean.class.isAssignableFrom(type.getType().getBinding())
                || boolean.class.isAssignableFrom(type.getType().getBinding()))
            return Boolean.valueOf(false);
        if (String.class.isAssignableFrom(type.getType().getBinding()))
            return ""; //$NON-NLS-1$
        if (Integer.class.isAssignableFrom(type.getType().getBinding()))
            return Integer.valueOf(0);
        if (Double.class.isAssignableFrom(type.getType().getBinding()))
            return Double.valueOf(0);
        if (Float.class.isAssignableFrom(type.getType().getBinding()))
            return Float.valueOf(0);
        if (CodeList.class.isAssignableFrom(type.getType().getBinding())) {
            return type.getDefaultValue();
        }
        return null;
    }

    /**
     * Transforms coordinates into the layer CRS if nessecary
     * 
     * @throws Exception
     */
    private static void transform( CoordinateReferenceSystem coordCRS,
            CoordinateReferenceSystem destinationCRS, Coordinate[] coordinates ) throws Exception {
        if( coordCRS==null || destinationCRS==null) 
            return;
        MathTransform mt = CRS.findMathTransform(coordCRS, destinationCRS, true );
        if (mt == null || mt.isIdentity())
            return;
        double[] coords = new double[coordinates.length * 2];
        for( int i = 0; i < coordinates.length; i++ ) {
            coords[i * 2] = coordinates[i].x;
            coords[i * 2 + 1] = coordinates[i].y;
        }
        mt.transform(coords, 0, coords, 0, coordinates.length);
        for( int i = 0; i < coordinates.length; i++ ) {
            coordinates[i].x = coords[i * 2];
            coordinates[i].y = coords[i * 2 + 1];
        }
    }

    /**
     * Returns 0 if the features are the same. 1 if the attributes are the same but have different
     * featureIDs and -1 if attributes are different or are of different featureTypes.
     * 
     * @param feature1
     * @param feature2
     * @return
     */
    public static int same( SimpleFeature feature1, SimpleFeature feature2 ) {
        if (DataUtilities.compare(feature1.getFeatureType(), feature2.getFeatureType()) != 0) {
            return -1;
        }
        for( int i = 0; i < feature1.getAttributeCount(); i++ ) {
            if (feature1.getAttribute(i) == null) {
                if (feature2.getAttribute(i) != null)
                    return -1;
                else
                    continue;
            }
            if (feature1.getAttribute(i) instanceof Geometry) {
                Geometry geom1 = (Geometry) feature1.getAttribute(i);
                if (feature2.getAttribute(i) instanceof Geometry) {
                    Geometry geom2 = (Geometry) feature2.getAttribute(i);
                    if (geom1.equalsExact(geom2))
                        continue;
                    else
                        return -1;
                } else
                    return -1;
            }
            if (!feature1.getAttribute(i).equals(feature2.getAttribute(i)))
                return -1;
        }

        return feature1.getID().equals(feature2.getID()) ? 0 : 1;
    }


    public static Map<String,String> createAttributeMapping( SimpleFeatureType sourceSchema, SimpleFeatureType targetSchema ){
        Map<String,String> queryAttributes=new HashMap<String, String>();
        performDirectMapping(sourceSchema, targetSchema, queryAttributes);
        mapGeometryAttributes(sourceSchema, targetSchema, queryAttributes);
        return queryAttributes;
    }

    /**
     * Maps the default geometry attribute regardless of whether they are the same type.
     */
    @SuppressWarnings("unchecked")
    private static void mapGeometryAttributes( SimpleFeatureType sourceSchema, SimpleFeatureType targetSchema, Map<String, String> queryAttributes ) {
        // Now we'll match the geometry on type only. I don't care if it has the same type name.
        GeometryDescriptor defaultGeometry = targetSchema.getGeometryDescriptor();
        if( defaultGeometry==null ){
            return;
        }
        else if (!queryAttributes.containsKey(defaultGeometry.getName())) {
            // first check source's default geom and see if it matches
            Class<?> binding = sourceSchema.getGeometryDescriptor().getType().getBinding();
			if (defaultGeometry.getType().getBinding().isAssignableFrom(
                    binding)) {
                queryAttributes.put(defaultGeometry.getName().getLocalPart(), sourceSchema.getGeometryDescriptor()
                        .getName().getLocalPart());
            } else {
                // we have to look through all the source attributes looking for a geometry that
                // matches.
                boolean found = false;
                for( int i = 0; i < sourceSchema.getAttributeCount(); i++ ) {
                    AttributeDescriptor source = sourceSchema.getDescriptor(i);
                    if (defaultGeometry.getType().getBinding().isAssignableFrom(source.getType().getBinding())) {
                        queryAttributes.put(defaultGeometry.getName().getLocalPart(), source.getName().getLocalPart());
                        found = true;
                        break;
                    }
                }
                // ok so we're going to have to do some transformations. Match default geometries
                // then.
                if (!found) {
                    queryAttributes.put(defaultGeometry.getName().getLocalPart(), sourceSchema
                            .getGeometryDescriptor().getName().getLocalPart());
                }
            }
        }
    }

    /**
     * Maps attributes with the same name and same types to each other.
     */
    @SuppressWarnings("unchecked")
    private static void performDirectMapping( SimpleFeatureType sourceSchema, SimpleFeatureType targetSchema, Map<String, String> queryAttributes ) {
        for( int i = 0; i < sourceSchema.getAttributeCount(); i++ ) {
            AttributeDescriptor source = sourceSchema.getDescriptor(i);
            for( int j = 0; j < targetSchema.getAttributeCount(); j++ ) {
                AttributeDescriptor target = targetSchema.getDescriptor(j);

                // don't worry about case of attribute name
                if (target.getName().getLocalPart().equalsIgnoreCase(source.getName().getLocalPart())
                       /* && target.getType().getBinding().isAssignableFrom(source.getType().getBinding())*/) {
                    queryAttributes.put(target.getName().getLocalPart(), source.getName().getLocalPart());
                }
            }
        }
    }
    
    private static GeometryBuilder geomBuilder = GeometryBuilder.create();

    /**
    * Adapts a collection of features to a FeatureCollection
    *
    * @param collection
    * @return
    */
    public static FeatureCollection<SimpleFeatureType, SimpleFeature> toFeatureCollection( final Collection<SimpleFeature> collection, SimpleFeatureType type ) {
        return new AdaptorFeatureCollection("collection",type){
            @Override
            protected void closeIterator( Iterator arg0 ) {
            }
            @Override
            protected Iterator openIterator() {
                return collection.iterator();
            }
            @Override
            public int size() {
                return collection.size();
            }
        };
    }

    public static Collection<SimpleFeature> copyFeature( final SimpleFeature source,
            final SimpleFeatureType destinationSchema, final Map<String, String> attributeMap, final MathTransform mt ) {

        return new AbstractCollection<SimpleFeature>(){
            public Iterator<SimpleFeature> iterator() {
                final Map<String, Iterator< ? extends Geometry>> geometries = new HashMap<String, Iterator< ? extends Geometry>>();
                final Object[] attributes = copyAttributes(destinationSchema, source,
                        geometries, attributeMap, mt);

                return new Iterator<SimpleFeature>(){
                    SimpleFeature next, template;
                    public boolean hasNext() { 
                        if (template == null) {
                            try {
                                template = next = SimpleFeatureBuilder.build( destinationSchema, attributes, null);

                            } catch (Exception e) {
                                // try then next one then
                                CorePlugin.log("", e); //$NON-NLS-1$
                            }
                            return true;
                        }
                        if (geometries.isEmpty())
                            return false;

                        while( next == null && !geometries.isEmpty() ) {
                            try {
                                next = SimpleFeatureBuilder.copy(template);
                                Set<Map.Entry<String, Iterator< ? extends Geometry>>> entries = geometries
                                        .entrySet();
                                for( Iterator<Map.Entry<String, Iterator< ? extends Geometry>>> iter = entries
                                        .iterator(); iter.hasNext(); ) {
                                    Map.Entry<String, Iterator< ? extends Geometry>> entry = iter
                                            .next();
                                    Geometry geom = entry.getValue().next();
                                    next.setAttribute(entry.getKey(), transformGeom(geom, mt));
                                    if (!entry.getValue().hasNext())
                                        iter.remove();
                                }
                                return true;

                            } catch (Exception e) {
                                // try then next one then
                                CorePlugin.log("", e); //$NON-NLS-1$
                            }
                        }
                        return false;
                    }
                    public SimpleFeature next() {
                        if (next == null)
                            throw new IndexOutOfBoundsException("No more elements in iterator."); //$NON-NLS-1$
                        SimpleFeature result=next;
                        next=null;
                        return result;
                    }
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
            public int size() {
                int size=0;
                for( Iterator<SimpleFeature> iter=iterator(); iter.hasNext(); iter.next()) size++;
                return size;
            }
        };
    }

    private static Object[] copyAttributes( SimpleFeatureType destSchema, SimpleFeature source,
            Map<String, Iterator< ? extends Geometry>> geometries, Map<String, String> attributeMap, MathTransform mt  ) {
        Object[] attributes = new Object[destSchema.getAttributeCount()];
        
        //dummy progress monitor
        IProgressMonitor mon = new NullProgressMonitor();
        
        for( int i = 0; i < attributes.length; i++ ) {
            String sourceAttributeName = destSchema.getDescriptor(i).getName().getLocalPart();
            String name = attributeMap.get(sourceAttributeName);
            //if an attribute match exists set its value
            if (name != null) {                 
                if (destSchema.getDescriptor(sourceAttributeName).getType().getBinding().isAssignableFrom(
                        source.getFeatureType().getDescriptor(sourceAttributeName).getType().getBinding())) {
                    attributes[i] = source.getAttribute(name);
                } else {
                    //if not directly assignable then check for an adapter
                    if (AdapterUtil.instance.canAdaptTo(
                            source.getAttribute(sourceAttributeName), 
                            destSchema.getDescriptor(i).getType().getBinding())) {
                        try {
                            attributes[i] = AdapterUtil.instance.adaptTo(
                                    destSchema.getDescriptor(i).getType().getBinding(), 
                                    source.getAttribute(sourceAttributeName), mon);
                        } catch (Exception e) {
                        	CorePlugin.log("", e);
                        }
                    } else {
                        attributes[i] = destSchema.getDescriptor(i).getDefaultValue();
                    }
                }
            } else {
                //if we reach here use the default value
                attributes[i] = destSchema.getDescriptor(i).getDefaultValue();
            }
            if (attributes[i] instanceof Geometry) {
                Class< ? extends Geometry> geomType = (Class< ? extends Geometry>) destSchema
                        .getDescriptor(i).getType().getBinding();
                if (!geomType.isAssignableFrom(attributes[i].getClass())) {
                    Collection< ? extends Geometry> geom = createCompatibleGeometry(
                            (Geometry) attributes[i], geomType);
                    Iterator< ? extends Geometry> giter = geom.iterator();
                    attributes[i] = giter.next();
                    if (giter.hasNext())
                        geometries.put(sourceAttributeName, giter);
                }
                attributes[i] = transformGeom((Geometry) attributes[i], mt);
            }
        }
        return attributes;
    }
    private static Geometry transformGeom( Geometry geom, MathTransform mt ) {
        if (mt != null) {
            try {
                return JTS.transform(geom, mt);
            } catch (TransformException e) {
                CorePlugin.log("", e); //$NON-NLS-1$
                return geom;
            }
        }
        return geom;
    }
   private static Collection< ? extends Geometry> createCompatibleGeometry( Geometry geom,
            Class< ? extends Geometry> targetType ) {
        Collection<Geometry> result = new ArrayList<Geometry>();
        if (nonCollectionToPoint(geom, targetType, result))
            return result;
        else if (collectionToPoint(geom, targetType, result))
            return result;
        else if (nonCollectionToMultiPoint(geom, targetType, result))
            return result;
        else if (collectionToMultiPoint(geom, targetType, result))
            return result;
        else if (simpleToLine(geom, targetType, result))
            return result;
        else if (collectionToLine(geom, targetType, result))
            return result;
        else if (simpleToMultiLine(geom, targetType, result))
            return result;
        else if (collectionToMultiLine(geom, targetType, result))
            return result;
        else if (polygonToMultiLine(geom, targetType, result))
            return result;
        else if (polygonToLine(geom, targetType, result))
            return result;
        else if (multiPolygonToLine(geom, targetType, result))
            return result;
        else if (multiPolygonToMultiLine(geom, targetType, result))
            return result;
        else if (simpleToPolygon(geom, targetType, result))
            return result;
        else if (collectionToPolygon(geom, targetType, result))
            return result;
        else if (multiPolygonToPolygon(geom, targetType, result))
            return result;
        else if (simpleToMultiPolygon(geom, targetType, result))
            return result;
        else if (collectionToMultiPolygon(geom, targetType, result))
            return result;
        else if (polygonToMultiPolygon(geom, targetType, result))
            return result;
        else if (toLinearRing(geom, targetType, result))
            return result;
        throw new IllegalArgumentException(
                "do not know how transform from " + geom.getClass().getName() + " to " + targetType.getName()); //$NON-NLS-1$ //$NON-NLS-2$

    }

    /**
     * return true if Geometry is a point and targetType is a point. Result will be populated
     * with the new point
     */
   private static boolean nonCollectionToPoint( Geometry geom, Class< ? extends Geometry> targetType,
            Collection<Geometry> result ) {
        if (!(geom instanceof GeometryCollection) && Point.class == targetType) {
            result.add(geom.getCentroid());
            return true;
        }
        return false;
    }
   private static boolean collectionToPoint( Geometry geom, Class< ? extends Geometry> targetType,
            Collection<Geometry> result ) {
        if (geom instanceof GeometryCollection && Point.class == targetType) {
            for( int i = 0; i < geom.getNumGeometries(); i++ ) {
                result.add(geom.getGeometryN(i).getCentroid());
            }
            return true;
        }
        return false;
    }
   private static boolean collectionToMultiPoint( Geometry geom, Class< ? extends Geometry> targetType,
            Collection<Geometry> result ) {
        if (geom instanceof GeometryCollection && MultiPoint.class == targetType) {
            Point[] points = new Point[geom.getNumGeometries()];
            for( int i = 0; i < geom.getNumGeometries(); i++ ) {
                points[i] = geom.getGeometryN(i).getCentroid();
            }
            result.add(geomBuilder.factory.createMultiPoint(points));
            return true;
        }
        return false;
    }
   private static boolean nonCollectionToMultiPoint( Geometry geom, Class< ? extends Geometry> targetType,
            Collection<Geometry> result ) {
        if (!(geom instanceof GeometryCollection) && MultiPoint.class == targetType) {
            result.add(geomBuilder.factory
                    .createMultiPoint(geom.getCentroid().getCoordinates()));
            return true;
        }
        return false;
    }
   private static boolean simpleToLine( Geometry geom, Class< ? extends Geometry> targetType,
            Collection<Geometry> result ) {
        if (!(geom instanceof Polygon) && !(geom instanceof GeometryCollection)
                && LineString.class == targetType) {
            result.add(geomBuilder.safeCreateGeometry(targetType, geom.getCoordinates()));
            return true;
        }
        return false;
    }
   private static boolean collectionToLine( Geometry geom, Class< ? extends Geometry> targetType,
            Collection<Geometry> result ) {
        if (!(geom instanceof Polygon) && !(geom instanceof MultiPolygon)
                && (geom instanceof GeometryCollection) && LineString.class == targetType) {
            for( int i = 0; i < geom.getNumGeometries(); i++ ) {
                result.add(geomBuilder.safeCreateGeometry(targetType, geom.getGeometryN(i)
                        .getCoordinates()));
            }
            return true;
        }
        return false;
    }
   private static boolean simpleToMultiLine( Geometry geom, Class< ? extends Geometry> targetType,
            Collection<Geometry> result ) {
        if (!(geom instanceof Polygon) && !(geom instanceof GeometryCollection)
                && MultiLineString.class == targetType) {
            result.add(geomBuilder.safeCreateGeometry(targetType, geom.getCoordinates()));
            return true;
        }
        return false;
    }
   private static boolean collectionToMultiLine( Geometry geom, Class< ? extends Geometry> targetType,
            Collection<Geometry> result ) {
        if (!(geom instanceof Polygon) && !(geom instanceof MultiPolygon)
                && (geom instanceof GeometryCollection) && MultiLineString.class == targetType) {
            LineString[] geoms = new LineString[geom.getNumGeometries()];
            for( int i = 0; i < geom.getNumGeometries(); i++ ) {
                geoms[i] = geomBuilder.safeCreateGeometry(LineString.class, geom
                        .getGeometryN(i).getCoordinates());
            }
            result.add(geomBuilder.factory.createMultiLineString(geoms));
            return true;
        }
        return false;
    }
   private static boolean simpleToPolygon( Geometry geom, Class< ? extends Geometry> targetType,
            Collection<Geometry> result ) {
        if (!(geom instanceof Polygon) && !(geom instanceof GeometryCollection)
                && Polygon.class == targetType) {
            result.add(geomBuilder.safeCreateGeometry(targetType, geom.getCoordinates()));
            return true;
        }
        return false;
    }
   private static boolean collectionToPolygon( Geometry geom, Class< ? extends Geometry> targetType,
            Collection<Geometry> result ) {
        if (!(geom instanceof MultiPolygon) && geom instanceof GeometryCollection
                && Polygon.class == targetType) {
            for( int i = 0; i < geom.getNumGeometries(); i++ ) {
                result.add(geomBuilder.safeCreateGeometry(targetType, geom.getGeometryN(i)
                        .getCoordinates()));
            }
            return true;
        }
        return false;
    }
   private static boolean polygonToLine( Geometry geom, Class< ? extends Geometry> targetType,
            Collection<Geometry> result ) {
        if (geom instanceof Polygon && LineString.class == targetType) {
            Polygon polygon = (Polygon) geom;
            result.add(geomBuilder.safeCreateGeometry(LineString.class, polygon
                    .getExteriorRing().getCoordinates()));
            int i = 0;
            while( i < polygon.getNumInteriorRing() ) {
                result.add(geomBuilder.safeCreateGeometry(LineString.class, polygon
                        .getInteriorRingN(i).getCoordinates()));
                i++;
            }
            return true;
        }
        return false;
    }
   private static boolean polygonToMultiLine( Geometry geom, Class< ? extends Geometry> targetType,
            Collection<Geometry> result ) {
        if (geom instanceof Polygon && MultiLineString.class == targetType) {
            ArrayList<Geometry> tmp = new ArrayList<Geometry>();
            if (!polygonToLine(geom, LineString.class, tmp))
                throw new RuntimeException(
                        "Huh?  multi polygons should only have polygons in them"); //$NON-NLS-1$
            result.add(geomBuilder.factory
                    .createMultiLineString(tmp.toArray(new LineString[0])));
            return true;
        }
        return false;
    }
   private static boolean multiPolygonToLine( Geometry geom, Class< ? extends Geometry> targetType,
            Collection<Geometry> result ) {
        if (geom instanceof MultiPolygon && LineString.class == targetType) {
            for( int i = 0; i < geom.getNumGeometries(); i++ ) {
                if (!polygonToLine(geom.getGeometryN(i), targetType, result))
                    throw new RuntimeException(
                            "Huh?  multi polygons should only have polygons in them"); //$NON-NLS-1$
            }
            return true;
        }
        return false;
    }
   private static boolean multiPolygonToMultiLine( Geometry geom, Class< ? extends Geometry> targetType,
            Collection<Geometry> result ) {
        if (geom instanceof MultiPolygon && MultiLineString.class == targetType) {

            for( int i = 0; i < geom.getNumGeometries(); i++ ) {
                if (!polygonToMultiLine(geom.getGeometryN(i), targetType, result))
                    throw new RuntimeException(
                            "Huh?  multi polygons should only have polygons in them, found a " + geom.getGeometryN(i)); //$NON-NLS-1$
            }

            return true;
        }
        return false;
    }
   private static boolean multiPolygonToPolygon( Geometry geom, Class< ? extends Geometry> targetType,
            Collection<Geometry> result ) {
        if (geom instanceof MultiPolygon && Polygon.class == targetType) {
            for( int j = 0; j < geom.getNumGeometries(); j++ ) {
                result.add(geom.getGeometryN(j));
            }

            return true;
        }
        return false;
    }
   private static boolean simpleToMultiPolygon( Geometry geom, Class< ? extends Geometry> targetType,
            Collection<Geometry> result ) {
        if (!(geom instanceof Polygon) && !(geom instanceof GeometryCollection)
                && MultiPolygon.class == targetType) {
            result.add(geomBuilder.safeCreateGeometry(targetType, geom.getCoordinates()));
            return true;
        }
        return false;
    }
   private static boolean collectionToMultiPolygon( Geometry geom, Class< ? extends Geometry> targetType,
            Collection<Geometry> result ) {
        if (!(geom instanceof Polygon) && geom instanceof GeometryCollection
                && MultiPolygon.class == targetType) {
            for( int i = 0; i < geom.getNumGeometries(); i++ ) {
                result.add(geomBuilder.safeCreateGeometry(targetType, geom.getGeometryN(i)
                        .getCoordinates()));
            }
            return true;
        }
        return false;
    }
   private static boolean polygonToMultiPolygon( Geometry geom, Class< ? extends Geometry> targetType,
            Collection<Geometry> result ) {
        if (geom instanceof Polygon && MultiPolygon.class == targetType) {
            result.add(geomBuilder.factory.createMultiPolygon(new Polygon[]{(Polygon) geom}));
            return true;
        }
        return false;
    }
   private static boolean toLinearRing( Geometry geom, Class< ? extends Geometry> targetType,
            Collection<Geometry> result ) {
        if (!(targetType == LinearRing.class))
            return false;
        ArrayList<Geometry> tmp = new ArrayList<Geometry>();
        if (!simpleToLine(geom, LineString.class, tmp)) {
            if (!collectionToLine(geom, LineString.class, tmp)) {
                if (!polygonToLine(geom, LineString.class, tmp)) {
                    if (!multiPolygonToLine(geom, LineString.class, tmp)) {
                        return false;
                    }
                }
            }
        }

        for( Geometry geometry : tmp ) {
            result.add(geomBuilder.safeCreateGeometry(targetType, geometry.getCoordinates()));
        }
        return true;
    }

    /**
     * Performs a lookup for the provided propertyName in the {@link FeatureType}. Matching process
     * finds case sensitive attribute first, after that case insensitive comparison returns the
     * first matching attribute
     *
     * @param featureType FeatureType to find named attribute
     * @param propertyName attribute name
     * @return the actual propertyName (case sensitive) or null if none is found
     * @throws IllegalArgumentException if featureType and/or property is null
     */
    public static String getActualPropertyName(final SimpleFeatureType featureType,
            final String propertyName) {

        if (featureType == null) {
            throw new IllegalArgumentException("featureType cannot be null");
        }
        if (propertyName == null) {
            throw new IllegalArgumentException("propertyName cannot be null");
        }

        final String internalPropertyName = propertyName.trim();

        if (internalPropertyName.isEmpty()) {
            // avoid iteration through all properties twice
            return null;
        }
        // if an exact match is found return it
        for (int i = 0; i < featureType.getAttributeCount(); i++) {
            String name = featureType.getDescriptor(i).getLocalName();
            if (internalPropertyName.equals(name)) {
                return name;
            }
        }

        // otherwise return the first match found by performing a case
        // insensitive check for equality
        for (int i = 0; i < featureType.getAttributeCount(); i++) {
            String name = featureType.getDescriptor(i).getLocalName();
            if (internalPropertyName.equalsIgnoreCase(name)) {
                return name;
            }
        }
        return null;
    }

    /**
     * Performs a lookup for the provided propertyNames in the {@link FeatureType} using
     * {@link #getActualPropertyName(SimpleFeatureType, String)} approach
     *
     * @param featureType  FeatureType to find named attribute
     * @param propertyName List of Properties to check
     * @return a list of actual propertyName (case sensitive). If a propertyName is not present then
     *         it is skipped from the list
     * @throws IllegalArgumentException if featureType and/or propertyNames is null
     */
    public static List<String> getActualPropertyName(final SimpleFeatureType featureType,
            final List<String> propertyNames) {

        if (featureType == null) {
            throw new IllegalArgumentException("featureType cannot be null");
        }
        if (propertyNames == null) {
            throw new IllegalArgumentException("propertyName cannot be null");
        }

        List<String> nameList = new LinkedList<String>();

        for (String property : propertyNames) {
            String name = getActualPropertyName(featureType, property);
            if (name != null) {
                nameList.add(name);
            }
        }
        return nameList;
    }

   public static Set<Identifier> stringToId(FilterFactory fac, String fid) {
	   return stringToId(fac, Collections.singleton(fid));
	}

   public static Set<Identifier> stringToId(FilterFactory fac, Collection<String> fid) {
		Set<Identifier> ids = new HashSet<Identifier>();
		for (String string : fid) {
			ids.add( fac.featureId(string));
		}
		return ids;
	}

/**
 * Convert a string to a Id filter
 *
 * @param fid the feature id
 * @return
 */
@SuppressWarnings("unchecked")
public static Filter id(String fid) {
    FilterFactory factory = CommonFactoryFinder.getFilterFactory2(GeoTools
            .getDefaultHints());
    Set<Identifier> id = stringToId(factory , fid);
    Id filter = factory.id(id);
    return filter;
}

}
