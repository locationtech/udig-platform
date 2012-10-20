/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
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

import net.refractions.udig.mapgraphic.MapGraphicContext;
import net.refractions.udig.mapgraphic.style.FontStyle;
import net.refractions.udig.mapgraphic.style.FontStyleContent;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IStyleBlackboard;
import net.refractions.udig.ui.graphics.ViewportGraphics;

/**
 * Style for the {@link GridMapGraphic}.
 * 
 * @author kengu
 * @since 1.3.3
 */
public class GraticuleStyle {

    /**
     * {@link GraticuleStyle} id.
     * 
     * @see {@link ILayer#getStyleBlackboard()}
     * @see {@link IStyleBlackboard#get(String)}
     */
    public static final String ID = "net.refractions.udig.tool.edit.mapgraphic.graticule.style"; //$NON-NLS-1$

    /**
     * Default {@link GraticuleStyle style}
     */
    public static final GraticuleStyle DEFAULT = new GraticuleStyle(
            new Color(0, 180, 255, 100), 
            new Color(0, 180, 255, 100),
            100, ViewportGraphics.LINE_SOLID, 1, true, true);

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
     * Graticule font {@link Color}
     */
    private Color fontColor;
    
    
    /**
     * Flag controlling CRS initialization;
     */
    private Boolean isInitCRS;
    
    /**
     * Constructor.
     * 
     * @param fontColor - Graticule font {@link Color}
     * @param lineColor - Graticule line {@link Color}
     * @param opacity - Graticule opacity (0-255)
     * @param lineStyle - Graticule line style
     * @param lineWidth - Graticule line widht
     * @param isShowLabels - Flag controlling graticule state
     * @param isInitCRS - Flag controlling graticule CRS initialization
     */
    public GraticuleStyle(
            Color fontColor, 
            Color lineColor, 
            int opacity, 
            int lineStyle, 
            int lineWidth, 
            Boolean isShowLabels,
            Boolean isInitCRS) {
        
        this.opacity = opacity;
        this.fontColor = fontColor;
        this.lineColor = lineColor;
        this.lineStyle = lineStyle;
        this.lineWidth = lineWidth;
        this.isShowLabels = isShowLabels;
        this.isInitCRS = isInitCRS;
    }

    /**
     * Copy constructor.
     * 
     * @param oldStyle - copy style from this.
     */
    public GraticuleStyle(GraticuleStyle oldStyle) {
        opacity = oldStyle.getOpacity();
        fontColor = oldStyle.getFontColor();
        lineColor = oldStyle.getLineColor();
        lineStyle = oldStyle.getLineStyle();
        lineWidth = oldStyle.getLineWidth();
        isShowLabels = oldStyle.isShowLabels();
        isInitCRS = oldStyle.isInitCRS();
    }

    public Color getFontColor() {
        return new Color(fontColor.getRed(),fontColor.getGreen(),fontColor.getBlue(),getOpacity());
    }

    public void setFontColor(Color color) {
        this.fontColor = color;
    }
    
    public Color getLineColor() {
        return new Color(lineColor.getRed(),lineColor.getGreen(),lineColor.getBlue(),getOpacity());
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
    
    /**
     * Get {@link GraticuleStyle style} from {@link ILayer#getStyleBlackboard()}.
     * <p>
     * If not found, {@link GraticuleStyle#DEFAULT} is used.
     * @param layer {@link ILayer}
     * @return {@link GraticuleStyle}
     */
    public static final GraticuleStyle getStyle( ILayer layer ) {
        GraticuleStyle style = (GraticuleStyle) layer.getStyleBlackboard().get(GraticuleStyle.ID);
        if( style==null ){
            return GraticuleStyle.DEFAULT;
        }
        return style;
    }
    
    /**
     * Get {@link FontStyle style} from {@link ILayer#getStyleBlackboard()}.
     * <p>
     * If not found, a new font style is instantiated.
     * @param layer {@link ILayer}
     * @return {@link FontStyle}
     */
    public static FontStyle getFontStyle( MapGraphicContext context ) {
        IStyleBlackboard styleBlackboard = context.getLayer().getStyleBlackboard();
        FontStyle style = (FontStyle) styleBlackboard.get(FontStyleContent.ID);
        if (style == null) {
            style = new FontStyle();
            styleBlackboard.put(FontStyleContent.ID, style);
        }
        return style;
    }    

}
