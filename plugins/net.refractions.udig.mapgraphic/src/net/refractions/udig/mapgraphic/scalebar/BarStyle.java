/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
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
package net.refractions.udig.mapgraphic.scalebar;

import java.awt.Color;

import net.refractions.udig.mapgraphic.MapGraphicPlugin;
import net.refractions.udig.mapgraphic.internal.Messages;
import net.refractions.udig.mapgraphic.internal.ui.ImageConstants;
import net.refractions.udig.project.internal.StyleBlackboard;

import org.eclipse.swt.graphics.Image;

/**
 * A style that representing the type of bar to draw, the color, and the number of intervals to
 * display in the bar.
 * 
 * @see StyleBlackboard
 * @see BarStyleContent
 * @author egouge
 * @since 1.1.0
 */
public class BarStyle {

    /** minimum/maximum divisions */
    public static final int MINIMUM_DIVISIONS = 2;
    public static final int MAXIMUM_DIVISIONS = 20;
    public static final int DIVISION_INCREMENT = 1;
    
    /**
     * <p>
     * Tracks the different types of scale bars.
     * </p>
     * 
     * @author egouge
     * @since 1.1.0
     */
    public enum BarType {
        SIMPLE(ImageConstants.SCALEBAR_TYPE_SIMPLE, Messages.BarStyle_LabelSimple), SIMPLE_LINE(ImageConstants.SCALEBAR_TYPE_SIMPLE_LINE, Messages.BarStyle_LabelLine),  
        FILLED(ImageConstants.SCALEBAR_TYPE_FILLED, Messages.BarStyle_LabelFilled), FILLED_LINE(ImageConstants.SCALEBAR_TYPE_FILLED_LINE, Messages.BarStyle_LabelFilledLine); 

        public final String imageName;
        public final String name;

        BarType( String imageName, String name ) {
            this.imageName = ImageConstants.ICONS_PATH + ImageConstants.BARTYPE_PATH + imageName;
            this.name = name;
        }

        public Image getImage() {
            return MapGraphicPlugin.getDefault().getImageRegistry().getDescriptor(imageName)
                    .createImage();
        }
        public String getName(){
            return this.name;
        }
    }

    private Color color = Color.BLACK;
    private Color bgColor = null;
    private int numintervales = 4;
    private BarType type = BarType.SIMPLE;
    
    private UnitPolicy units = UnitPolicy.determineDefaultUnits();
    
    /**
     * Creates a new default bar style.
     */
    public BarStyle() {
    }

    /**
     * Creates a new scale with the provided parameters
     * 
     * @param type the type of scale bar
     * @param color the color of the scale bar
     * @param numintervals the number of intervals to display in the scale bar
     * @param units - METRIC/IMPERIAL
     */
    public BarStyle( BarType type, Color color, int numintervals, UnitPolicy units ) {
        this.type = type;
        this.color = color;
        this.numintervales = numintervals;
        this.units = units;
    }

    /**
     * Returns the color.
     * 
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Returns the number of intervals to divide the scale bar into.
     * 
     * @return the number of intervals to divide the scale bar into
     */
    public int getNumintervals() {
        return numintervales;
    }

    /**
     * Returns the type of scale bar to be drawn.
     * 
     * @return the type of scale bar to be drawn.
     */
    public BarType getType() {
        return type;
    }

    /**
     * Returns the UnitPolicy used when displaying the scale bar
     * AUTO, IMPERIAL or METRIC
     *
     * @return UnitPolicy used when displayin gthe scale bar
     */
    public UnitPolicy getUnits(){
        return this.units;
    }
    /**
     * Sets the type of scale bar to be drawn.
     * 
     * @param type new scale bar type
     */
    public void setType( BarType type ) {
        this.type = type;
    }
    
    /**
     * Sets the color.
     * 
     * @param c the new color
     */
    public void setColor( Color c ) {
        this.color = c;
    }
    
    /**
     * Sets the units.
     *
     * @param newUnits
     */
    public void setUnits(UnitPolicy newUnits){
        this.units = newUnits;
    }
    /**
     * Sets the number of intervals
     * 
     * @param intervals new number of intervals
     */
    public void setNumIntervals( int intervals ) {
        this.numintervales = intervals;
    }

    /**
     * Returns all possible scale bar type names.
     * 
     * @return
     */
    public static BarType[] getTypes() {
        return new BarType[]{BarType.SIMPLE, BarType.SIMPLE_LINE, BarType.FILLED,
                BarType.FILLED_LINE};
    }

    public Color getBgColor() {
        return bgColor;
    }

    public void setBgColor( Color bgColor ) {
        this.bgColor = bgColor;
    }

}
