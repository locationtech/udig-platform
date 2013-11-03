/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.commands;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Iterator;

import org.locationtech.udig.core.IProvider;
import org.locationtech.udig.project.ui.commands.AbstractDrawCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseMotionListener;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.preferences.PreferenceConstants;
import org.locationtech.udig.tools.edit.preferences.PreferenceUtil;
import org.locationtech.udig.tools.edit.support.EditGeomPointIterator;
import org.locationtech.udig.tools.edit.support.Point;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;
import org.locationtech.udig.tools.edit.support.ShapeType;
import org.locationtech.udig.ui.graphics.ViewportGraphics;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Draws each vertex point as a rectangle.  Listens to mouse events as long as valid and fills vertext when
 * cursor is over a vertex.
 * 
 * @author jones
 * @since 1.1.0
 */
public class DrawPointCommand extends AbstractDrawCommand implements MapMouseMotionListener {
    
    private PrimitiveShape shape;
    private int radius = 2;
    private IProvider<Color> outline = new IProvider<Color>(){

        public Color get(Object... params) {
            return PreferenceUtil.instance().getDrawVertexLineColor();
        }
        
    };
    private IProvider<Color> fill = new IProvider<Color>(){

        public Color get(Object... params) {
            return PreferenceUtil.instance().getDrawVertexFillColor();
        }
        
    };
    private ViewportPane pane;
    private Point location;
    private int radiusDelta=1;
    private IProvider<Color> selectionFill = new IProvider<Color>(){

        public Color get(Object... params) {
            return PreferenceUtil.instance().getDrawSelectionFillColor();
        }
        
    };
    private boolean overPoint;
    private EditToolHandler handler;
    private boolean drawCurrentShape; 
    IPreferenceStore store = EditPlugin.getDefault().getPreferenceStore();


    public DrawPointCommand( EditToolHandler handler2, PrimitiveShape shape, ViewportPane pane ){
        this.shape=shape;
        this.pane=pane;
        pane.addMouseMotionListener(this);
        this.handler=handler2;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        if( handler.getCurrentState()==EditState.MOVING ){
            return;
        }
        if( drawCurrentShape ){
            shape=handler.getCurrentShape();
        }
        if( shape==null || shape.getNumPoints()==0)
            return;
        Rectangle rect=new Rectangle();
        rect.width=radius*2;
        rect.height=radius*2;
        boolean selected;
        for( Iterator<Point> iter=new EditGeomPointIterator(shape.getEditGeom()); iter.hasNext();) {
            graphics.setStroke(ViewportGraphics.LINE_SOLID, 1);

            Point p=iter.next();
            if( (p.getX()<0||p.getX()>display.getWidth()) 
                    && (p.getY()<0 || p.getY()>display.getHeight()) )
                continue;
            
            rect.x=p.getX()-radius;
            rect.y=p.getY()-radius;
            
            if( overPoint && p==shape.getPoint(0) ){
                drawOverPoint(rect, isSelected(p));
            }else{
                setZoomedRect(rect, p, 0);                    
                selected = isSelected(p);
                fillVertex(rect, selected);
                if( selected ){
                	drawOutline(rect,2, selected);
                }else{
                	drawOutline(rect,1, selected);
                }
            }
        }            
    }

    /**
     * Draws the point the mouse is over a bit bigger and bolder than the others.
     * 
     * @param rect
     */
    private void drawOverPoint( Rectangle rect, boolean selected ) {
        if( overPoint ){
            Point point = shape.getPoint(0);
            rect.x=point.getX()-radius;
            rect.y=point.getY()-radius;
            
            setZoomedRect(rect, point, radiusDelta);
            if( selected )
                graphics.setColor(selectionFill.get());
            else
                graphics.setColor(fill.get());
            graphics.fillRect(rect.x+1, rect.y+1, rect.width-2, rect.height-2);
//            graphics.fillRect(rect.x, rect.y, rect.width, rect.height);
            
            drawOutline(rect,2, selected);
        }
    }

    private void drawOutline( Rectangle rect, int width, boolean selected ) {
        graphics.setLineWidth(width);
        if( selected ){
            graphics.setColor(outline.get());
        } else {
            graphics.setColor(PreferenceUtil.reduceTransparency(outline.get(), .35f));
        }
        graphics.drawRect(rect.x, rect.y, rect.width, rect.height);
    }

    private void fillVertex( Rectangle rect, boolean selected ) {
        if( selected ){
            graphics.setColor(selectionFill.get());
        } else {
            graphics.setColor(this.fill.get());
        }
        
        if( selected || store.getBoolean(PreferenceConstants.P_FILL_VERTICES) 
                || shape.getEditGeom().getShapeType()==ShapeType.POINT )
            graphics.fillRect(rect.x, rect.y, rect.width, rect.height);
    }

    private boolean isSelected( Point p ) {
        if( shape.getEditBlackboard().getSelection().contains(p) ){
            return true;
        }else{
            return false;
        }
    }

    public void setDrawCurrentShape(boolean b) {
        drawCurrentShape=b;
    }
    
    
    /**
     * Sets the size of the point box.  
     *
     * @param rect rectangle to set
     * @param p center point
     * @param widthDelta the number of pixels larger the box should be than the radius.  
     * (radius of box is size of box from center to edge) delta is number of pixel from edge of normal
     * rect to new edge.  So 1 pixel delta would make rectangle 2 pixels larger. 
     */
    private void setZoomedRect( Rectangle rect, Point p, int widthRad ) {
        rect.width=(widthRad*2)+radius*2;
        rect.height=rect.width;
        int i=radius+widthRad;
        rect.x=p.getX()-i;
        rect.y=p.getY()-i;
    }

    private boolean isOverPoint( Point p ) {
        for( int x=-radiusDelta-radius, max=radius+radiusDelta; x<max; x++){
            for( int y=-radiusDelta-radius; y<max; y++){
                if ( Point.valueOf(p.getX()+x,p.getY()+y).equals(location))
                    return true;
            }            
        }
        return false;
    }
    
    @Override
    public void setValid( boolean valid ) {
        if( !valid ){
            pane.removeMouseMotionListener(this);
        }
        super.setValid(valid);
    }

    public void mouseMoved( MapMouseEvent event ) {
        boolean oldIsOver=overPoint;
        this.location=Point.valueOf(event.x, event.y);

        if( shape==null || shape.getNumPoints()==0 )
            return;
        
        Point firstPoint = shape.getPoint(0);
        overPoint=isOverPoint(firstPoint);
        if( overPoint!=oldIsOver )
            handler.repaint();
    }

    public void mouseDragged( MapMouseEvent event ) {
        mouseMoved(event);
    }

    public void setCurrentShape( PrimitiveShape shape) {
        if( shape==this.shape)
            return;
        this.shape=shape;
        handler.repaint();
    }
    /**
     * 
     * @return Returns the radiusDelta.
     */
    public int getRadiusDelta() {
        return radiusDelta;
    }
    /**
     * Default is 1.
     * @param width_rad The radiusDelta to set.
     */
    public void setRadiusDelta( int radiusDelta ) {
        this.radiusDelta = radiusDelta;
    }
    /**
     * @return Returns the radius.
     */
    public int getRadius() {
        return radius;
    }
    /**
     * Default is 3.
     * @param radius The radius to set.
     */
    public void setRadius( int radius ) {
        this.radius = radius-radiusDelta;
    }
    /**
     * @return Returns the geom.
     */
    public PrimitiveShape getGeom() {
        return shape;
    }

    public Rectangle getValidArea() {
        if( shape!=null )
            return shape.getBounds();
        else
            return null;
    }

    public void mouseHovered( MapMouseEvent event ) {
    }

}
