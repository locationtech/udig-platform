/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.mapgraphic.graticule;

import java.awt.Color;

import org.locationtech.udig.mapgraphic.MapGraphicContext;
import org.locationtech.udig.mapgraphic.style.FontStyle;
import org.locationtech.udig.mapgraphic.style.FontStyleContent;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IStyleBlackboard;
import org.locationtech.udig.ui.graphics.ViewportGraphics;

/**
 * Style for the {@link GridMapGraphic}.
 * 
 * @author kengu
 * @since 1.3.3
 */
public class GraticuleStyle {

    private static final String EPSG_4326 = "EPSG:4326"; //$NON-NLS-1$

    /**
     * {@link GraticuleStyle} id.
     * 
     * @see {@link ILayer#getStyleBlackboard()}
     * @see {@link IStyleBlackboard#get(String)}
     */
    public static final String ID = "org.locationtech.udig.tool.edit.mapgraphic.graticule.style"; //$NON-NLS-1$

    /**
     * Default {@link GraticuleStyle style}
     */
    //new Color(0, 180, 255, 100),
    public static final GraticuleStyle DEFAULT = new GraticuleStyle(
            new Color(0, 180, 255, 100), 100, 
            ViewportGraphics.LINE_SOLID, 1, true, true, EPSG_4326);

    /**
     * Graticule opacity (0-255)
     */
    private int opacity;

    /**
     * Graticule line {@link Color}
     */
    private Color lineColor;

    /**
     * Graticule line style. One of
     * <ul>
     * <li>{@link ViewportGraphics#LINE_DASH},</li>
     * <li>{@link ViewportGraphics#LINE_DASHDOT},</li>
     * <li>{@link ViewportGraphics#LINE_DASHDOTDOT},</li>
     * <li>{@link ViewportGraphics#LINE_DOT},</li>
     * <li>{@link ViewportGraphics#LINE_SOLID}</li>
     * </ul>
     */
    private int lineStyle;

    /**
     * Graticule line widths (pixels)
     */
    private int lineWidth;

    /**
     * Flag controlling label state
     */
    private Boolean isShowLabels;

    /**
     * Flag controlling CRS initialization;
     */
    private Boolean isInitCRS;

    /**
     * CRS definition
     */
    private String crs;

    /**
     * Constructor.
     * 
     * @param lineColor - Graticule line {@link Color}
     * @param opacity - Graticule opacity (0-255)
     * @param lineStyle - Graticule line style
     * @param lineWidth - Graticule line widht
     * @param isShowLabels - Flag controlling graticule state
     * @param isInitCRS - Flag controlling graticule CRS initialization
     */
    public GraticuleStyle( Color lineColor, int opacity, int lineStyle,
            int lineWidth, Boolean isShowLabels, Boolean isInitCRS, String crs) {

        this.opacity = opacity;
        this.lineColor = lineColor;
        this.lineStyle = lineStyle;
        this.lineWidth = lineWidth;
        this.isShowLabels = isShowLabels;
        this.isInitCRS = isInitCRS;
        this.crs = crs;
    }

    /**
     * Copy constructor.
     * 
     * @param oldStyle - copy style from this.
     */
    public GraticuleStyle(GraticuleStyle oldStyle) {
        opacity = oldStyle.getOpacity();
        lineColor = oldStyle.getLineColor();
        lineStyle = oldStyle.getLineStyle();
        lineWidth = oldStyle.getLineWidth();
        isShowLabels = oldStyle.isShowLabels();
        isInitCRS = oldStyle.isInitCRS();
        crs = oldStyle.getCRS();
    }

    public Color getLineColor() {
        return new Color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(),
                getOpacity());
    }

    public void setLineColor(Color color) {
        this.lineColor = color;
    }

    public int getLineStyle() {
        return lineStyle;
    }

    public void setLineStyle(int lineStyle) {
        this.lineStyle = lineStyle;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public int getOpacity() {
        return opacity;
    }

    public void setOpacity(int opacity) {
        this.opacity = opacity;
    }

    public boolean isShowLabels() {
        return isShowLabels != null ? isShowLabels : false;
    }

    public void setShowLabels(boolean isShowLabels) {
        this.isShowLabels = isShowLabels;
    }

    public boolean isInitCRS() {
        return isInitCRS != null ? isInitCRS : true;
    }

    public void setInitCRS(boolean isInitCRS) {
        this.isInitCRS = isInitCRS;
    }

    public String getCRS() {
        return crs != null ? crs : EPSG_4326;
    }

    public void setCRS(String crs) {
        this.crs = crs;
    }

    /**
     * Get {@link GraticuleStyle style} from {@link ILayer#getStyleBlackboard()}.
     * <p>
     * If not found, {@link GraticuleStyle#DEFAULT} is used.
     * 
     * @param layer {@link ILayer}
     * @return {@link GraticuleStyle}
     */
    public static final GraticuleStyle getStyle(ILayer layer) {
        GraticuleStyle style = (GraticuleStyle) layer.getStyleBlackboard().get(GraticuleStyle.ID);
        if (style == null) {
            return GraticuleStyle.DEFAULT;
        }
        return style;
    }

    /**
     * Get {@link FontStyle style} from {@link ILayer#getStyleBlackboard()}.
     * <p>
     * If not found, a new font style is instantiated.
     * 
     * @param layer {@link ILayer}
     * @return {@link FontStyle}
     */
    public static FontStyle getFontStyle(MapGraphicContext context) {
        IStyleBlackboard styleBlackboard = context.getLayer().getStyleBlackboard();
        FontStyle style = (FontStyle) styleBlackboard.get(FontStyleContent.ID);
        if (style == null) {
            style = new FontStyle();
            style.setColor(new Color(0, 180, 255, 100));
            styleBlackboard.put(FontStyleContent.ID, style);
        }
        return style;
    }

}
