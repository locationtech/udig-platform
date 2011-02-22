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

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import junit.framework.TestCase;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.Behaviour;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.ShapeType;
import net.refractions.udig.tools.edit.support.TestHandler;

/**
 * Test FreeHandPolygon Behaviour
 * @author jones
 * @since 1.1.0
 */
public class FreeHandPolygonDrawBehaviourTest extends TestCase {

    private TestHandler handler;
    private FreeHandPolygonDrawBehaviour behav;
    private TestAcceptBehaviour acceptor;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        handler=new TestHandler();
        behav = new FreeHandPolygonDrawBehaviour();
        handler.getBehaviours().add(behav);
        handler.getTestEditBlackboard().util.setVertexRadius(4);
        acceptor=new TestAcceptBehaviour();
        handler.getAcceptBehaviours().add(acceptor);
    }

    /*
     * Test method for 'net.refractions.udig.tools.edit.behaviour.FreeHandPolygonDrawBehaviour.isValid(EditToolHandler, MapMouseEvent, EventType)'
     */
    public void testIsValid() {
        MapMouseEvent event=new MapMouseEvent(null, 10,10,MapMouseEvent.NONE,MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        assertTrue(behav.isValid(handler, event, EventType.DRAGGED));
        assertFalse(behav.isValid(handler, event, EventType.RELEASED));
        assertFalse(behav.isValid(handler, event, EventType.PRESSED));
        assertFalse(behav.isValid(handler, event, EventType.MOVED));
        assertFalse(behav.isValid(handler, event, EventType.WHEEL));
        assertTrue(behav.isValid(handler, event, EventType.DRAGGED));
        event=new MapMouseEvent(null, 10,10,MapMouseEvent.NONE,MapMouseEvent.BUTTON2, MapMouseEvent.BUTTON1);
        assertFalse(behav.isValid(handler, event, EventType.DRAGGED));
    }

    /*
     * Test method for 'net.refractions.udig.tools.edit.behaviour.FreeHandPolygonDrawBehaviour.getCommand(EditToolHandler, MapMouseEvent, EventType)'
     */
    public void testDrawLine() {
        MapMouseEvent event=new MapMouseEvent(null, 10,10,MapMouseEvent.NONE,MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        handler.getMouseTracker().setDragStarted(Point.valueOf(0,10));
        handler.handleEvent(event, EventType.DRAGGED);

        assertTrue(handler.isLocked());
        assertNotNull(handler.getCurrentShape());
        assertEquals(EditState.CREATING, handler.getCurrentState());

        event=new MapMouseEvent(null, 20,10,MapMouseEvent.NONE,MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);
        handler.handleEvent(event, EventType.RELEASED);

        assertFalse(handler.isLocked());
        assertEquals(Point.valueOf(0,10), handler.getCurrentShape().getPoint(0));
        assertEquals(Point.valueOf(10,10), handler.getCurrentShape().getPoint(1));
        assertEquals(Point.valueOf(20,10), handler.getCurrentShape().getPoint(2));
        assertEquals(ShapeType.LINE, handler.getCurrentGeom().getShapeType());
        assertFalse( acceptor.ran);

        //continue line
        PrimitiveShape currentShape = handler.getCurrentShape();

        handler.getMouseTracker().setDragStarted(Point.valueOf(20,11));
        event=new MapMouseEvent(null, 30,10,MapMouseEvent.NONE,MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);
        handler.handleEvent(event, EventType.RELEASED);

        assertEquals(currentShape, handler.getCurrentShape());
        assertEquals(Point.valueOf(0,10), handler.getCurrentShape().getPoint(0));
        assertEquals(Point.valueOf(10,10), handler.getCurrentShape().getPoint(1));
        assertEquals(Point.valueOf(20,10), handler.getCurrentShape().getPoint(2));
        assertEquals(Point.valueOf(30,10), handler.getCurrentShape().getPoint(3));


        //start a new line
        acceptor.ran=false;
        handler.getMouseTracker().setDragStarted(Point.valueOf(100,10));
        event=new MapMouseEvent(null, 100,20,MapMouseEvent.NONE,MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);

        handler.handleEvent(event, EventType.RELEASED);

        assertEquals(Point.valueOf(100,10), handler.getCurrentShape().getPoint(0));
        assertEquals(Point.valueOf(100,20), handler.getCurrentShape().getPoint(1));

    }

    public void testDrawPolygon(){
        handler.getMouseTracker().setDragStarted(Point.valueOf(0,10));

        MapMouseEvent event=new MapMouseEvent(null, 10,10,MapMouseEvent.NONE,MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);

        event=new MapMouseEvent(null, 20,10,MapMouseEvent.NONE,MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);

        event=new MapMouseEvent(null, 20,20,MapMouseEvent.NONE,MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);

        event=new MapMouseEvent(null, 10,20,MapMouseEvent.NONE,MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);

        event=new MapMouseEvent(null, 1,10,MapMouseEvent.NONE,MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);

        handler.handleEvent(event, EventType.RELEASED);

        assertEquals(Point.valueOf(0,10), handler.getCurrentShape().getPoint(0));
        assertEquals(Point.valueOf(10,10), handler.getCurrentShape().getPoint(1));
        assertEquals(Point.valueOf(20,10), handler.getCurrentShape().getPoint(2));
        assertEquals(Point.valueOf(20,20), handler.getCurrentShape().getPoint(3));
        assertEquals(Point.valueOf(10,20), handler.getCurrentShape().getPoint(4));
        assertEquals(Point.valueOf(0,10), handler.getCurrentShape().getPoint(5));
        assertEquals(ShapeType.POLYGON, handler.getCurrentGeom().getShapeType());

    }

    public void testCutHole() throws Exception {
        EditBlackboard editBlackboard = handler.getEditBlackboard();
        EditGeom geom = editBlackboard.getGeoms().get(0);
        editBlackboard.addPoint(0,0,geom.getShell());
        editBlackboard.addPoint(40,0,geom.getShell());
        editBlackboard.addPoint(40,40,geom.getShell());
        editBlackboard.addPoint(0,40,geom.getShell());
        editBlackboard.addPoint(0,0,geom.getShell());
        handler.setCurrentShape(geom.getShell());

        handler.getMouseTracker().setDragStarted(Point.valueOf(10,10));

        MapMouseEvent event=new MapMouseEvent(null, 20,10,MapMouseEvent.NONE,MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);

        event=new MapMouseEvent(null, 20,20,MapMouseEvent.NONE,MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);

        event=new MapMouseEvent(null, 10,20,MapMouseEvent.NONE,MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);

        event=new MapMouseEvent(null, 10,10,MapMouseEvent.NONE,MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);

        handler.handleEvent(event, EventType.RELEASED);

        assertEquals(geom, handler.getCurrentGeom());
        assertEquals(geom.getHoles().get(0), handler.getCurrentShape());
        assertEquals(Point.valueOf(10,10), handler.getCurrentShape().getPoint(0));
        assertEquals(Point.valueOf(20,10), handler.getCurrentShape().getPoint(1));
        assertEquals(Point.valueOf(20,20), handler.getCurrentShape().getPoint(2));
        assertEquals(Point.valueOf(10,20), handler.getCurrentShape().getPoint(3));
    }

    private static final boolean INTERACTIVE = false;
    private int height = 50;
    private int width = 50;
    java.awt.Point SCREEN=new java.awt.Point(width,height);

    private BufferedImage actual = new BufferedImage(width, height,
            BufferedImage.TYPE_4BYTE_ABGR);
    private BufferedImage expected = new BufferedImage(width, height,
            BufferedImage.TYPE_4BYTE_ABGR);
    JFrame frame;


    protected void openFrame() throws Exception {
        if (INTERACTIVE) {
            frame = new JFrame("EditGeomPathIterator Test"); //$NON-NLS-1$
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().setLayout(new GridLayout(1,2));
            frame.getContentPane().add(new JPanel(){
                /** long serialVersionUID field */
                private static final long serialVersionUID = 1L;

                @Override
                public void paint( Graphics g ) {
                  g.drawImage(actual, 0, 0, this);
                  g.drawRect(0,0,width, height);
                }
            });

            frame.getContentPane().add(new JPanel(){
                /** long serialVersionUID field */
                private static final long serialVersionUID = 1L;

                @Override
                public void paint( Graphics g ) {
                  g.drawImage(expected, 0, 0, this);
                  g.drawRect(0,0,width, height);
                }
            });

            frame.setSize(width*2+20, height+50);

            frame.setVisible(true);
        }
    }

    protected void closeFrame() throws Exception {
        if (frame != null)
            frame.dispose();
        super.tearDown();
    }

    class TestAcceptBehaviour implements Behaviour{

        boolean ran=false;

        public boolean isValid( EditToolHandler handler ) {
            return handler.getCurrentShape()!=null && ( handler.getCurrentState()==EditState.CREATING
                    || handler.getCurrentState()==EditState.MODIFYING);
        }

        public UndoableMapCommand getCommand( EditToolHandler handler ) {
            this.ran=true;
            return null;
        }

        public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        }

    }



}
