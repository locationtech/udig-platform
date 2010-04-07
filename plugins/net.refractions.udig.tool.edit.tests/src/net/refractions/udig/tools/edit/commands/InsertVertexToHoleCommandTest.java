package net.refractions.udig.tools.edit.commands;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;

import junit.framework.TestCase;
import net.refractions.udig.TestViewportPane;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditUtils;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.TestHandler;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.operation.MathTransform;

public class InsertVertexToHoleCommandTest extends TestCase {
    AffineTransform transform=AffineTransform.getTranslateInstance(0,0);
    private MathTransform layerToWorld;

    java.awt.Point SCREEN=new java.awt.Point(500,500);

    protected void setUp() throws Exception {
        super.setUp();
        layerToWorld=CRS.findMathTransform(DefaultGeographicCRS.WGS84, DefaultGeographicCRS.WGS84);    
    }
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
