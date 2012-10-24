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
package net.refractions.udig.tools.edit.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;

import net.refractions.udig.TestViewportPane;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditUtils;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.TestHandler;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Before;
import org.junit.Test;
import org.opengis.referencing.operation.MathTransform;

public class InsertVertexToHoleCommandTest {
    AffineTransform transform=AffineTransform.getTranslateInstance(0,0);
    private MathTransform layerToWorld;

    java.awt.Point SCREEN=new java.awt.Point(500,500);

    @Before
    public void setUp() throws Exception {
        layerToWorld=CRS.findMathTransform(DefaultGeographicCRS.WGS84, DefaultGeographicCRS.WGS84);    
    }
    
    @Test
    public void testRunAndUndo() throws Exception {
        EditBlackboard map=new EditBlackboard(SCREEN.x, SCREEN.y, transform, layerToWorld);
        
        PrimitiveShape hole=map.getGeoms().get(0).newHole();
        InsertVertexCommand command1=new InsertVertexCommand(new TestHandler(), map, new TestViewportPane(new Dimension(500,500)), new EditUtils.StaticShapeProvider(hole), Point.valueOf(10,10), 0, true );
        InsertVertexCommand command2=new InsertVertexCommand(new TestHandler(), map, new TestViewportPane(new Dimension(500,500)), new EditUtils.StaticShapeProvider(hole), Point.valueOf(10,15), 0, true );

        assertEquals(0, map.getCoords(10,10).size());
        assertEquals(0, map.getCoords(10,15).size());
        
        command1.run(new NullProgressMonitor());
        command2.run(new NullProgressMonitor());
        
        assertEquals(1, map.getCoords(10,10).size());
        assertEquals(1, map.getCoords(10,15).size());
        assertEquals( Point.valueOf(10,15), hole.getPoint(0));
        assertEquals( Point.valueOf(10,10), hole.getPoint(1));
        
        command2.rollback(new NullProgressMonitor());
        assertTrue( 0==map.getCoords(10,15).size());
        assertTrue(0==map.getGeoms(10,15).size());
        assertEquals(Point.valueOf(10,10), hole.getPoint(0));
        assertEquals(1, hole.getNumPoints());
        assertEquals(1, hole.getNumCoords());
        
        command1.rollback(new NullProgressMonitor());
        assertTrue(0==map.getCoords(10,10).size());
        assertTrue(0==map.getGeoms(10,10).size());
        assertEquals(0, hole.getNumPoints());
        assertEquals(0, hole.getNumCoords());
        
        
    }
    
    
    
}
