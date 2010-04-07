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
package net.refractions.udig.tools.edit.support;

import java.util.Map;

import junit.framework.TestCase;
import net.refractions.udig.tools.edit.support.GeometryCreationUtil.Bag;

import org.geotools.feature.AttributeTypeBuilder;
import org.opengis.feature.type.GeometryDescriptor;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * 
 * @author jones
 * @since 1.1.0
 */
public class GeometryCreationUtilTest extends TestCase {
    public void testCreateBasedOnGeomToCreate() throws Exception {
        TestEditBlackboard bb=new TestEditBlackboard();
        String fid="FeatureID"; //$NON-NLS-1$
        EditGeom geom = bb.newGeom(fid, null);
        bb.addPoint(10,10,geom.getShell());
        EditGeom geom2 = bb.newGeom(fid, null);
        AttributeTypeBuilder builder = new AttributeTypeBuilder();
        builder.setBinding(Geometry.class);
        builder.setName("geom");
        GeometryDescriptor at = (GeometryDescriptor) builder.buildDescriptor("geom", builder.buildGeometryType()); //$NON-NLS-1$
        Map<String, Bag> result = GeometryCreationUtil.createAllGeoms(geom, Point.class, at, false);
        
        assertEquals(1, result.get(fid).jts.size());

        bb.addPoint(100,100, geom2.getShell());
        result = GeometryCreationUtil.createAllGeoms(geom, Point.class, at, false);
        
        assertEquals(1, result.size());
        assertEquals(2, result.get(fid).jts.size());
        assertEquals(Point.class, result.get(fid).jts.get(0).getClass());
        assertEquals(Point.class, result.get(fid).jts.get(1).getClass());
        
        String fid2="FID2"; //$NON-NLS-1$
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
    
    public void testCreateBasedOnShapeType() throws Exception {
        TestEditBlackboard bb=new TestEditBlackboard();
        String fid="FeatureID"; //$NON-NLS-1$
        EditGeom geom = bb.newGeom(fid, null);
        bb.addPoint(10,10,geom.getShell());
        EditGeom geom2 = bb.newGeom(fid, null);
        AttributeTypeBuilder builder = new AttributeTypeBuilder();
        builder.setBinding(Geometry.class);
        builder.setName("geom");
        GeometryDescriptor at = builder.buildDescriptor("geom", builder.buildGeometryType()); //$NON-NLS-1$
        bb.addPoint(100,100, geom2.getShell());
        
        String fid2="FID2"; //$NON-NLS-1$
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
