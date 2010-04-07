/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.tools.edit.behaviour;

import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import junit.framework.TestCase;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.CommandManager;
import net.refractions.udig.project.internal.EditManager;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.behaviour.CreateShapeBehaviour.ShapeFactory;
import net.refractions.udig.tools.edit.behaviour.accept.AcceptChangesBehaviour;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.ShapeType;
import net.refractions.udig.tools.edit.support.TestHandler;
import net.refractions.udig.ui.WaitCondition;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Polygon;

/**
 * Tests the ShapeCreationBehaviour.
 * @author jones
 * @since 1.1.0
 */
public class ShapeCreationBehaviourTest extends TestCase {

    public void testIsValid() throws Exception {
        TestHandler handler=new TestHandler();
        
        CreateShapeBehaviour behav=new CreateShapeBehaviour(new ShapeFactory(){
            @Override
            public GeneralPath create( int width, int height ) {
                GeneralPath path=new GeneralPath();
                path.append(new Rectangle(width, height), false);
                return path;
            }
        });
        
        MapMouseEvent event=new MapMouseEvent( null, 0,0, MapMouseEvent.NONE, 
                MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        assertTrue(behav.isValid(handler, event, EventType.DRAGGED));
        
        //released no legal
        event=new MapMouseEvent( null, 0,0, MapMouseEvent.NONE, 
                MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        assertFalse(behav.isValid(handler, event, EventType.RELEASED));

        // must be mouse button 1
        event=new MapMouseEvent( null, 0,0, MapMouseEvent.NONE, 
                MapMouseEvent.BUTTON2, MapMouseEvent.BUTTON1);
        assertFalse(behav.isValid(handler, event, EventType.DRAGGED));

        // cannot be Modify state
        handler.setCurrentState(EditState.MODIFYING);
        event=new MapMouseEvent( null, 0,0, MapMouseEvent.NONE, 
                MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        assertFalse(behav.isValid(handler, event, EventType.DRAGGED));

        // cannot be creating state
        handler.setCurrentState(EditState.CREATING);
        event=new MapMouseEvent( null, 0,0, MapMouseEvent.NONE, 
                MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        assertFalse(behav.isValid(handler, event, EventType.DRAGGED));
        
        // make sure state is good
        handler.setCurrentState(EditState.NONE);
        event=new MapMouseEvent( null, 0,0, MapMouseEvent.NONE, 
                MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        assertTrue(behav.isValid(handler, event, EventType.DRAGGED));

        // Alt down is not legal
        handler.setCurrentState(EditState.NONE);
        event=new MapMouseEvent( null, 0,0, MapMouseEvent.ALT_DOWN_MASK, 
                MapMouseEvent.BUTTON2, MapMouseEvent.BUTTON1);
        assertFalse(behav.isValid(handler, event, EventType.DRAGGED));

    
    }
    
    /*
     * Test method for 'net.refractions.udig.tools.edit.behaviour.ShapeCreationBehaviour.getCommand(EditToolHandler, MapMouseEvent, EventType)'
     */
    public void testRectangleShape() throws Exception {
        final TestHandler handler=new TestHandler();
        ILayer layer = handler.getContext().getMapLayers().get(0);
        FeatureSource<SimpleFeatureType, SimpleFeature> resource = layer.getResource(FeatureSource.class, null);
        SimpleFeature feature = resource.getFeatures().features().next();
        ((EditManager)handler.getContext().getEditManager()).setEditFeature(feature, (Layer) layer);
        
        final EditBlackboard editBlackboard = handler.getEditBlackboard();
        PrimitiveShape shell = editBlackboard.getGeoms().get(0).getShell();
        editBlackboard.addPoint(100,100,shell);
        shell.getEditGeom().setShapeType(ShapeType.POINT);
        editBlackboard.newGeom("newone", null); //$NON-NLS-1$
        
        CreateShapeBehaviour behav=new CreateShapeBehaviour(new ShapeFactory(){
            @Override
            public GeneralPath create( int width, int height ) {
                GeneralPath path=new GeneralPath();
                path.append(new Rectangle(width, height), false);
                return path;
            }
        });
        
        handler.getBehaviours().add(behav);
        
        assertNotNull( handler.getContext().getEditManager().getEditFeature());

        handler.getAcceptBehaviours().add(new AcceptChangesBehaviour(Polygon.class, false));
        handler.getMouseTracker().setDragStarted(Point.valueOf(10,0));
        MapMouseEvent event=new MapMouseEvent( null, 20, 20, MapMouseEvent.NONE, 
                MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);
        
        assertTrue( handler.isLocked() );
        assertTrue( handler.isLockOwner(behav) );

        event=new MapMouseEvent( null, 20, 20, MapMouseEvent.NONE, 
                MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        handler.setTesting(false);
        handler.handleEvent(event, EventType.RELEASED);

        UDIGTestUtil.inDisplayThreadWait(1000, new WaitCondition(){

            public boolean isTrue() {
                return handler.getCurrentState()==EditState.NONE && editBlackboard.getGeoms().size()==1
                && 2==editBlackboard.getCoords(10,0).size();
            }
        }, false);
        
        assertNotSame( feature, handler.getContext().getEditManager().getEditFeature());
        assertFalse( handler.isLocked() );
        assertEquals( 1, editBlackboard.getGeoms().size());
        assertEquals( 2, editBlackboard.getCoords(10,0).size() );
        assertEquals( 1, editBlackboard.getCoords(20,0).size() );
        assertEquals( 1, editBlackboard.getCoords(20,20).size() );
        assertEquals( 1, editBlackboard.getCoords(10,20).size() );
        assertEquals(ShapeType.POLYGON, editBlackboard.getGeoms().get(0).getShapeType());
        
        ((CommandManager)((Map)handler.getContext().getMap()).getCommandStack()).undo(false);
        
        assertNotSame(feature, handler.getContext().getEditManager().getEditFeature());
        assertFalse( handler.isLocked() );
        assertEquals(2, editBlackboard.getGeoms().size());
        assertEquals(ShapeType.POINT, editBlackboard.getGeoms().get(0).getShapeType());
        assertEquals(Point.valueOf(100,100), editBlackboard.getGeoms().get(0).getShell().getPoint(0));
        
    }

    public void testDonut() throws Exception {
        TestHandler handler=new TestHandler();
        
        CreateShapeBehaviour behav=new CreateShapeBehaviour(new ShapeFactory(){
            @Override
            public GeneralPath create( int width, int height ) {
                
                Rectangle2D shell =new Rectangle2D.Float(0,0,width,height);
                float i = ((float)width)/4;
                float j = ((float)height)/4;
                Rectangle2D hole =new Rectangle2D.Float(i,j,((float)width)/2,((float)height)/2);
                GeneralPath path=new GeneralPath();
                path.append(shell, false);
                path.append(hole, false);
                
                return path;
            }
        });
        
        handler.getBehaviours().add(behav);
        
        handler.getAcceptBehaviours().add(new AcceptChangesBehaviour(Polygon.class, false));
        handler.getMouseTracker().setDragStarted(Point.valueOf(10,0));
        MapMouseEvent event=new MapMouseEvent( null, 26, 16, MapMouseEvent.NONE, 
                MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);
        
        assertTrue( handler.isLocked() );
        assertTrue( handler.isLockOwner(behav) );

        event=new MapMouseEvent( null, 26, 16, MapMouseEvent.NONE, 
                MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.RELEASED);
        
        assertFalse( handler.isLocked() );
        assertEquals( 2, handler.getEditBlackboard().getCoords(10,0).size() );
        assertEquals( 1, handler.getEditBlackboard().getCoords(26,0).size() );
        assertEquals( 1, handler.getEditBlackboard().getCoords(26,16).size() );
        assertEquals( 1, handler.getEditBlackboard().getCoords(10,16).size() );
        
        assertEquals( 2, handler.getEditBlackboard().getCoords(14,4).size() );
        assertEquals( 1, handler.getEditBlackboard().getCoords(22,4).size() );
        assertEquals( 1, handler.getEditBlackboard().getCoords(22,12).size() );
        assertEquals( 1, handler.getEditBlackboard().getCoords(14,12).size() );
    }
    
}
