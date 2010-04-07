package net.refractions.udig.tools.edit.behaviour;

import junit.framework.TestCase;
import net.refractions.udig.project.IEditManager;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.CommandManager;
import net.refractions.udig.project.internal.EditManager;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.ShapeType;
import net.refractions.udig.tools.edit.support.TestHandler;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.WaitCondition;

import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class StartEditingBehaviourTest extends TestCase {

    /*
     * Test method for 'net.refractions.udig.tools.edit.behaviour.StartEditingBehaviour.isValid(EditToolHandler, MapMouseEvent, EventType)'
     */
    public void testIsValid() throws Exception {
        TestHandler handler=new TestHandler();
        
        StartEditingBehaviour behavior=new StartEditingBehaviour(ShapeType.POLYGON);

        MapMouseEvent event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertTrue(behavior.isValid(handler, event, EventType.RELEASED));

        event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.BUTTON2, MapMouseEvent.BUTTON1);
        assertFalse(behavior.isValid(handler, event, EventType.RELEASED));
        
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON2);
        assertFalse(behavior.isValid(handler, event, EventType.RELEASED));

        // no modifiers only
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.ALT_DOWN_MASK, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertFalse(behavior.isValid(handler, event, EventType.RELEASED));

        // works only with NONE
        handler.setCurrentState(EditState.CREATING);
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertFalse(behavior.isValid(handler, event, EventType.RELEASED));
        
        // works only with NONE
        handler.setCurrentState(EditState.CREATING);
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);

        
        // should work, just checking state is still good;
        handler.setCurrentState(EditState.NONE);
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertTrue(behavior.isValid(handler, event, EventType.RELEASED));
        // doesn't work with event pressed
        assertFalse(behavior.isValid(handler, event, EventType.PRESSED));        
    }

    public void testRun() throws Exception {
        final TestHandler handler=new TestHandler();
        
        ILayer layer = handler.getContext().getMapLayers().get(0);
        FeatureSource<SimpleFeatureType, SimpleFeature> resource = layer.getResource(FeatureSource.class, null);
        SimpleFeature feature = resource.getFeatures().features().next();
        IEditManager editManager = handler.getContext().getEditManager();
		((EditManager)editManager).setEditFeature(feature, (Layer) layer);
        
        EditBlackboard editBlackboard = handler.getEditBlackboard();
        PrimitiveShape shell = editBlackboard.getGeoms().get(0).getShell();
        editBlackboard.addPoint(100,100,shell);
        shell.getEditGeom().setShapeType(ShapeType.POINT);
        editBlackboard.newGeom("newone", null); //$NON-NLS-1$
        
        StartEditingBehaviour behav=new StartEditingBehaviour(ShapeType.POLYGON);

        handler.getBehaviours().add(behav);
        
        assertNotNull( editManager.getEditFeature());
        
        handler.setTesting(false);
        MapMouseEvent event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.RELEASED);
        try{
            PlatformGIS.wait(200, 200, WaitCondition.FALSE_CONDITION, null);
        }catch (Exception e) {
            // its expected
        }
        assertEquals(1, handler.getEditBlackboard().getGeoms().size());
        
        ((CommandManager)((Map)handler.getContext().getMap()).getCommandStack()).undo(false);
        
        assertEquals("Is the feature ID equal", feature.getID(), editManager.getEditFeature().getID());        
        assertEquals("Is the feature equal", feature, editManager.getEditFeature());
        assertFalse( handler.isLocked() );
        assertEquals(2, editBlackboard.getGeoms().size());
        assertEquals(ShapeType.POINT, editBlackboard.getGeoms().get(0).getShapeType());
        assertEquals(Point.valueOf(100,100), editBlackboard.getGeoms().get(0).getShell().getPoint(0));
        
    }
    
}
