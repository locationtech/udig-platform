/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.mapgraphic.grid;

import java.awt.Color;

import org.locationtech.udig.ui.graphics.ViewportGraphics;

/**
 * Style for the {@link GridMapGraphic}.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class GridStyle {

    public enum Type {
        SCREEN, WORLD
    }

    public static final String ID = "org.locationtech.udig.tool.edit.mapgraphic.grid.style"; //$NON-NLS-1$
    public static final GridStyle DEFAULT_STYLE = new GridStyle(Type.SCREEN, 25, 25, new Color(0,
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

    public GridStyle( Type type, double dx, double dy, Color color, int lineStyle, int lineWidth ) {
        gridSize = new double[]{dx, dy};
        this.type = type;
        this.color = color;
        this.lineStyle = lineStyle;
        this.lineWidth = lineWidth;
        this.centerGrid = false;
        this.showLabels = false;
    }

    public GridStyle( GridStyle oldStyle ) {
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
