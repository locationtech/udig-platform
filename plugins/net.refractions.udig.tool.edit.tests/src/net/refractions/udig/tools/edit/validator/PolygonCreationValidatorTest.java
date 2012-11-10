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
package net.refractions.udig.tools.edit.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.ShapeType;
import net.refractions.udig.tools.edit.support.TestHandler;

import org.junit.Before;
import org.junit.Test;

public class PolygonCreationValidatorTest {

    private EditBlackboard bb;
    private EditGeom geom;
    private TestHandler handler;
    private ValidHoleValidator validator;
    private PrimitiveShape hole;

    @Before
    public void setUp() throws Exception {
        handler=new TestHandler();
        bb=handler.getEditBlackboard();
        geom=bb.newGeom("id", ShapeType.POLYGON); //$NON-NLS-1$
        bb.addPoint(0, 0, geom.getShell());
        bb.addPoint(100, 0, geom.getShell());
        bb.addPoint(100, 100, geom.getShell());
        bb.addPoint(0, 100, geom.getShell());
        bb.addPoint(0, 0, geom.getShell());
        
        validator=new ValidHoleValidator();
        
        hole=geom.newHole();
        
        handler.setCurrentShape(hole);
    }
    
    @Test
    public void testStartingHoles() throws Exception {

        MapMouseEvent event=new MapMouseEvent(null, 10,10,MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertNull(validator.isValid(handler, event, EventType.RELEASED));
        
        bb.addPoint(5, 5, hole);
        
        event=new MapMouseEvent(null, 10,10,MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertNull(validator.isValid(handler, event, EventType.RELEASED));
        
    }

    @Test
    public void testSelfIntersection() {
        bb.addPoint(10, 10, hole);
        bb.addPoint(20, 10, hole);
        bb.addPoint(20, 20, hole);
        bb.addPoint(10, 20, hole);
        
//      just closing hole should be legal
        MapMouseEvent event=new MapMouseEvent(null, 10,10,MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertNull(validator.isValid(handler, event, EventType.RELEASED));
        
//      no intersection so should be good
        event=new MapMouseEvent(null, 5,15,MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertNull(validator.isValid(handler, event, EventType.RELEASED));
        
//      crosses 1st line so illegal
        event=new MapMouseEvent(null, 10, 5 ,MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertEquals(Messages.ValidHoleValidator_selfIntersection, validator.isValid(handler, event, EventType.RELEASED));
    }
    
    @Test
    public void testOutSideOfShell() throws Exception {

        bb.addPoint(10, 10, hole);
        bb.addPoint(20, 10, hole);
        bb.addPoint(20, 20, hole);
        bb.addPoint(10, 20, hole);
        MapMouseEvent event=new MapMouseEvent(null, -5,20,MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertEquals(Messages.ValidHoleValidator_outsideShell, validator.isValid(handler, event, EventType.RELEASED));
    }
    
    @Test
    public void testInOtherHole() throws Exception {

        bb.addPoint(10, 10, hole);
        bb.addPoint(20, 10, hole);
        
        PrimitiveShape hole2 = geom.newHole();
        
        bb.addPoint(50, 50, hole2);
        bb.addPoint(70, 50, hole2);
        bb.addPoint(70, 70, hole2);
        bb.addPoint(50, 70, hole2);
        bb.addPoint(50, 50, hole2);
        
        MapMouseEvent event=new MapMouseEvent(null, 60, 60,MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertEquals(Messages.ValidHoleValidator_holeOverlap, validator.isValid(handler, event, EventType.RELEASED));
        
    }

}
