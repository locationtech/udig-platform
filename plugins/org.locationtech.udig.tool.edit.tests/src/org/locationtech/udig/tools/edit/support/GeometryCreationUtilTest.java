/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.support;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.locationtech.udig.tools.edit.support.GeometryCreationUtil.Bag;

import org.geotools.feature.AttributeTypeBuilder;
import org.junit.Test;
import org.opengis.feature.type.GeometryDescriptor;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 * 
 * @author jones
 * @since 1.1.0
 */
@SuppressWarnings("nls")
public class GeometryCreationUtilTest {
    
    @Test
    public void testCreateBasedOnGeomToCreate() throws Exception {
        TestEditBlackboard bb=new TestEditBlackboard();
        String fid="FeatureID";
        EditGeom geom = bb.newGeom(fid, null);
        bb.addPoint(10,10,geom.getShell());
        EditGeom geom2 = bb.newGeom(fid, null);
        AttributeTypeBuilder builder = new AttributeTypeBuilder();
        builder.setBinding(Geometry.class);
        builder.setName("geom");
        GeometryDescriptor at = (GeometryDescriptor) builder.buildDescriptor("geom", builder.buildGeometryType());
        Map<String, Bag> result = GeometryCreationUtil.createAllGeoms(geom, Point.class, at, false);
        
        assertEquals(1, result.get(fid).jts.size());

        bb.addPoint(100,100, geom2.getShell());
        result = GeometryCreationUtil.createAllGeoms(geom, Point.class, at, false);
        
        assertEquals(1, result.size());
        assertEquals(2, result.get(fid).jts.size());
        assertEquals(Point.class, result.get(fid).jts.get(0).getClass());
        assertEquals(Point.class, result.get(fid).jts.get(1).getClass());
        
        String fid2="FID2";
        EditGeom differentGeom=bb.newGeom(fid2, null);
        bb.addPoint(200,200, differentGeom.getShell());
        
        result = GeometryCreationUtil.createAllGeoms(geom, Point.class, at, false);
        assertEquals(2, result.size());
        assertEquals(2, result.get(fid).jts.size());
        assertEquals(Point.class, result.get(fid).jts.get(0).getClass());
        assertEquals(Point.class, result.get(fid).jts.get(1).getClass());
        assertEquals(1, result.get(fid2).jts.size());
        assertEquals(Point.class, result.get(fid2).jts.get(0).getClass());
        
        result = GeometryCreationUtil.createAllGeoms(geom, LineString.class, at, false);
        assertEquals(2, result.size());
        assertEquals(2, result.get(fid).jts.size());
        assertEquals(LineString.class, result.get(fid).jts.get(0).getClass());
        assertEquals(LineString.class, result.get(fid).jts.get(1).getClass());
        assertEquals(1, result.get(fid2).jts.size());
        assertEquals(Point.class, result.get(fid2).jts.get(0).getClass());
        
        result = GeometryCreationUtil.createAllGeoms(geom, Polygon.class, at, false);
        assertEquals(2, result.size());
        assertEquals(2, result.get(fid).jts.size());
        assertEquals(Polygon.class, result.get(fid).jts.get(0).getClass());
        assertEquals(Polygon.class, result.get(fid).jts.get(1).getClass());
        assertEquals(1, result.get(fid2).jts.size());
        assertEquals(Point.class, result.get(fid2).jts.get(0).getClass());
        
        geom.getFeatureIDRef().set(null);
        geom2.getFeatureIDRef().set(null);

        result = GeometryCreationUtil.createAllGeoms(geom, Polygon.class, at, false);
        assertEquals(2, result.size());
        assertEquals(2, result.get(null).jts.size());
        assertEquals(Polygon.class, result.get(null).jts.get(0).getClass());
        assertEquals(Polygon.class, result.get(null).jts.get(1).getClass());
        assertEquals(1, result.get(fid2).jts.size());
        assertEquals(Point.class, result.get(fid2).jts.get(0).getClass());
        
    }
    
    @Test
    public void testCreateBasedOnShapeType() throws Exception {
        TestEditBlackboard bb=new TestEditBlackboard();
        String fid="FeatureID";
        EditGeom geom = bb.newGeom(fid, null);
        bb.addPoint(10,10,geom.getShell());
        EditGeom geom2 = bb.newGeom(fid, null);
        AttributeTypeBuilder builder = new AttributeTypeBuilder();
        builder.setBinding(Geometry.class);
        builder.setName("geom");
        GeometryDescriptor at = builder.buildDescriptor("geom", builder.buildGeometryType());
        bb.addPoint(100,100, geom2.getShell());
        
        String fid2="FID2";
        EditGeom differentGeom=bb.newGeom(fid2, null);
        bb.addPoint(200,200, differentGeom.getShell());
        
        
        Map<String, Bag> result = GeometryCreationUtil.createAllGeoms(geom, Point.class, at, false);
        assertEquals(1, result.get(fid2).jts.size());
        assertEquals(Point.class, result.get(fid2).jts.get(0).getClass());

        differentGeom.setShapeType(ShapeType.LINE);
        result = GeometryCreationUtil.createAllGeoms(geom, Point.class, at, false);
        assertEquals(1, result.get(fid2).jts.size());
        assertEquals(LineString.class, result.get(fid2).jts.get(0).getClass());

        differentGeom.setShapeType(ShapeType.POLYGON);
        result = GeometryCreationUtil.createAllGeoms(geom, Point.class, at, false);
        assertEquals(1, result.get(fid2).jts.size());
        assertEquals(Polygon.class, result.get(fid2).jts.get(0).getClass());

        differentGeom.setShapeType(ShapeType.UNKNOWN);
        result = GeometryCreationUtil.createAllGeoms(geom, Point.class, at, false);
        assertEquals(1, result.get(fid2).jts.size());
        assertEquals(Point.class, result.get(fid2).jts.get(0).getClass());
        
        bb.addPoint(200,200, differentGeom.getShell());

        result = GeometryCreationUtil.createAllGeoms(geom, Point.class, at, false);
        assertEquals(1, result.get(fid2).jts.size());
        assertEquals(Point.class, result.get(fid2).jts.get(0).getClass());

        bb.addPoint(200,210, differentGeom.getShell());

        result = GeometryCreationUtil.createAllGeoms(geom, Point.class, at, false);
        assertEquals(1, result.get(fid2).jts.size());
        assertEquals(LineString.class, result.get(fid2).jts.get(0).getClass());

        bb.addPoint(200,200, differentGeom.getShell());

        result = GeometryCreationUtil.createAllGeoms(geom, Point.class, at, false);
        assertEquals(1, result.get(fid2).jts.size());
        assertEquals(Polygon.class, result.get(fid2).jts.get(0).getClass());

    }
}
