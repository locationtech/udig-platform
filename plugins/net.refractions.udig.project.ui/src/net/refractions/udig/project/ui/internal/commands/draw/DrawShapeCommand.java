/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.project.ui.internal.commands.draw;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.ui.commands.AbstractDrawCommand;
import net.refractions.udig.ui.graphics.AWTSWTImageUtils;
import net.refractions.udig.ui.graphics.SWTGraphics;
import net.refractions.udig.ui.graphics.ViewportGraphics;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.widgets.Display;

/**
 * Draws the outline of a shape on the Acetate layer.
 * 
 * @author jeichar
 * @since 0.3
 */
public class DrawShapeCommand extends AbstractDrawCommand {

    private Path path = null;
    private Shape shape = null;
    private Color paint = null;
    private int style = -1;
    private int width;
    private Color fill;

    /**
     * Creates a new instance of DrawShapeCommand
     */
    public DrawShapeCommand() {
        this(null, Color.BLACK, ViewportGraphics.LINE_SOLID, 1);
    }

    /**
     * Creates a new instance of DrawShapeCommand
     * 
     * @param shape The shape to draw
     * @param color The paint to draw the shape with.
     * @param lineStyle the line style to use for the shape outline
     * @param lineWidth the line width in pixels.
     */
    public DrawShapeCommand( Shape shape, Color color, int lineStyle, int lineWidth ) {
        setShape(shape);
        this.paint = color;
        this.style = lineStyle;
        this.width = lineWidth;

    }

    /**
     * @see MapCommand#run()
     */
    public void run( IProgressMonitor monitor ) {
		if( shape==null )
    			return;
		if( fill!=null ){
            graphics.setColor(fill);
            graphics.fill(shape);
        }
            
        if (paint != null)
            graphics.setColor(paint);
        if (style > -1)
            graphics.setStroke(style, width);
        doDraw();
    }

    private void doDraw() {
        if( shape instanceof Rectangle2D ){
            Rectangle2D rect=(Rectangle2D) shape;
            graphics.drawRect((int)rect.getX(), (int)rect.getY(), 
                    (int)rect.getWidth(), (int)rect.getHeight() );
            return;
        }
        
        if( shape instanceof Ellipse2D){
            Ellipse2D ellipse=(Ellipse2D) shape;
            graphics.drawOval((int)ellipse.getMinX(), (int)ellipse.getMinY(), 
                    (int)ellipse.getWidth(), (int)ellipse.getHeight());
            return;
        }
        
        if( graphics instanceof SWTGraphics ){
	        if( path==null || path.isDisposed() )
	            path=AWTSWTImageUtils.createPath(shape.getPathIterator(new AffineTransform()),
	                    Display.getCurrent());
	        graphics.drawPath(path);
        }else{
        	graphics.draw(shape);
        }
    }

    /**
     * @return Returns the paint.
     */
    public Paint getPaint() {
        return paint;
    }
    /**
     * @param paint The paint to set.
     */
    public void setPaint( Color paint ) {
        this.paint = paint;
    }
    /**
     * @return Returns the shape but it is a copy so changing won't affect actual rendering.
     */
    public Shape getShape() {
        return shape;
    }
    /**
     * @param shape The shape to set.
     */
    public void setShape( Shape shape ) {
        this.shape = shape;
        path=null;
    }
    /**
     * @return Returns the line style.
     */
    public int getLineStyle() {
        return style;
    }
    /**
     * @return Returns the line width.
     */
    public int getLineWidth() {
        return width;
    }

    /**
     * Sets the line style
     * 
     * @param lineStyle the style of the line
     * @param lineWidth the width of the line
     */
    public void setStroke( int lineStyle, int lineWidth ) {
        this.style = lineStyle;
        this.width = lineWidth;
    }

    /**
     * Sets the color that the shape will be filled with.  
     * If fill is null then no fill will be applied.
     *  
     * @param fillColor a color to be used to fill the shapeor null.
     */
    public void setFill( Color fillColor ) {
        this.fill=fillColor;
    }

    public Rectangle getValidArea() {
        if(shape==null)
            return null;
        return shape.getBounds();
    }
    
    @Override
    public void setValid( boolean valid ) {
        super.setValid(valid);
     }
    
    @Override
    public void dispose() {
        if( path!=null && !path.isDisposed() )
            path.dispose();
    }
}
