/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.sld.raster.internal;

import java.awt.Color;

import org.eclipse.swt.SWT;
import org.eclipse.ui.IMemento;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.Symbolizer;

/**
 * Seems to be a mess of defaults?
 * 
 * @author jgarnett
 * @since 0.6.0
 */
public class SymbolizerContent {
    //   
    /** <code>ID</code> field */
    public static final String ID = "org.locationtech.udig.style.sld.symbolizer"; //$NON-NLS-1$

    static final String TAG_TYPE = "featureType"; //$NON-NLS-1$
    static final String TAG_LABEL = "text_label"; //$NON-NLS-1$
    static final String TAG_FONT = "text_font"; //$NON-NLS-1$
    static final String TAG_HALO = "text_halo"; //$NON-NLS-1$

    /** <code>DEFAULT_LINE_COLOR</code> field */
    public static final Color DEFAULT_LINE_COLOR = Color.BLUE;
    /** <code>DEFAULT_LINE_WIDTH</code> field */
    public static final int DEFAULT_LINE_WIDTH = 1;
    /** <code>DEFAULT_LINE_OPACITY</code> field */
    public static final float DEFAULT_LINE_OPACITY = 1.0f;
    /** <code>DEFAULT_LINE_LINEJOIN</code> field */
    public static final String DEFAULT_LINE_LINEJOIN = "mitre"; //$NON-NLS-1$
    /** <code>DEFAULT_LINE_LINECAP</code> field */
    public static final String DEFAULT_LINE_LINECAP = "butt"; //$NON-NLS-1$

    /** <code>DEFAULT_FILL_COLOR</code> field */
    public static final Color DEFAULT_FILL_COLOR = Color.GREEN;
    /** <code>DEFAULT_BORDER_COLOR</code> field */
    public static final Color DEFAULT_BORDER_COLOR = Color.BLACK;
    /** <code>DEFAULT_BORDER_WIDTH</code> field */
    public static final int DEFAULT_BORDER_WIDTH = 1;
    /** <code>DEFAULT_POLY_BORDER_OPACITY</code> field */
    public static final float DEFAULT_POLY_BORDER_OPACITY = 1.0f;
    /** <code>DEFAULT_POLY_FILL_OPACITY</code> field */
    public static final float DEFAULT_POLY_FILL_OPACITY = 1.0f;

    /** <code>DEFAULT_MARKER_COLOR</code> field */
    public static final Color DEFAULT_MARKER_COLOR = Color.RED;
    /** <code>DEFAULT_MARKER_WIDTH</code> field */
    public static final int DEFAULT_MARKER_WIDTH = 1;
    /** <code>DEFAULT_MARKER_TYPE</code> field */
    public static final String DEFAULT_MARKER_TYPE = StyleBuilder.MARK_CIRCLE;
    /** <code>DEFAULT_MARKER_BORDER_OPACITY</code> field */
    public static final float DEFAULT_MARKER_BORDER_OPACITY = 1.0f;
    /** <code>DEFAULT_MARKER_OPACITY</code> field */
    public static final float DEFAULT_MARKER_OPACITY = 1.0f;

    /** <code>DEFAULT_OPACITY</code> field */
    public static final float DEFAULT_OPACITY = 1.0f;

    /** <code>DEFAULT_FONT_COLOR</code> field */
    public static final Color DEFAULT_FONT_COLOR = Color.BLACK;
    /** <code>DEFAULT_FONT_FACE</code> field */
    public static final String DEFAULT_FONT_FACE = "Arial"; //$NON-NLS-1$
    /** <code>DEFAULT_FONT_SIZE</code> field */
    public static final int DEFAULT_FONT_SIZE = 10;
    /** <code>DEFAULT_FONT_STYLE</code> field */
    public static final int DEFAULT_FONT_STYLE = SWT.NORMAL;
    /** <code>DEFAULT_HALO_COLOR</code> field */
    public static final Color DEFAULT_HALO_COLOR = Color.GREEN;
    /** <code>DEFAULT_HALO_OPACITY</code> field */
    public static final float DEFAULT_HALO_OPACITY = 1.0f;
    /** <code>DEFAULT_HALO_WIDTH</code> field */
    public static final float DEFAULT_HALO_WIDTH = 1.0f;

    Class symbolizer;

    <T extends Symbolizer> SymbolizerContent( Class<T> symbolizer ) {
        this.symbolizer = symbolizer;
    }
    /**
     * Can we save this symbolizer?
     * 
     * @param sym
     * @return true if this TYPE manages the provided symbolizer
     */
    final boolean isManaged( Symbolizer sym ) {
        return symbolizer.isInstance(sym);
    }

    /**
     * Save provided symbolizer to the momento?
     * <p>
     * This delegates the actual saving to various content managers like SldPolygonContentManager.
     * </p>
     * 
     * @param momento
     * @param sym
     */
    void save( IMemento momento, Symbolizer sym ) {
        // momento.putInteger(TAG_TYPE, this.ordinal());
    }
    /**
     * Load previously saved symbolizer from a momento?
     * <p>
     * This delegates the actual load to various content managers like SldPolygonContentManager.
     * </p>
     * 
     * @param momento
     * @return Symbolizer
     */
    public Symbolizer load( IMemento momento ) {
        return null; // could not handle
    }

    /**
     * Create the default Style (w/ Symbolizer for this thing)
     * 
     * @return a style with some defaults set
     */
    public Style defaultStyle() {
        return null;
    }
}
