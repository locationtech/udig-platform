package net.refractions.udig.tools.edit.commands;

import junit.framework.TestCase;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.TestHandler;

import org.eclipse.core.runtime.NullProgressMonitor;

public class SelectHoleCommandTest extends TestCase {

    /*
     * Test method for 'net.refractions.udig.tools.edit.commands.SelectHoleCommand.run(IProgressMonitor)'
     */
    public void testRun() throws Exception {
        TestHandler handler=new TestHandler();

        EditBlackboard editBlackboard = handler.getEditBlackboard();
        PrimitiveShape shape = editBlackboard.getGeoms().get(0).getShell();
        editBlackboard.addPoint(0,0, shape);
        editBlackboard.addPoint(100,0, shape);
        editBlackboard.addPoint(100,100, shape);
        editBlackboard.addPoint(0,100, shape);
        editBlackboard.addPoint(0,0, shape);

        PrimitiveShape hole = shape.getEditGeom().newHole();
        editBlackboard.addPoint(5,5, hole);
        editBlackboard.addPoint(20,5, hole);
        editBlackboard.addPoint(20,20, hole);
        editBlackboard.addPoint(5,20, hole);
        editBlackboard.addPoint(5,5, hole);

        PrimitiveShape hole2 = shape.getEditGeom().newHole();
        editBlackboard.addPoint(30,30, hole2);
        editBlackboard.addPoint(80,30, hole2);
        editBlackboard.addPoint(80,80, hole2);
        editBlackboard.addPoint(30,80, hole2);
        editBlackboard.addPoint(30,30, hole2);

        handler.setCurrentShape(shape);

        MapMouseEvent event=new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.NONE);
        SelectHoleCommand command=new SelectHoleCommand(handler, event);

        command.setMap((Map) handler.getContext().getMap());
        NullProgressMonitor nullProgressMonitor = new NullProgressMonitor();
        command.run(nullProgressMonitor);

        assertEquals(hole, handler.getCurrentShape());

        command.rollback(nullProgressMonitor);
        assertEquals(shape, handler.getCurrentShape());

        command.run(nullProgressMonitor);
        assertEquals(hole, handler.getCurrentShape());

        event=new MapMouseEvent(null, 40,40, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.NONE);
        command=new SelectHoleCommand(handler, event);

        command.setMap((Map) handler.getContext().getMap());
        command.run(nullProgressMonitor);

        assertEquals(hole2, handler.getCurrentShape());

        command.rollback(nullProgressMonitor);
        assertEquals(hole, handler.getCurrentShape());
    }

}
