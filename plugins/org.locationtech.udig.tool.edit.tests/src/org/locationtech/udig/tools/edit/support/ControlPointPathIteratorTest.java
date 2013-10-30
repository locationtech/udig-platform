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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

import javax.swing.JFrame;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests the case where a polygon is just starting to be created.  There are only 2 lines
 * @author jeichar
 */
public class ControlPointPathIteratorTest {

    private static final boolean DO_SHOW = true;
    private TestEditBlackboard bb;
    private EditGeom editGeom;

    @Before
    public void setUp() throws Exception {
        bb=new TestEditBlackboard();
        editGeom = bb.newGeom("id", ShapeType.POLYGON); //$NON-NLS-1$
        
        bb.addPoint(10, 10, editGeom.getShell());
        bb.addPoint(30, 10, editGeom.getShell());

        PrimitiveShape hole = editGeom.newHole();
        
        bb.addPoint(10, 30, hole);
        bb.addPoint(30, 30, hole);

        hole=editGeom.newHole();
        
        bb.addPoint(10, 60, hole);
        bb.addPoint(30, 60, hole);

    }

    @Ignore
    @Test
    public void testDraw() throws Exception {
        ControlPointPathIterator pathIterator = new ControlPointPathIterator(editGeom, true, 5, 5);
        
        assertCorrectPath(pathIterator, false);

        show( new ControlPointPathIterator(editGeom, false, 5, 5) );
    }

    private void show( ControlPointPathIterator iter ) throws Exception {
        if( DO_SHOW ){
            final GeneralPath shape = new GeneralPath();
            shape.append(iter, false);
            JFrame frame=new JFrame(){
                /** long serialVersionUID field */
                private static final long serialVersionUID = 1L;

                @Override
                public void paint( Graphics g ) {
                    Graphics2D g2=(Graphics2D) g;
                    g2.fill(shape);
                }
            };
            
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            frame.setSize(400, 400);
            frame.setVisible(true);
            Thread.sleep(50000);
        }
    }

    private void assertCorrectPath( ControlPointPathIterator pathIterator, boolean selection ) {
        assertPoint(pathIterator, Point.valueOf(10, 10), false);
        pathIterator.next();

        assertPoint(pathIterator, Point.valueOf(30, 10), false);
        pathIterator.next();

        assertPoint(pathIterator, Point.valueOf(10, 30), false);
        pathIterator.next();

        assertPoint(pathIterator, Point.valueOf(30, 30), false);
        pathIterator.next();

        assertPoint(pathIterator, Point.valueOf(10, 60), false);
        pathIterator.next();

        assertPoint(pathIterator, Point.valueOf(30, 60), true);
        pathIterator.next();
        assertTrue(pathIterator.isDone());
        
    }

    private void assertPoint( ControlPointPathIterator pathIterator, Point expectedPoint, boolean isLastPoint ) {
        assertFalse( pathIterator.isDone());
        double[] nextDoubles=new double[2];
        float[] nextFloats=new float[2];

        int result=pathIterator.currentSegment(nextDoubles);
        assertEquals(PathIterator.SEG_MOVETO, result);
        assertEquals( (double)expectedPoint.getX()-2, nextDoubles[0], 0);
        assertEquals( (double)expectedPoint.getY()-2, nextDoubles[1], 0);

        result=pathIterator.currentSegment(nextFloats);
        assertEquals(PathIterator.SEG_MOVETO, result);
        assertEquals( (float)expectedPoint.getX()-2, nextFloats[0], 0);
        assertEquals( (float)expectedPoint.getY()-2, nextFloats[1], 0);

        pathIterator.next();
        assertFalse( pathIterator.isDone());

        result=pathIterator.currentSegment(nextDoubles);
        assertEquals(PathIterator.SEG_LINETO, result);
        assertEquals( (double)expectedPoint.getX()+2, nextDoubles[0], 0);
        assertEquals( (double)expectedPoint.getY()-2, nextDoubles[1], 0);

        result=pathIterator.currentSegment(nextFloats);
        assertEquals(PathIterator.SEG_LINETO, result);
        assertEquals( (float)expectedPoint.getX()+2, nextFloats[0], 0);
        assertEquals( (float)expectedPoint.getY()-2, nextFloats[1], 0);

        pathIterator.next();
        assertFalse( pathIterator.isDone());

        result=pathIterator.currentSegment(nextDoubles);
        assertEquals(PathIterator.SEG_LINETO, result);
        assertEquals( (double)expectedPoint.getX()+2, nextDoubles[0], 0);
        assertEquals( (double)expectedPoint.getY()+2, nextDoubles[1], 0);

        result=pathIterator.currentSegment(nextFloats);
        assertEquals(PathIterator.SEG_LINETO, result);
        assertEquals( (float)expectedPoint.getX()+2, nextFloats[0], 0);
        assertEquals( (float)expectedPoint.getY()+2, nextFloats[1], 0);

        pathIterator.next();
        assertFalse( pathIterator.isDone());

        result=pathIterator.currentSegment(nextDoubles);
        assertEquals(PathIterator.SEG_LINETO, result);
        assertEquals( (double)expectedPoint.getX()-2, nextDoubles[0], 0);
        assertEquals( (double)expectedPoint.getY()+2, nextDoubles[1], 0);

        result=pathIterator.currentSegment(nextFloats);
        assertEquals(PathIterator.SEG_LINETO, result);
        assertEquals( (float)expectedPoint.getX()-2, nextFloats[0], 0);
        assertEquals( (float)expectedPoint.getY()+2, nextFloats[1], 0);

        pathIterator.next();
        assertFalse( pathIterator.isDone() );

        result=pathIterator.currentSegment(nextDoubles);
        assertEquals(PathIterator.SEG_LINETO, result);
        assertEquals( (double)expectedPoint.getX()-2, nextDoubles[0], 0);
        assertEquals( (double)expectedPoint.getY()-2, nextDoubles[1], 0);

        result=pathIterator.currentSegment(nextFloats);
        assertEquals(PathIterator.SEG_LINETO, result);
        assertEquals( (float)expectedPoint.getX()-2, nextFloats[0], 0);
        assertEquals( (float)expectedPoint.getY()-2, nextFloats[1], 0);
        
        if( isLastPoint ){
            pathIterator.next();
            assertFalse( pathIterator.isDone() );

            result=pathIterator.currentSegment(nextDoubles);
            assertEquals(PathIterator.SEG_CLOSE, result);

            result=pathIterator.currentSegment(nextFloats);
            assertEquals(PathIterator.SEG_CLOSE, result);
        }
    }
}
