/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.internal.commands.draw;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.PathIterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Path;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.ui.commands.AbstractDrawCommand;
import org.locationtech.udig.ui.graphics.AWTSWTImageUtils;
import org.locationtech.udig.ui.graphics.ViewportGraphics;

/**
 * Draws the outline of a shape on the Acetate layer.
 * 
 * @author jeichar
 * @since 0.3
 */
public class DrawPathCommand extends AbstractDrawCommand {

    Path path = null;
    Color paint = null;
    private int style = -1;
    private int width;
    private Color fill;
    private boolean closeShape;
    private int closex1;
    private int closex2;
    private int closey1;
    private int closey2;

    /**
     * Creates a new instance of DrawShapeCommand
     */
    public DrawPathCommand() {
        this((Path)null, Color.BLACK, ViewportGraphics.LINE_SOLID, 1);
    }

    /**
     * Creates a new instance of DrawShapeCommand
     * 
     * @param path The path to draw
     * @param color The paint to draw the shape with.
     * @param lineStyle the line style to use for the shape outline
     * @param lineWidth the line width in pixels.
     */
    public DrawPathCommand( Path path, Color color, int lineStyle, int lineWidth ) {
        this.path = path;
        this.paint = color;
        this.style = lineStyle;
        this.width = lineWidth;
    }

    /**
     * Creates a new instance of DrawShapeCommand
     * 
     * @param path The path to draw
     * @param color The paint to draw the shape with.
     * @param lineStyle the line style to use for the shape outline
     * @param lineWidth the line width in pixels.
     */
    public DrawPathCommand( Device device, PathIterator path, Color color, int lineStyle, int lineWidth ) {
        this.paint = color;
        this.style = lineStyle;
        this.width = lineWidth;
        this.path=AWTSWTImageUtils.createPath(path, device);
    }

    /**
     * @see MapCommand#run()
     */
    public void run( IProgressMonitor monitor ) {
		if( path==null )
			return;
		fill();
            
        draw();
        
        close();
    }

    private void draw() {
        if( paint!=null || fill==null  ){
            if (paint != null)
                graphics.setColor(paint);
            if (style > -1)
                graphics.setStroke(style, width);
            if( path!=null )
                graphics.drawPath(path);
        }

    }

    private void close() {
        if( closeShape )
            graphics.drawLine(closex1, closey1, closex2, closey2);
    }

    private void fill() {
        if( fill!=null ){
            graphics.setColor(fill);
            if( path!=null )
                graphics.fillPath(path);
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
     * @param path The new path.
     */
    public void setPath( Path path ) {
        this.path = path;
    }
    /**
     * @param path The new path.
     */
    public void setPath( Device device, PathIterator path ) {
        this.path=AWTSWTImageUtils.createPath(path, device);
        
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
        if( path!=null ){
            float[] bounds=new float[4];
            path.getBounds(bounds);
            return new Rectangle((int)bounds[0], (int)bounds[1], (int)bounds[2], (int)bounds[3]);
        }
        return null;
    }

    /**
     * Disposes of the Path object if it is not null and sets this command as invalid.
     *
     */
    public void dispose() {
        if(path!=null){
            path.dispose();
            path=null;
        }
        super.setValid(false);
    }

    @Override
    public void setValid( boolean valid ) {
        if( !valid ){
            dispose();
        }else{
            super.setValid(true);
        }
    }
    
    /**
     * if this is called then a line is also drawn from (x1,y1) to (x2,y2).  This would be used to make the path appear to be closed.  Actually closing
     * the path is problematic because it cannot be undone.  So this is a method to simulate it.  Obviously if the path is not going to need to be re-opened 
     * then this method does not need to be called.
     *
     */
    public void line( int x1, int y1, int x2, int y2 ) {
        closeShape=true;
        this.closex1=x1;
        this.closex2=x2;
        this.closey1=y1;
        this.closey2=y2;
    }
}
