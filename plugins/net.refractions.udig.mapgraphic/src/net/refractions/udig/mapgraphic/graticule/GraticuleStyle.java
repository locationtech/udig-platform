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
package net.refractions.udig.mapgraphic.graticule;

import java.awt.Color;

import net.refractions.udig.ui.graphics.ViewportGraphics;

/**
 * Style for the {@link GridMapGraphic}.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class GraticuleStyle {

    public enum Type {
        SCREEN, WORLD
    }

    public static final String ID = "net.refractions.udig.tool.edit.mapgraphic.grid.style"; //$NON-NLS-1$
    public static final GraticuleStyle DEFAULT_STYLE = new GraticuleStyle(Type.SCREEN, 25, 25, new Color(0,
            0, 255, 100), ViewportGraphics.LINE_DOT, 1);

    private double[] gridSize;
    private Type type;
    private Color color;
    private boolean centerGrid;
    private boolean showLabels;
    
    /**
     * One of
     * <ul>
     * <li>{@link ViewportGraphics#LINE_DASH},</li>
     * <li>{@link ViewportGraphics#LINE_DASHDOT}, </li>
     * <li>{@link ViewportGraphics#LINE_DASHDOTDOT}, </li>
     * <li>{@link ViewportGraphics#LINE_DOT}, </li>
     * <li>{@link ViewportGraphics#LINE_SOLID} </li>
     */
    private int lineStyle;
    /**
     * Width of the grid line in pixels
     */
    private int lineWidth;

    public GraticuleStyle( Type type, double dx, double dy, Color color, int lineStyle, int lineWidth ) {
        gridSize = new double[]{dx, dy};
        this.type = type;
        this.color = color;
        this.lineStyle = lineStyle;
        this.lineWidth = lineWidth;
        this.centerGrid = false;
        this.showLabels = false;
    }

    public GraticuleStyle( GraticuleStyle oldStyle ) {
        gridSize = oldStyle.getGridSize();
        type = oldStyle.getType();
        color = oldStyle.getColor();
        lineStyle = oldStyle.getLineStyle();
        lineWidth = oldStyle.getLineWidth();
        centerGrid = oldStyle.isCenterGrid();
        showLabels = oldStyle.isShowLabels();
    }

    /**
     * Returns a copy of the grid size array - use setGridSize to modify
     *
     * @return copy of grid size array
     */
    public double[] getGridSize() {
        return new double[]{ gridSize[0], gridSize[1] };
    }

    public Type getType() {
        return type;
    }

    public void setType( Type type ) {
        this.type = type;
    }

    public void setGridSize( double dx, double dy ) {
        this.gridSize[0] = dx;
        this.gridSize[1] = dy;
    }

    public Color getColor() {
        return color;
    }

    public void setColor( Color color ) {
        this.color = color;
    }

    public int getLineStyle() {
        return lineStyle;
    }

    public void setLineStyle( int lineStyle ) {
        this.lineStyle = lineStyle;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth( int lineWidth ) {
        this.lineWidth = lineWidth;
    }

	public boolean isCenterGrid() {
		return centerGrid;
	}

	public void setCenterGrid(boolean centerGrid) {
		this.centerGrid = centerGrid;
	}

	public boolean isShowLabels() {
		return showLabels;
	}

	public void setShowLabels(boolean showLabels) {
		this.showLabels = showLabels;
	}

}
