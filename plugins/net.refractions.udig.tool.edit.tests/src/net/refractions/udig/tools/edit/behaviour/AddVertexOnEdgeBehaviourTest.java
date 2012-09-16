package net.refractions.udig.tools.edit.behaviour;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.TestHandler;

import org.junit.Before;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;

public class AddVertexOnEdgeBehaviourTest {

    private TestHandler handler;
    private PrimitiveShape shell;
    
    @Before
    public void setUp() throws Exception {
        handler = new TestHandler();
        shell = handler.getEditBlackboard().getGeoms().get(0).getShell();
        handler.getEditBlackboard().addPoint(0, 10, shell);
        handler.getEditBlackboard().addPoint(20, 10, shell);
        handler.getEditBlackboard().addPoint(20, 20, shell);
        handler.getEditBlackboard().addPoint(10, 20, shell);
        handler.getEditBlackboard().addPoint(0, 10, shell);

        handler.getTestEditBlackboard().util.setVertexRadius(4);
    }

    /*
     * Test method for
     * 'net.refractions.udig.tools.edit.behaviour.AddVertexOnEdgeBehaviour.isValid(EditToolHandler,
     * MapMouseEvent, EventType)'
     */
    @Test
    public void testIsValid() {
        InsertVertexOnEdgeBehaviour behaviour = new InsertVertexOnEdgeBehaviour();

        handler.setCurrentState(EditState.MODIFYING);

        // current shape is not set
        MapMouseEvent event = new MapMouseEvent(null, 10, 10, MapMouseEvent.NONE,
                MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertFalse(behaviour.isValid(handler, event, EventType.RELEASED));

        handler.setCurrentShape(shell);

        // should work
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.NONE, MapMouseEvent.NONE,
                MapMouseEvent.BUTTON1);
        assertTrue(behaviour.isValid(handler, event, EventType.RELEASED));

        // creating not valid state
        handler.setCurrentState(EditState.CREATING);
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.NONE, MapMouseEvent.NONE,
                MapMouseEvent.BUTTON1);
        assertFalse(behaviour.isValid(handler, event, EventType.RELEASED));

        // MOVING not valid state
        handler.setCurrentState(EditState.MOVING);
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.NONE, MapMouseEvent.NONE,
                MapMouseEvent.BUTTON1);
        assertFalse(behaviour.isValid(handler, event, EventType.RELEASED));

        // BUSY not valid state
        handler.setCurrentState(EditState.BUSY);
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.NONE, MapMouseEvent.NONE,
                MapMouseEvent.BUTTON1);
        assertFalse(behaviour.isValid(handler, event, EventType.RELEASED));

        // ILLEGAL not valid state
        handler.setCurrentState(EditState.ILLEGAL);
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.NONE, MapMouseEvent.NONE,
                MapMouseEvent.BUTTON1);
        assertFalse(behaviour.isValid(handler, event, EventType.RELEASED));

        // make sure state is good
        handler.setCurrentState(EditState.MODIFYING);
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.NONE, MapMouseEvent.NONE,
                MapMouseEvent.BUTTON1);
        assertTrue(behaviour.isValid(handler, event, EventType.RELEASED));

        // Button2 not acceptable
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.NONE, MapMouseEvent.NONE,
                MapMouseEvent.BUTTON2);
        assertFalse(behaviour.isValid(handler, event, EventType.RELEASED));

        // buttons down not acceptable
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.NONE, MapMouseEvent.BUTTON2,
                MapMouseEvent.BUTTON1);
        assertFalse(behaviour.isValid(handler, event, EventType.RELEASED));

        // modifiers not acceptable
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.ALT_DOWN_MASK, MapMouseEvent.NONE,
                MapMouseEvent.BUTTON1);
        assertFalse(behaviour.isValid(handler, event, EventType.RELEASED));

        // DRAGGED not acceptable
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.NONE, MapMouseEvent.NONE,
                MapMouseEvent.BUTTON1);
        assertFalse(behaviour.isValid(handler, event, EventType.DRAGGED));

        // DOUBLE_CLICK not acceptable
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.NONE, MapMouseEvent.NONE,
                MapMouseEvent.BUTTON1);
        assertFalse(behaviour.isValid(handler, event, EventType.DOUBLE_CLICK));

        // ENTERED not acceptable
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.NONE, MapMouseEvent.NONE,
                MapMouseEvent.BUTTON1);
        assertFalse(behaviour.isValid(handler, event, EventType.ENTERED));

        // EXITED not acceptable
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.NONE, MapMouseEvent.NONE,
                MapMouseEvent.BUTTON1);
        assertFalse(behaviour.isValid(handler, event, EventType.EXITED));

        // MOVED not acceptable
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.NONE, MapMouseEvent.NONE,
                MapMouseEvent.BUTTON1);
        assertFalse(behaviour.isValid(handler, event, EventType.MOVED));

        // WHEEL not acceptable
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.NONE, MapMouseEvent.NONE,
                MapMouseEvent.BUTTON1);
        assertFalse(behaviour.isValid(handler, event, EventType.WHEEL));

        // close enough to edge
        event = new MapMouseEvent(null, 10, 8, MapMouseEvent.NONE, MapMouseEvent.NONE,
                MapMouseEvent.BUTTON1);
        assertTrue(behaviour.isValid(handler, event, EventType.RELEASED));

        // not close enough to edge
        event = new MapMouseEvent(null, 0, -10, MapMouseEvent.NONE, MapMouseEvent.NONE,
                MapMouseEvent.BUTTON1);
        assertFalse(behaviour.isValid(handler, event, EventType.RELEASED));

    }

    /*
     * Test method for
     * 'net.refractions.udig.tools.edit.behaviour.AddVertexOnEdgeBehaviour.getCommand(EditToolHandler,
     * MapMouseEvent, EventType)'
     */
    @Test
    public void testGetCommand() {
        int x = 10;
        int y = 8;
        MapMouseEvent event = new MapMouseEvent(null, x, y, MapMouseEvent.NONE,
                MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        handler.getBehaviours().add(new InsertVertexOnEdgeBehaviour());

        handler.setCurrentState(EditState.MODIFYING);
        handler.setCurrentShape(shell);
        handler.handleEvent(event, EventType.RELEASED);
        assertTrue("Shape should have a new point", shell.hasVertex(Point.valueOf(x, y))); //$NON-NLS-1$
        assertEquals(
                "Blackboard should also reflect the change", 1, handler.getEditBlackboard().getCoords(x, y).size()); //$NON-NLS-1$
        Coordinate[] array = shell.coordArray();
        boolean found = false;
        Coordinate expected = handler.getEditBlackboard().toCoord(Point.valueOf(x, y));
        for( Coordinate coordinate : array ) {
            if (coordinate.equals(expected)) {
                found = true;
                return;
            }
        }
        assertTrue("Shape should have the new Coordinate", found); //$NON-NLS-1$
    }

}
