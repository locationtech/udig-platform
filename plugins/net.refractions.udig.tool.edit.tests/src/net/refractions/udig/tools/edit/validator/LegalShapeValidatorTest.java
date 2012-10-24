/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.tools.edit.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.ShapeType;
import net.refractions.udig.tools.edit.support.TestHandler;

import org.junit.Before;
import org.junit.Test;

public class LegalShapeValidatorTest {

    private TestHandler handler;
    private EditBlackboard bb;
    private LegalShapeValidator validator;

    @Before
    public void setUp() throws Exception {
        handler = new TestHandler();
        bb=handler.getEditBlackboard();
        bb.clear();
        
        validator=new LegalShapeValidator(); 
    }

    @Test
    public void testLine() throws Exception {
        EditGeom line = bb.newGeom("line", ShapeType.LINE); //$NON-NLS-1$
        PrimitiveShape shell = line.getShell();
        
        bb.addPoint(10, 10, shell);
        bb.addPoint(20, 20, shell);
        bb.addPoint(20, 30, shell);
        bb.addPoint(5, 20, shell);
        bb.addPoint(10, 40, shell);
        
        
        assertNull(validator.isValid(handler, null, null));
    }
    
    @Test
    public void testPoint() throws Exception {
        EditGeom point = bb.newGeom("line", ShapeType.POINT); //$NON-NLS-1$
        PrimitiveShape shell = point.getShell();
        
        bb.addPoint(10, 10, shell);
        
        assertNull(validator.isValid(handler, null, null));
    }
    
    @Test
    public void testPolygonLegal() throws Exception {
        EditGeom polygon = bb.newGeom("line", ShapeType.POLYGON); //$NON-NLS-1$
        PrimitiveShape shell = polygon.getShell();
        
        bb.addPoint(10, 10, shell);
        bb.addPoint(20, 10, shell);
        bb.addPoint(20, 20, shell);
        bb.addPoint(10, 20, shell);
        bb.addPoint(10, 10, shell);
        
        
        PrimitiveShape hole = polygon.newHole();
        bb.addPoint(12, 12, hole);
        bb.addPoint(15, 12, hole);
        bb.addPoint(15, 15, hole);
        bb.addPoint(12, 12, hole);
        
        PrimitiveShape hole2 = polygon.newHole();
        bb.addPoint(15, 12, hole2);
        bb.addPoint(19, 19, hole2);
        bb.addPoint(15, 19, hole2);
        bb.addPoint(15, 12, hole2);
        
        assertNull(validator.isValid(handler, null, null));
    }
    
    @Test
    public void testShellSelfIntersection() throws Exception {
        EditGeom polygon = bb.newGeom("line", ShapeType.POLYGON); //$NON-NLS-1$
        PrimitiveShape shell = polygon.getShell();
        
        bb.addPoint(10, 10, shell);
        bb.addPoint(20, 10, shell);
        bb.addPoint(20, 20, shell);
        bb.addPoint(10, 20, shell);
        bb.addPoint(15, 5, shell);
        
        assertEquals(Messages.LegalShapeValidator_shellIntersection, validator.isValid(handler, null, null));
    }

    @Test
    public void testHoleSelfIntersection() throws Exception {
        EditGeom polygon = bb.newGeom("line", ShapeType.POLYGON); //$NON-NLS-1$
        PrimitiveShape shell = polygon.getShell();
        
        bb.addPoint(10, 10, shell);
        bb.addPoint(20, 10, shell);
        bb.addPoint(20, 20, shell);
        bb.addPoint(10, 20, shell);
        bb.addPoint(10, 10, shell);
        
        PrimitiveShape hole = polygon.newHole();
        bb.addPoint(12, 12, hole);
        bb.addPoint(18, 12, hole);
        bb.addPoint(18, 18, hole);
        bb.addPoint(16, 11, hole);
        
        assertEquals(Messages.LegalShapeValidator_holeIntersection, validator.isValid(handler, null, null));
    }
    
    @Test
    public void testHoleOutOfShell() throws Exception {
        EditGeom polygon = bb.newGeom("line", ShapeType.POLYGON); //$NON-NLS-1$
        PrimitiveShape shell = polygon.getShell();
        
        bb.addPoint(10, 10, shell);
        bb.addPoint(20, 10, shell);
        bb.addPoint(20, 20, shell);
        bb.addPoint(10, 20, shell);
        bb.addPoint(10, 10, shell);
        
        PrimitiveShape hole = polygon.newHole();
        bb.addPoint(12, 12, hole);
        bb.addPoint(18, 0, hole);
        
        assertEquals(Messages.LegalShapeValidator_holeOutside, validator.isValid(handler, null, null));
    }
    
    @Test
    public void testHoleIntersections() throws Exception {
        EditGeom polygon = bb.newGeom("line", ShapeType.POLYGON); //$NON-NLS-1$
        PrimitiveShape shell = polygon.getShell();
        
        bb.addPoint(10, 10, shell);
        bb.addPoint(20, 10, shell);
        bb.addPoint(20, 20, shell);
        bb.addPoint(10, 20, shell);
        bb.addPoint(10, 10, shell);
        
        PrimitiveShape hole = polygon.newHole();
        bb.addPoint(13, 12, hole);
        bb.addPoint(18, 12, hole);
        bb.addPoint(18, 18, hole);
        bb.addPoint(13, 12, hole);
        
        PrimitiveShape hole2 = polygon.newHole();
        bb.addPoint(15, 15, hole2);
        bb.addPoint(19, 15, hole2);
        bb.addPoint(19, 19, hole2);
        bb.addPoint(15, 15, hole2);
        
        assertEquals(Messages.LegalShapeValidator_holeOverlap, validator.isValid(handler, null, null));
    }

}
