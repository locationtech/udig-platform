/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.behaviour;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.EventType;
import org.locationtech.udig.tools.edit.behaviour.accept.AcceptChangesBehaviour;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.tools.edit.support.Point;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;
import org.locationtech.udig.tools.edit.support.TestEditBlackboard;
import org.locationtech.udig.tools.edit.support.TestHandler;

import org.junit.Before;
import org.junit.Test;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

/**
 * Test MoveGeometryBehaviour.
 * 
 * @author jones
 * @since 1.1.0
 */
public class MoveGeometryBehaviourTest {

    private MoveGeometryBehaviour moveGeometryBehaviour;
    private TestHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new TestHandler();
        moveGeometryBehaviour = new MoveGeometryBehaviour();
        handler.getBehaviours().add(moveGeometryBehaviour);
        PrimitiveShape shell = handler.getEditBlackboard().getGeoms().get(0).getShell();
        handler.getEditBlackboard().addPoint(0, 0, shell);
        handler.getEditBlackboard().addPoint(50, 0, shell);
        handler.getEditBlackboard().addPoint(50, 50, shell);
        handler.getEditBlackboard().addPoint(0, 50, shell);
        handler.getEditBlackboard().addPoint(0, 0, shell);

        handler.setCurrentShape(shell);
    }

    /*
     * Test method for
     * 'MoveGeometryBehaviour.isValid(EditToolHandler,
     * MapMouseEvent, EventType)'
     */
    @Test
    public void testIsValid() {
        MapMouseEvent event = new MapMouseEvent(null, 100, 100, MapMouseEvent.ALT_DOWN_MASK
                | MapMouseEvent.CTRL_DOWN_MASK, MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        assertFalse(moveGeometryBehaviour.isValid(handler, event, EventType.DRAGGED));

        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.ALT_DOWN_MASK
                | MapMouseEvent.CTRL_DOWN_MASK, MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        assertTrue(moveGeometryBehaviour.isValid(handler, event, EventType.DRAGGED));

        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.ALT_DOWN_MASK
                | MapMouseEvent.CTRL_DOWN_MASK, MapMouseEvent.BUTTON1 | MapMouseEvent.BUTTON2,
                MapMouseEvent.BUTTON1);
        assertFalse(moveGeometryBehaviour.isValid(handler, event, EventType.DRAGGED));

        handler.getEditBlackboard().selectionAdd(Point.valueOf(0, 0));
        handler.getEditBlackboard().selectionAdd(Point.valueOf(50, 0));
        handler.getEditBlackboard().selectionAdd(Point.valueOf(50, 50));
        handler.getEditBlackboard().selectionAdd(Point.valueOf(0, 50));

        // now all vertices are selected so if the mouse is within the geom or on a
        // vertex the behaviour should be valid.
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.ALT_DOWN_MASK
                | MapMouseEvent.CTRL_DOWN_MASK, MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        assertTrue(moveGeometryBehaviour.isValid(handler, event, EventType.DRAGGED));

        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.NONE, MapMouseEvent.BUTTON1,
                MapMouseEvent.BUTTON1);

        handler.getMouseTracker().setDragStarted(Point.valueOf(-10, 10));
        assertFalse(moveGeometryBehaviour.isValid(handler, event, EventType.DRAGGED));

        handler.getMouseTracker().setDragStarted(Point.valueOf(10, 10));
        assertTrue(moveGeometryBehaviour.isValid(handler, event, EventType.DRAGGED));

    }

    /*
     * Test method for
     * 'org.locationtech.udig.tools.edit.behaviour.MoveVertexBehaviour.getCommand(EditToolHandler,
     * MapMouseEvent, EventType)'
     */
    @Test
    public void testGetCommand() {

        EditGeom geom = handler.getCurrentGeom();
        PrimitiveShape hole = geom.newHole();
        EditBlackboard bb = handler.getEditBlackboard();

        bb.addPoint(5, 5, hole);
        bb.addPoint(25, 5, hole);
        bb.addPoint(25, 25, hole);
        bb.addPoint(5, 25, hole);
        bb.addPoint(5, 5, hole);

        handler.getMouseTracker().setDragStarted(Point.valueOf(10, 10));
        MapMouseEvent event = new MapMouseEvent(null, 20, 10, MapMouseEvent.ALT_DOWN_MASK
                | MapMouseEvent.CTRL_DOWN_MASK, MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);

        assertEquals(2, handler.getEditBlackboard().getCoords(10, 0).size());
        assertEquals(1, handler.getEditBlackboard().getCoords(60, 0).size());
        assertEquals(1, handler.getEditBlackboard().getCoords(60, 50).size());
        assertEquals(1, handler.getEditBlackboard().getCoords(10, 50).size());
        assertEquals(2, handler.getEditBlackboard().getCoords(15, 5).size());
        assertEquals(1, handler.getEditBlackboard().getCoords(35, 5).size());
        assertEquals(1, handler.getEditBlackboard().getCoords(35, 25).size());
        assertEquals(1, handler.getEditBlackboard().getCoords(15, 25).size());

        assertEquals(0, handler.getEditBlackboard().getCoords(0, 0).size());
        assertEquals(0, handler.getEditBlackboard().getCoords(50, 0).size());
        assertEquals(0, handler.getEditBlackboard().getCoords(50, 50).size());
        assertEquals(0, handler.getEditBlackboard().getCoords(0, 50).size());
        assertEquals(0, handler.getEditBlackboard().getCoords(5, 5).size());
        assertEquals(0, handler.getEditBlackboard().getCoords(25, 5).size());
        assertEquals(0, handler.getEditBlackboard().getCoords(25, 25).size());
        assertEquals(0, handler.getEditBlackboard().getCoords(5, 25).size());

        PrimitiveShape shell = handler.getCurrentShape();

        assertEquals(Point.valueOf(10, 0), shell.getPoint(0));
        assertEquals(Point.valueOf(60, 0), shell.getPoint(1));
        assertEquals(Point.valueOf(60, 50), shell.getPoint(2));
        assertEquals(Point.valueOf(10, 50), shell.getPoint(3));
        assertEquals(Point.valueOf(10, 0), shell.getPoint(4));
        assertEquals(Point.valueOf(15, 5), hole.getPoint(0));
        assertEquals(Point.valueOf(35, 5), hole.getPoint(1));
        assertEquals(Point.valueOf(35, 25), hole.getPoint(2));
        assertEquals(Point.valueOf(15, 25), hole.getPoint(3));
        assertEquals(Point.valueOf(15, 5), hole.getPoint(4));
    }

    @Test
    public void testMoveDecimatedGeometry() throws Exception {
        Coordinate[] coords = new Coordinate[]{new Coordinate(-128.6898, 59.0493),
                new Coordinate(-128.6894, 59.0502), new Coordinate(-128.6892, 59.052),
                new Coordinate(-128.6883, 59.0525), new Coordinate(-128.6865, 59.0526),
                new Coordinate(-128.6846, 59.0535), new Coordinate(-128.6846, 59.0538),
                new Coordinate(-128.6833, 59.0548), new Coordinate(-128.682, 59.0565),
                new Coordinate(-128.6811, 59.0559), new Coordinate(-128.6817, 59.0538),
                new Coordinate(-128.683, 59.0526), new Coordinate(-128.6837, 59.052),
                new Coordinate(-128.685, 59.0512), new Coordinate(-128.6863, 59.0512),
                new Coordinate(-128.6889, 59.0493), new Coordinate(-128.6898, 59.0493)};
        GeometryFactory fac=new GeometryFactory();
        Polygon lake = fac.createPolygon(fac.createLinearRing(coords), new LinearRing[0]);
        Envelope env = lake.getEnvelopeInternal();
        
        ViewportModel model = ((ViewportModel) handler.getContext().getMap().getViewportModel());
        model.setBounds(env);
        model.setWidth(env.getWidth()*200);
        
        handler.setEditBlackboard(new EditBlackboard(10, 10, model.worldToScreenTransform(),
                TestEditBlackboard.IDENTITY));

        EditBlackboard editBlackboard = handler.getEditBlackboard();
        editBlackboard.setGeometries(lake, "lake"); //$NON-NLS-1$
        
        handler.getAcceptBehaviours().add(new AcceptChangesBehaviour(Polygon.class, false));
        Point[] points = new Point[coords.length];
        for( int i = 0; i < coords.length; i++ ) {
            points[i] = editBlackboard.toPoint(coords[i]);
        }
        
        for( int i = 0; i < points.length; i++ ) {
            List<Coordinate> list = editBlackboard.getCoords(points[i].getX(), points[i].getY());
            System.out.println(list.size());
            assertTrue(!list.isEmpty());
        }
        
        System.out.println("Done first check"); //$NON-NLS-1$
        
        handler.getMouseTracker().setDragStarted(points[0]);
        PrimitiveShape shell = editBlackboard.getGeoms().get(0).getShell();
        handler.setCurrentShape(shell);

        Coordinate[] shellCoords=new Coordinate[coords.length];
        
        for( int i = 0; i < shellCoords.length; i++ ) {
            shellCoords[i]=shell.getCoord(i);
        }
        
        MapMouseEvent event = new MapMouseEvent(handler.getContext().getMapDisplay(), points[0]
                .getX(), points[0].getY(), MapMouseEvent.ALT_DOWN_MASK
                | MapMouseEvent.CTRL_DOWN_MASK, MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);

        event = new MapMouseEvent(handler.getContext().getMapDisplay(), points[0]
                .getX() + 1, points[0].getY(), MapMouseEvent.ALT_DOWN_MASK
                | MapMouseEvent.CTRL_DOWN_MASK, MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);
        

        event = new MapMouseEvent(handler.getContext().getMapDisplay(), points[0]
                .getX() + 2, points[0].getY(), MapMouseEvent.ALT_DOWN_MASK
                | MapMouseEvent.CTRL_DOWN_MASK, MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);

        for( int i = 0; i < points.length; i++ ) {
            List<Coordinate> list = editBlackboard.getCoords(points[i].getX()+2, points[i].getY());
            System.out.println(list.size());
            assertTrue(!list.isEmpty());
        }

        for( int i = 0; i < points.length; i++ ) {
            List<EditGeom> list = editBlackboard.getGeoms(points[i].getX()+2, points[i].getY());
            System.out.println(list.size());
            assertTrue(!list.isEmpty()); 
        }
        
//        for( int i = 0; i < coords.length; i++ ) {
//            assertSame(  );
//        }
        
        Coordinate deltaPart1 = editBlackboard.toCoord(Point.valueOf(points[0].getX()+2, points[0].getY()));
        Coordinate deltaPart2 = editBlackboard.toCoord(points[0]);
        double deltaX = deltaPart1.x-deltaPart2.x;
        double deltaY = deltaPart1.y-deltaPart2.y;
        
        for( int i = 0; i < coords.length; i++ ) {
            assertEquals( new Coordinate( coords[i].x+deltaX, coords[i].y+deltaY ), 
                    shell.getCoord(i) );
        }
        
        
    }
}
