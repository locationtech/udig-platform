/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.ui.palette;

import java.util.EnumSet;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.geotools.util.SimpleInternationalString;
import org.opengis.util.InternationalString;

/**
 * Captures the information provided by the ColorBrewer icons.
 * <p>
 * <ul>
 * <li>ColorBlind
 * <li>PhotoCopy
 * <li>LCD Projector
 * <li>LCD
 * <li>CRT
 * <li>Color Printing
 * </ul>
 * </p>
 * 
 * @see http://www.personal.psu.edu/faculty/c/a/cab38/ColorBrewer/ColorBrewer_learnMore.html
 * @author jgarnett
 * @since 0.6.0
 * @deprecated
 */
public enum Friendly {
    /**
     * Will not confuse people with red-green color blindness. Red-green color blindness affects
     * approximately 8 percent of men and 0.4 percent of women.
     */
    COLORBLIND,

    /**
     * Will withstand black and white photocopying. Diverging schemes can not be photocopied
     * successfully. Differences in lightness should be preserved with sequential schemes.
     */
    PHOTOCOPY,

    /** <code>PROJECTOR</code> field */
    PROJECTOR,

    /**
     * Suitable for the typical LCD room projector. LCD projectors have a tendency to 'wash-out'
     * colors resulting in pastel and pale colors looking the same (i.e. white).
     */
    LCD,

    /**
     * Suitable for viewing on a laptop LCD display. LCD monitors tend to wash-out colors which
     * results in noticeable differences from traditional CRT computer monitors.
     */
    CRT,

    /**
     * Suitable for color printing. CMYK specs are as close to press-ready as is reasonable.
     */
    PRINT;

    public final InternationalString description;
    public final InternationalString display;

    private Friendly() {
        this.display = string(name() + ".display"); //$NON-NLS-1$
        this.description = string(name() + ".description"); //$NON-NLS-1$
    }

    private static ResourceBundle bundle = null;
    /**
     * Gets a string from the resource bundle. We don't want to crash because of a missing String.
     * Returns the key if not found.
     * 
     * @param key the id to look up
     * @return the string with the given key
     */
    private static InternationalString string( String key ) {
        if( bundle == null ){
            try {
                bundle = ResourceBundle.getBundle("net.refractions.udig.ui.palette.friendly"); //$NON-NLS-1$
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        try {
            return new SimpleInternationalString(bundle.getString(key));
        } catch (MissingResourceException e) {
            return new SimpleInternationalString(key);
        } catch (NullPointerException e) {
            return new SimpleInternationalString("!" + key + "!"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
    /** 
     * Parse text of the form: <code>colorblind, lcd, crt</code>.
     * 
     * @param text
     * @return EnumSet (may be empty)
     */
    public static EnumSet<Friendly> parse( String text ){
        EnumSet<Friendly> set = EnumSet.noneOf( Friendly.class );        
        if( text == null ) return set; 
        for( String symbol :  text.split("," ) ){ //$NON-NLS-1$
            symbol = symbol.trim();
            if( symbol.length() == 0 ) continue;
            try{ 
                set.add( Friendly.valueOf( symbol.toUpperCase() ));
            }
            catch( IllegalArgumentException badSymbol ){
                System.out.println( badSymbol );                
            }
        }
        return set;
    }
}
