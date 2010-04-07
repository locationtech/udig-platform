package net.refractions.udig.tools.edit.validator;

import junit.framework.TestCase;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.ShapeType;
import net.refractions.udig.tools.edit.support.TestHandler;


public class ValidHoleValidatorTest extends TestCase {

    private EditBlackboard bb;
    private EditGeom geom;
    private TestHandler handler;
    private PolygonCreationValidator validator;
    private PrimitiveShape shell;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        handler=new TestHandler();
        bb=handler.getEditBlackboard();
        geom=bb.newGeom("id", ShapeType.POLYGON); //$NON-NLS-1$
        validator=new PolygonCreationValidator();
        
        shell=geom.getShell();
        
        handler.setCurrentShape(shell);
    }
    
    public void testStartingPolygon() throws Exception {

        MapMouseEvent event=new MapMouseEvent(null, 10,10,MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertNull(validator.isValid(handler, event, EventType.RELEASED));
        
        bb.addPoint(5, 5, shell);
        
        event=new MapMouseEvent(null, 10,10,MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertNull(validator.isValid(handler, event, EventType.RELEASED));        

        bb.addPoint(10, 10, shell);
        
        event=new MapMouseEvent(null, 10,10,MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertNull(validator.isValid(handler, event, EventType.RELEASED));
        
    }

    public void testSelfIntersection() {
        bb.addPoint(10, 10, shell);
        bb.addPoint(20, 10, shell);
        bb.addPoint(20, 20, shell);
        bb.addPoint(10, 20, shell);
        
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
    
}
