package net.refractions.udig.mapgraphic.scalebar;

import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;

/**
 * 
 * Configuration bean for ScaleDenomMapGraphic 
 * <p>
 *
 * </p>
 * @author brocka
 * @since 1.1.0
 */
public class ScaleDenomMapGraphicBean {

    /**
     * Style Blackboard key
     */
    public static final String KEY = "net.refractions.udig.mapgraphic.scalebar.ScaleDenomMapGraphic"; //$NON-NLS-1$
    
    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_RIGHT = 1;
    public static final int ALIGN_CENTER = 2;
    
    private Font font;
    private int width;
    private int height;
    private Color textColor;
    private Color backgroundColor;
    private NumberFormat numberFormat;
    private String label;
    private int horizAlignment;


    public ScaleDenomMapGraphicBean() {
        label = ""; //$NON-NLS-1$
        horizAlignment = ALIGN_CENTER;
    }
    
    public String getLabel() {
        return label;
    }

    public void setLabel( String label ) {
        this.label = label;
    }

    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    public void setNumberFormat( NumberFormat numberFormat ) {
        this.numberFormat = numberFormat;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth( int width ) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight( int height ) {
        this.height = height;
    }

    public Font getFont() {
        return font;
    }

    public void setFont( Font font ) {
        this.font = font;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor( Color textColor ) {
        this.textColor = textColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor( Color backgroundColor ) {
        this.backgroundColor = backgroundColor;
    }

    public int getHorizAlignment() {
        return horizAlignment;
    }

    public void setHorizAlignment( int horizAlignment ) {
        this.horizAlignment = horizAlignment;
    }
    
    
    
}
