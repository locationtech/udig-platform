/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.edit.commands;

import java.awt.Color;

import net.refractions.udig.core.IProvider;
import net.refractions.udig.core.StaticProvider;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.ui.graphics.ViewportGraphics;

/**
 * This class manages the colors and line patterns the uses for drawing. There are different colors
 * for the fill and outline of the shapes that change depending on whether the current edit state is
 * create or modify (and the shape in question is the current shape"
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class StyleStrategy {
    private IProvider<Color> line = new StaticProvider<Color>(new Color(255, 255, 0));
    private IProvider<Color> fill = new StaticProvider<Color>(new Color(255, 255, 0, 100));
    private IProvider<Integer> lineWidth = new StaticProvider<Integer>(1);
    private IProvider<int[]> linePattern = new StaticProvider<int[]>(null);
    private IProvider<Color> line2 = new StaticProvider<Color>(new Color(0, 0, 0, 75));
    private IProvider<int[]> linePattern2 = new StaticProvider<int[]>(null);
    private IProvider<Integer> lineWidth2 = new StaticProvider<Integer>(3);

    /**
     * Sets the line color of the graphics
     * 
     */
    public void setLineColor( ViewportGraphics graphics, EditGeom geom, EditToolHandler handler ) {
        graphics.setColor(line.get());
        graphics.setLineWidth(lineWidth.get());
        graphics.setLineDash(linePattern.get());
    }

    /**
     * Sets the background line color and width.
     */
    public void setLineColor2( ViewportGraphics graphics, EditGeom geom, EditToolHandler handler ) {
        graphics.setColor(line2.get());
        graphics.setLineWidth(lineWidth2.get());
        graphics.setLineDash(linePattern2.get());
    }

    public void setFillColor( ViewportGraphics graphics, EditGeom geom, EditToolHandler handler ) {
        graphics.setColor(fill.get());
        graphics.setStroke(ViewportGraphics.LINE_SOLID, lineWidth.get());
    }

    
    /**
     * @param fill The fill to set.
     */
    public void setFill( Color fill ) {
        this.fill = new StaticProvider<Color>(fill);
    }
    /**
     * @param line The line to set.
     */
    public void setLine( Color line ) {
        this.line = new StaticProvider<Color>(line);
    }

    /**
     * @param fill The fill to set.
     */
    public void setFill( IProvider<Color> fill ) {
        this.fill = fill;
    }
    /**
     * @param line The line to set.
     */
    public void setLine( IProvider<Color> line ) {
        this.line = line;
    }

    /**
     * @param linePattern The linePattern to set.
     */
    public void setLinePattern( IProvider<int[]> linePattern ) {
        this.linePattern = linePattern;
    }

    /**
     * @param lineWidth The lineWidth to set.
     */
    public void setLineWidth( IProvider<Integer> lineWidth ) {
        this.lineWidth = lineWidth;
    }


    /**
     * @param line The line to set.
     */
    public void setBackgroundLine( IProvider<Color> line ) {
        this.line2 = line;
    }

    /**
     * @param linePattern The linePattern to set.
     */
    public void setBackgroundLinePattern( IProvider<int[]> linePattern ) {
        this.linePattern2 = linePattern;
    }

    /**
     * @param lineWidth The lineWidth to set.
     */
    public void setBackgroundLineWidth( IProvider<Integer> lineWidth ) {
        this.lineWidth2 = lineWidth;
    }

}
