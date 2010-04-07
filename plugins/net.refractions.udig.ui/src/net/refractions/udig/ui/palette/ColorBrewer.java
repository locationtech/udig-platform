/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.ui.palette;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Class gathering up the ColorBrewer palettes.
 * <p>
 * 
 * </p>
 * @see http://www.personal.psu.edu/faculty/c/a/cab38/ColorBrewerBeta2.html 
 * @see http://jira.codehaus.org/browse/UDIG-204 
 * @see http://cvs.sourceforge.net/viewcvs.py/geovistastudio/geovistastudio/GeoVista/colorbrewer/src/edu/psu/geovista/colorbrewer/ColorSpecifics.java?rev=1.5&view=auto 
 * 
 * @author Biliang Zhou, GeoVISTA Center (Penn State, Dept. of Geography)
 * @author Jody Garnett, Refractions Research
 * @deprecated org.geotools.brewer.color.ColorBrewer
 * @since 0.6.0
 */
public final class ColorBrewer {    
    /** <code>BLUES</code> field */
    final public static Palette BLUES = pal( "blues" ); //$NON-NLS-1$
    
    /** <code>BUGN</code> field */
    final public static Palette BUGN = pal( "BuGn" ); //$NON-NLS-1$
    
    /** <code>BRBG</code> field */
    final public static Palette BRGB = pal( "BrBG" ); //$NON-NLS-1$
    
    final static SortedSet<Palette> sequential(){
        return set( EnumSet.of( SchemeType.SEQUENTIAL ));
    }
    final static SortedSet<Palette> qualitative(){
        return set( EnumSet.of( SchemeType.QUALITATIVE ));
    }
    final static SortedSet<Palette> diverging(){
        return set( EnumSet.of( SchemeType.DIVERGING ));
    }
    /**
     * Finds the palette that is closest to the provided color.
     */
    final static Palette sequential( Color color ){
        Palette best = null;
        double distance = Double.MAX_VALUE;
        
        for( Palette pal : sequential() ){
            double score = 0;
            for( Scheme scheme : pal.contents ){
                for( Color c: scheme.colors ){
                    score += distance( color, c );
                    
                }
            }
            if( score < distance ) {
                best = pal;
                distance = score;
            }
        }
        return best;        
    }
    
    /**
     * Distance between colors (between 0.0 and 1.0 ).
     * <p>
     * A magic function where:
     * <ul>
     * <li>max of difference of saturation, hue, etc the distance
     * <li>aka Black-White is the max distance of 1.0
     * <li>aka Blue=Blue is 0 distance
     * <li> 
     * </ul>
     * I am sure some researcher somewhere is laughing.
     * </p>
     * 
     * @param a
     * @param b
     * 
     * @return distance such that 0 implys a equals b
     */
    public static final double distance( Color a, Color b ){
        if( a.equals( b )) return 0.0;
        
        // compare distance in HSB space - because it is the right thing to do
        float hsb1[] = Color.RGBtoHSB( a.getRed(), a.getGreen(), a.getBlue(), null );
        float hsb2[] = Color.RGBtoHSB( b.getRed(), b.getGreen(), b.getBlue(), null );
        
        return Math.max(      Math.abs( hsb1[0] - hsb2[0] ),
                    Math.max( Math.abs( hsb1[1] - hsb2[1] ),
                              Math.abs( hsb1[2] - hsb2[2] )
                    )
               );
    }
    /**
     * Used to query ColorBrewer for Palette(s) of the provided type.
     * <p>
     * Example:
     * <ul>
     * <li>ColorBrewer.create().set( EnumSet.of( SchemeType.SEQUENTIAL ) );
     * </ul>
     * </p>
     * 
     * @return set of Palette of provided type
     */
    private static SortedSet<Palette> set( EnumSet<SchemeType> types ){
        SortedSet<Palette> sorted = new TreeSet<Palette>(new Comparator<Palette>(){
            public int compare( Palette a, Palette b ) {                
                return a.name.compareTo( b.name );
            }            
        });
        for( Field field : ColorBrewer.class.getFields() ){
            if( field.getType() == Palette.class){
                try {
                    Palette pal = (Palette) field.get( null );
                    if( pal != null && types.contains( pal.type ) ){
                        sorted.add( pal );
                    }
                } catch (IllegalArgumentException e) {
                    // ignore
                } catch (IllegalAccessException e) {
                    // ignore
                }
            }
        }
        return sorted;
    }    
    /** Load pal file */
    static final Palette pal( String palette ){
        try {
            return new Palette( palette+".pal" ); //$NON-NLS-1$
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        } 
    }        
}
