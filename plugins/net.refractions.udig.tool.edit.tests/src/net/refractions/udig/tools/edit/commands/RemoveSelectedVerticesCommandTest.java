package net.refractions.udig.tools.edit.commands;

import static org.junit.Assert.assertEquals;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.ShapeType;
import net.refractions.udig.tools.edit.support.TestHandler;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Before;
import org.junit.Test;

public class RemoveSelectedVerticesCommandTest {

    private TestHandler handler;
    private PrimitiveShape shell;
    
    @Before
    public void setUp() throws Exception {
        handler=new TestHandler();
        shell = handler.getEditBlackboard().getGeoms().get(0).getShell();
        handler.getEditBlackboard().addPoint(10,10, shell);
        handler.getEditBlackboard().addPoint(20,10, shell);
        handler.getEditBlackboard().addPoint(20,20, shell);
        handler.getEditBlackboard().addPoint(10,20, shell);
        handler.getEditBlackboard().addPoint(10,10, shell);
        
        handler.setCurrentShape(shell);
    }
    
    /*
     * Test method for 'net.refractions.udig.tools.edit.commands.RemoveSelectedVerticesCommand.run(IProgressMonitor)'
     */
    @Test
    public void testRun() throws Exception {
        handler.getEditBlackboard().selectionAdd(Point.valueOf(10,10));
        handler.getEditBlackboard().selectionAdd(Point.valueOf(10,20));
        
        RemoveSelectedVerticesCommand command=new RemoveSelectedVerticesCommand(handler);
        
        command.setRunAnimation(false);
        command.setMap((Map) handler.getContext().getMap());
        command.run(new NullProgressMonitor());

        assertEquals(0, handler.getEditBlackboard().getCoords(10,10).size());
        assertEquals(0, handler.getEditBlackboard().getCoords(10,20).size());
        assertEquals(1, handler.getEditBlackboard().getCoords(20,20).size());
        assertEquals(1, handler.getEditBlackboard().getCoords(20,10).size());
        
        assertEquals(2, shell.getNumPoints());
        assertEquals(2, shell.getNumCoords());
        
        command.rollback(new NullProgressMonitor());
        
        assertEquals(2, handler.getEditBlackboard().getCoords(10,10).size());
        assertEquals(1, handler.getEditBlackboard().getCoords(10,20).size());
        assertEquals(1, handler.getEditBlackboard().getCoords(20,20).size());
        assertEquals(1, handler.getEditBlackboard().getCoords(20,10).size());
        
        assertEquals(5, shell.getNumPoints());
        assertEquals(5, shell.getNumCoords());
        
    }    

    /*
     * If shape is a polygon even after deletion the geometry still be a polygon.
     */
    @Test
    public void testRemoveStartVertexOnPolygon() throws Exception {
        handler.getEditBlackboard().selectionAdd(Point.valueOf(10,10));
        handler.getEditBlackboard().selectionAdd(Point.valueOf(10,20));
        
        RemoveSelectedVerticesCommand command=new RemoveSelectedVerticesCommand(handler);

        shell.getEditGeom().setShapeType(ShapeType.POLYGON);
        
        command.setMap((Map) handler.getContext().getMap());

        command.setRunAnimation(false);
        command.run(new NullProgressMonitor());

        assertEquals(0, handler.getEditBlackboard().getCoords(10,10).size());
        assertEquals(0, handler.getEditBlackboard().getCoords(10,20).size());
        assertEquals(2, handler.getEditBlackboard().getCoords(20,10).size());
        assertEquals(1, handler.getEditBlackboard().getCoords(20,20).size());
        
        assertEquals(3, shell.getNumPoints());
        assertEquals(3, shell.getNumCoords());
        
        command.rollback(new NullProgressMonitor());
        
        assertEquals(2, handler.getEditBlackboard().getCoords(10,10).size());
        assertEquals(1, handler.getEditBlackboard().getCoords(10,20).size());
        assertEquals(1, handler.getEditBlackboard().getCoords(20,20).size());
        assertEquals(1, handler.getEditBlackboard().getCoords(20,10).size());
        
        assertEquals(5, shell.getNumPoints());
        assertEquals(5, shell.getNumCoords());
    }

}
