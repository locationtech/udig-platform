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

/**
 * A super simple representation of the units we use in the scalebar
 * <p>
 * This should be replaced by use of the java.units.Unit package.
 * @author jesse
 * @since 1.1.0
 */
public enum Unit {
    KILOMETER("km", 1000), //$NON-NLS-1$
    METER("m", 1), //$NON-NLS-1$
    CENTIMETER("cm", .01), //$NON-NLS-1$
    YARD("yards",0.9144), //$NON-NLS-1$
    FOOT("feet", 0.3048), //$NON-NLS-1$
    INCHES("in", 2.54 * 0.01), //$NON-NLS-1$
    MILE("mile", 1.6093 * 1000); //$NON-NLS-1$
    
    
    public String display;
    private double toMeter;

    private Unit(String display, double toMeter){
        if( toMeter==0 ){
            throw new IllegalArgumentException("Can't be 0"); //$NON-NLS-1$
        }
        this.display = display;
        this.toMeter = toMeter;
    }

    /**
     * Converts the value to meters.  Assumes the value is in the same unit as provided
     *
     * @param value the value to convert
     * @return value in meters
     */
    public double unitToMeter(double value) {
        return value*toMeter;
    }

    /**
     * Converts the value from meters to current unit.  Assumes the value is in meters
     *
     * @param value the value in meters 
     * @return value in current unit
     */
    public double meterToUnit(double value) {
        return value/toMeter;
    }
}
