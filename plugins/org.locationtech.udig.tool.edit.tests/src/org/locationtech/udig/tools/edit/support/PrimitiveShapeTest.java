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
package org.locationtech.udig.tools.edit.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.PackedCoordinateSequenceFactory;

@SuppressWarnings("nls")
public class PrimitiveShapeTest {

    private TestHandler handler;
    private EditBlackboard bb;
    private EditGeom geom;
    private PrimitiveShape shell;

    @Before
    public void setUp() throws Exception {
        handler = new TestHandler();
        bb = handler.getEditBlackboard();
        geom = bb.newGeom("newFeature", ShapeType.LINE);

        shell = geom.getShell();
        bb.addPoint(10, 10, shell);
        bb.addPoint(11, 10, shell);
        bb.addPoint(10, 10, shell);
        bb.addPoint(2, 20, shell);
        bb.addPoint(2, 20, shell);
        bb.addPoint(4000, 8000, shell);
        bb.addPoint(4000, 8000, shell);
    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.PrimitiveShape.getNumPoints()'
     */
    @Test
    public void testGetNumPoints() {
        assertEquals(5, shell.getNumPoints());
    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.PrimitiveShape.getPoint(int)'
     */
    @Test
    public void testGetPoint() {
        assertEquals(Point.valueOf(10, 10), shell.getPoint(0));
        assertEquals(Point.valueOf(11, 10), shell.getPoint(1));
        assertEquals(Point.valueOf(10, 10), shell.getPoint(2));
        assertEquals(Point.valueOf(2, 20), shell.getPoint(3));
        assertEquals(Point.valueOf(4000, 8000), shell.getPoint(4));
    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.PrimitiveShape.getNumCoords()'
     */
    @Test
    public void testGetNumCoords() {
        assertEquals(7, shell.getNumCoords());
    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.PrimitiveShape.getCoord(int)'
     */
    @Test
    public void testGetCoord() {
        assertEquals(bb.toCoord(Point.valueOf(10, 10)), shell.getCoord(0));
        assertEquals(bb.toCoord(Point.valueOf(11, 10)), shell.getCoord(1));
        assertEquals(bb.toCoord(Point.valueOf(10, 10)), shell.getCoord(2));
        assertEquals(bb.toCoord(Point.valueOf(2, 20)), shell.getCoord(3));
        assertEquals(bb.toCoord(Point.valueOf(2, 20)), shell.getCoord(4));
        assertEquals(bb.toCoord(Point.valueOf(4000, 8000)), shell.getCoord(5));
        assertEquals(bb.toCoord(Point.valueOf(4000, 8000)), shell.getCoord(6));
    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.PrimitiveShape.iterator()'
     */
    @Test
    public void testIterator() {
        Iterator<Point> iter = shell.iterator();
        assertTrue(iter.hasNext());
        assertEquals(Point.valueOf(10, 10), iter.next());
        assertEquals(Point.valueOf(11, 10), iter.next());
        assertEquals(Point.valueOf(10, 10), iter.next());
        assertEquals(Point.valueOf(2, 20), iter.next());
        assertEquals(Point.valueOf(4000, 8000), iter.next());
    }

    @Test
    public void testRemovePoint() throws Exception {
        bb.removeCoordsAtPoint(10, 10);

        assertEquals(Point.valueOf(11, 10), shell.getPoint(0));
        assertEquals(Point.valueOf(2, 20), shell.getPoint(1));
        assertEquals(Point.valueOf(4000, 8000), shell.getPoint(2));
    }

    @Test
    public void testRemoveCoordinate() throws Exception {

        bb.removeCoordinate(4, shell.getCoord(6), shell);
        assertEquals(bb.toCoord(Point.valueOf(10, 10)), shell.getCoord(0));
        assertEquals(bb.toCoord(Point.valueOf(11, 10)), shell.getCoord(1));
        assertEquals(bb.toCoord(Point.valueOf(10, 10)), shell.getCoord(2));
        assertEquals(bb.toCoord(Point.valueOf(2, 20)), shell.getCoord(3));
        assertEquals(bb.toCoord(Point.valueOf(2, 20)), shell.getCoord(4));
        assertEquals(bb.toCoord(Point.valueOf(4000, 8000)), shell.getCoord(5));
    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.PrimitiveShape.coordIterator()'
     */
    @Test
    public void testCoordIterator() {

        Iterator<Coordinate> iter = shell.coordIterator();
        assertTrue(iter.hasNext());
        assertEquals(bb.toCoord(Point.valueOf(10, 10)), iter.next());
        assertEquals(bb.toCoord(Point.valueOf(11, 10)), iter.next());
        assertEquals(bb.toCoord(Point.valueOf(10, 10)), iter.next());
        assertEquals(bb.toCoord(Point.valueOf(2, 20)), iter.next());
        assertEquals(bb.toCoord(Point.valueOf(2, 20)), iter.next());
        assertEquals(bb.toCoord(Point.valueOf(4000, 8000)), iter.next());
        assertEquals(bb.toCoord(Point.valueOf(4000, 8000)), iter.next());
    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.PrimitiveShape.coordArray()'
     */
    @Test
    public void testCoordArray() {
        Coordinate[] array = shell.coordArray();
        assertEquals(bb.toCoord(Point.valueOf(10, 10)), array[0]);
        assertEquals(bb.toCoord(Point.valueOf(11, 10)), array[1]);
        assertEquals(bb.toCoord(Point.valueOf(10, 10)), array[2]);
        assertEquals(bb.toCoord(Point.valueOf(2, 20)), array[3]);
        assertEquals(bb.toCoord(Point.valueOf(2, 20)), array[4]);
        assertEquals(bb.toCoord(Point.valueOf(4000, 8000)), array[5]);
        assertEquals(bb.toCoord(Point.valueOf(4000, 8000)), array[6]);
    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.PrimitiveShape.hasVertex(Point)'
     */
    @Test
    public void testHasVertexPoint() {
        assertTrue(shell.hasVertex(Point.valueOf(10, 10)));
        assertTrue(shell.hasVertex(Point.valueOf(11, 10)));
        assertTrue(shell.hasVertex(Point.valueOf(2, 20)));
        assertTrue(shell.hasVertex(Point.valueOf(4000, 8000)));
        assertFalse(shell.hasVertex(Point.valueOf(40, 40)));
    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.PrimitiveShape.hasVertex(Point,
     * LazyCoord)'
     */
    @Test
    public void testHasVertexPointLazyCoord() {

    }

    /*
     * Test method for
     * 'org.locationtech.udig.tools.edit.support.PrimitiveShape.getClosestEdge(Point)'
     */
    @Test
    public void testGetClosestEdge() {
        bb = new TestEditBlackboard();
        geom = bb.newGeom("id", ShapeType.POLYGON);
        bb.addPoint(10, 10, geom.getShell());
        bb.addPoint(20, 10, geom.getShell());
        bb.addPoint(20, 20, geom.getShell());
        bb.addPoint(10, 20, geom.getShell());
        bb.addPoint(10, 10, geom.getShell());

        // test getClosest for each edge
        assertCorrectEdge(Point.valueOf(12, 9), Point.valueOf(12, 10), 0);
        assertCorrectEdge(Point.valueOf(21, 12), Point.valueOf(20, 12), 1);
        assertCorrectEdge(Point.valueOf(18, 21), Point.valueOf(18, 20), 2);
        assertCorrectEdge(Point.valueOf(9, 18), Point.valueOf(10, 18), 3);
    }

    private void assertCorrectEdge( Point referencePoint, Point pointOnEdge,
            int expectedPreviousPoint ) {
        ClosestEdge edge = geom.getShell().getClosestEdge(referencePoint, true);
        assertEquals(1, (int) edge.getDistanceToEdge());
        assertEquals(expectedPreviousPoint, edge.getIndexOfPrevious());
        assertEquals(pointOnEdge, edge.getPointOnLine());
        assertEquals(new Coordinate(pointOnEdge.getX() + .5, pointOnEdge.getY() + 0.5), edge
                .getAddedCoord());
        assertEquals(geom, edge.getGeom());
        assertEquals(geom.getShell(), edge.getPart());
    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.PrimitiveShape.getBounds()'
     */
    @Test
    public void testGetBounds() {

    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.PrimitiveShape.contains(Point,
     * boolean)'
     */
    @Test
    public void testContainsPointBoolean() {

    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.PrimitiveShape.getBounds2D()'
     */
    @Test
    public void testGetBounds2D() {

    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.PrimitiveShape.contains(double,
     * double)'
     */
    @Test
    public void testContainsDoubleDouble() {

    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.PrimitiveShape.contains(Point2D)'
     */
    @Test
    public void testContainsPoint2D() {

    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.PrimitiveShape.intersects(double,
     * double, double, double)'
     */
    @Test
    public void testIntersectsDoubleDoubleDoubleDouble() {

    }

    /*
     * Test method for
     * 'org.locationtech.udig.tools.edit.support.PrimitiveShape.intersects(Rectangle2D)'
     */
    @Test
    public void testIntersectsRectangle2D() {

    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.PrimitiveShape.contains(double,
     * double, double, double)'
     */
    @Test
    public void testContainsDoubleDoubleDoubleDouble() {

    }

    /*
     * Test method for
     * 'org.locationtech.udig.tools.edit.support.PrimitiveShape.contains(Rectangle2D)'
     */
    @Test
    public void testContainsRectangle2D() {

    }

    /*
     * Test method for
     * 'org.locationtech.udig.tools.edit.support.PrimitiveShape.getPathIterator(AffineTransform)'
     */
    @Test
    public void testGetPathIteratorAffineTransform() {

    }

    /*
     * Test method for
     * 'org.locationtech.udig.tools.edit.support.PrimitiveShape.getPathIterator(AffineTransform,
     * double)'
     */
    @Test
    public void testGetPathIteratorAffineTransformDouble() {

    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.PrimitiveShape.getCoordsAt(int)'
     */
    @Test
    public void testGetCoordsAt() {

    }

    @Test
    public void testIntersectsPrimitiveShape() throws Exception {
        // Test point - point
        EditGeom geom1 = bb.newGeom("shp1", ShapeType.POINT);
        bb.addPoint(10, 10, geom1.getShell());

        EditGeom geom2 = bb.newGeom("shp2", ShapeType.POINT);
        bb.addPoint(10, 11, geom2.getShell());
        assertFalse(geom1.getShell().overlap(geom2.getShell(), false, true));
        assertFalse(geom2.getShell().overlap(geom1.getShell(), false, true));

        geom2 = bb.newGeom("shp2", ShapeType.POINT);
        bb.addPoint(10, 10, geom2.getShell());
        assertTrue(geom1.getShell().overlap(geom2.getShell(), false, true));
        assertTrue(geom2.getShell().overlap(geom1.getShell(), false, true));

        // test line-point
        geom1 = bb.newGeom("shp1", ShapeType.LINE);
        bb.addPoint(10, 10, geom1.getShell());
        bb.addPoint(20, 10, geom1.getShell());

        geom2 = bb.newGeom("shp2", ShapeType.POINT);
        bb.addPoint(10, 11, geom2.getShell());
        assertFalse(geom1.getShell().overlap(geom2.getShell(), false, true));
        assertFalse(geom2.getShell().overlap(geom1.getShell(), false, true));

        geom2 = bb.newGeom("shp2", ShapeType.POINT);
        bb.addPoint(15, 10, geom2.getShell());
        assertTrue(geom1.getShell().overlap(geom2.getShell(), false, true));
        assertTrue(geom2.getShell().overlap(geom1.getShell(), false, true));
        assertFalse(geom1.getShell().overlap(geom2.getShell(), false, false));
        assertFalse(geom2.getShell().overlap(geom1.getShell(), false, false));

        // test line-line
        geom1 = bb.newGeom("shp1", ShapeType.LINE);
        bb.addPoint(10, 10, geom1.getShell());
        bb.addPoint(20, 10, geom1.getShell());

        geom2 = bb.newGeom("shp2", ShapeType.LINE);
        bb.addPoint(10, 11, geom2.getShell());
        bb.addPoint(20, 11, geom2.getShell());
        assertFalse(geom1.getShell().overlap(geom2.getShell(), false, false));
        assertFalse(geom2.getShell().overlap(geom1.getShell(), false, false));

        geom2 = bb.newGeom("shp2", ShapeType.LINE);
        bb.addPoint(15, 5, geom2.getShell());
        bb.addPoint(15, 15, geom2.getShell());
        assertTrue(geom1.getShell().overlap(geom2.getShell(), false, false));
        assertTrue(geom2.getShell().overlap(geom1.getShell(), false, false));

        geom2 = bb.newGeom("shp2", ShapeType.LINE);
        bb.addPoint(15, 10, geom2.getShell());
        bb.addPoint(15, 15, geom2.getShell());
        assertTrue(geom1.getShell().overlap(geom2.getShell(), false, true));
        assertTrue(geom2.getShell().overlap(geom1.getShell(), false, true));
        assertFalse(geom1.getShell().overlap(geom2.getShell(), false, false));
        assertFalse(geom2.getShell().overlap(geom1.getShell(), false, false));

        // test polygon-line
        geom1 = bb.newGeom("shp1", ShapeType.POLYGON);
        bb.addPoint(0, 0, geom1.getShell());
        bb.addPoint(10, 0, geom1.getShell());
        bb.addPoint(10, 10, geom1.getShell());
        bb.addPoint(0, 10, geom1.getShell());
        bb.addPoint(0, 0, geom1.getShell());

        geom2 = bb.newGeom("shp2", ShapeType.LINE);
        bb.addPoint(10, 11, geom2.getShell());
        bb.addPoint(20, 11, geom2.getShell());
        assertFalse(geom1.getShell().overlap(geom2.getShell(), false, false));
        assertFalse(geom2.getShell().overlap(geom1.getShell(), false, false));

        geom2 = bb.newGeom("shp2", ShapeType.LINE);
        bb.addPoint(5, 5, geom2.getShell());
        bb.addPoint(5, 15, geom2.getShell());
        assertTrue(geom1.getShell().overlap(geom2.getShell(), false, false));
        assertTrue(geom2.getShell().overlap(geom1.getShell(), false, false));

        geom2 = bb.newGeom("shp2", ShapeType.LINE);
        bb.addPoint(5, 2, geom2.getShell());
        bb.addPoint(5, 8, geom2.getShell());
        assertTrue(geom1.getShell().overlap(geom2.getShell(), false, false));
        assertTrue(geom2.getShell().overlap(geom1.getShell(), false, false));

        // test polygon-polygon
        geom1 = bb.newGeom("shp1", ShapeType.POLYGON);
        bb.addPoint(0, 0, geom1.getShell());
        bb.addPoint(10, 0, geom1.getShell());
        bb.addPoint(10, 10, geom1.getShell());
        bb.addPoint(0, 10, geom1.getShell());
        bb.addPoint(0, 0, geom1.getShell());

        // no intersection
        geom2 = bb.newGeom("shp2", ShapeType.POLYGON);
        bb.addPoint(0, 11, geom2.getShell());
        bb.addPoint(10, 11, geom2.getShell());
        bb.addPoint(10, 21, geom2.getShell());
        bb.addPoint(0, 21, geom2.getShell());
        bb.addPoint(0, 11, geom2.getShell());
        assertFalse(geom1.getShell().overlap(geom2.getShell(), false, false));
        assertFalse(geom2.getShell().overlap(geom1.getShell(), false, false));

        //   ---------
        // ------    |
        // | |  |    |
        // ------    |
        //   ---------
        geom2 = bb.newGeom("shp2", ShapeType.POLYGON);
        bb.addPoint(-5, 2, geom2.getShell());
        bb.addPoint(5, 2, geom2.getShell());
        bb.addPoint(5, 8, geom2.getShell());
        bb.addPoint(-5, 8, geom2.getShell());
        bb.addPoint(-5, 2, geom2.getShell());
        assertTrue(geom1.getShell().overlap(geom2.getShell(), false, false));
        assertTrue(geom2.getShell().overlap(geom1.getShell(), false, false));

        //    ---------
        // ----------------
        // |  |       |   |
        // ----------------
        //    ---------
        geom2 = bb.newGeom("shp2", ShapeType.POLYGON);
        bb.addPoint(-5, 2, geom2.getShell());
        bb.addPoint(15, 2, geom2.getShell());
        bb.addPoint(15, 8, geom2.getShell());
        bb.addPoint(-5, 8, geom2.getShell());
        bb.addPoint(-5, 2, geom2.getShell());
        assertTrue(geom1.getShell().overlap(geom2.getShell(), false, false));
        assertTrue(geom2.getShell().overlap(geom1.getShell(), false, false));

        //     ---------
        // ----        |
        // |  |        |
        // ----        |
        //     ---------
        geom2 = bb.newGeom("shp2", ShapeType.POLYGON);
        bb.addPoint(-5, 2, geom2.getShell());
        bb.addPoint(0, 2, geom2.getShell());
        bb.addPoint(0, 8, geom2.getShell());
        bb.addPoint(-5, 8, geom2.getShell());
        bb.addPoint(-5, 2, geom2.getShell());
        assertFalse(geom1.getShell().overlap(geom2.getShell(), false, false));
        assertFalse(geom2.getShell().overlap(geom1.getShell(), false, false));
        assertTrue(geom1.getShell().overlap(geom2.getShell(), false, true));
        assertTrue(geom2.getShell().overlap(geom1.getShell(), false, true));

        // ---------
        // ---     |
        // | |     |
        // ---     |
        // ---------
        geom2 = bb.newGeom("shp2", ShapeType.POLYGON);
        bb.addPoint(0, 2, geom2.getShell());
        bb.addPoint(5, 2, geom2.getShell());
        bb.addPoint(5, 8, geom2.getShell());
        bb.addPoint(0, 8, geom2.getShell());
        bb.addPoint(0, 2, geom2.getShell());
        assertTrue(geom1.getShell().overlap(geom2.getShell(), false, false));
        assertTrue(geom2.getShell().overlap(geom1.getShell(), false, false));
        assertTrue(geom1.getShell().overlap(geom2.getShell(), false, true));
        assertTrue(geom2.getShell().overlap(geom1.getShell(), false, true));

        // ---------
        // | ---   |
        // | | |   |
        // | ---   |
        // ---------
        geom2 = bb.newGeom("shp2", ShapeType.POLYGON);
        bb.addPoint(2, 2, geom2.getShell());
        bb.addPoint(8, 2, geom2.getShell());
        bb.addPoint(8, 8, geom2.getShell());
        bb.addPoint(2, 8, geom2.getShell());
        bb.addPoint(2, 2, geom2.getShell());
        assertTrue(geom1.getShell().overlap(geom2.getShell(), false, false));
        assertTrue(geom2.getShell().overlap(geom1.getShell(), false, false));

    }
    
    @Test
    public void testMovePointOnGeometry() throws Exception {
    	GeometryFactory fac = new GeometryFactory();
    	
    	double[] coords = new double[]{10,10, 10,20, 10,30, 30,30, 20,30, 10,10};
		LinearRing ring = fac.createLinearRing(new PackedCoordinateSequenceFactory().create(coords , 2));
    	Polygon poly = fac.createPolygon(ring, new LinearRing[0]);
    	
    	bb.clear();
    	geom =  bb.addGeometry(poly, "poly").values().iterator().next();
    	
    	bb.moveCoords(10, 10, 0, 10);
    	
    	
	}

}
