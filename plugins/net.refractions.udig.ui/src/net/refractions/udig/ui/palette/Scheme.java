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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * A color schme from a palette.
 * 
 * @author Jody Garnett, Refractions Research
 * @since 0.6.0
 * @deprecated
 */
public class Scheme {
    
    /** List of Color aranged by length */ 
    public final List<Color> colors;
    
    /** <code>good</code> field */
    public final EnumSet<Friendly> good;
    
    /** <code>doubtful</code> field */
    public final EnumSet<Friendly> doubtful;

    /**
     * Construct <code>Scheme</code>.
     *
     * @param scheme
     */
    public Scheme( Color[] scheme ){
        this( scheme, new Friendly[]{ Friendly.CRT, Friendly.LCD }, new Friendly[]{Friendly.COLORBLIND, Friendly.PROJECTOR} );        
    }
    /**
     * Construct <code>Scheme</code>.
     *
     * @param scheme
     * @param good
     */
    public Scheme( Color[] scheme, Friendly good[] ){
        this( scheme, good, new Friendly[0] );        
    }
    
    /**
     * Construct <code>Scheme</code>.
     *
     * @param scheme
     */
    public Scheme( List<Color> scheme ){
        this( scheme,
              EnumSet.of(Friendly.CRT, Friendly.LCD ),
              EnumSet.of(Friendly.COLORBLIND, Friendly.PROJECTOR) );        
    }
    /**
     * Construct <code>Scheme</code>.
     *
     * @param scheme
     * @param good 
     * @param doubtful 
     */
    public Scheme( List<Color> scheme, EnumSet<Friendly> good, EnumSet<Friendly> doubtful  ){
        colors = scheme;
        this.good = good;
        this.doubtful = doubtful;
    }
    
    /**
     * Construct <code>Scheme</code>.
     *
     * @param scheme
     * @param good 
     * @param doubtful 
     */
    public Scheme( Color[] scheme, Friendly good[], Friendly doubtful[] ){
        this( colors( scheme ), friendly( good ), friendly( doubtful ) );        
    }
    
    /**
     * Construct <code>Scheme</code>.
     *
     * @param scheme
     * @param good
     * @param doubtful
     */
    public Scheme( int[][] scheme, int friendly[] ){
        this( colors( scheme ), good( friendly ), doubtful( friendly ) );
    }
    
    /*
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append( "<Scheme" );         //$NON-NLS-1$
        if( colors.size() != 0){
            if( colors.size() == 1){
                buf.append( colors.get(0 ) );
            }
            else {
                buf.append("("); //$NON-NLS-1$
                buf.append( colors.size() );
                buf.append("("); //$NON-NLS-1$
                buf.append( colors.get(0));
                buf.append(".."); //$NON-NLS-1$
                buf.append( colors.get(colors.size()-1));
            }
        }
        if( !good.isEmpty() ){
            buf.append(" good="); //$NON-NLS-1$
            buf.append( good );
        }
        if( !doubtful.isEmpty() ){
            buf.append(" doubtful="); //$NON-NLS-1$
            buf.append( doubtful );
        }
        buf.append(">"); //$NON-NLS-1$
        return buf.toString();
    }
    private static EnumSet<Friendly> friendly( Friendly good[] ){
        if( good != null ){
            return EnumSet.copyOf( Arrays.asList( good ) );
        }
        return (EnumSet<Friendly>) Collections.unmodifiableSet( EnumSet.noneOf( Friendly.class ) );        
    }
    
    private static List<Color> colors( Color[] scheme ){
        return Collections.unmodifiableList( Arrays.asList( scheme ));
    }
    /** One of GOOD, BAD, DOUBTFUL */
    private static EnumSet<Friendly> good( int[] friendly){
        EnumSet<Friendly> set = EnumSet.noneOf( Friendly.class );
        int i = 0;
        for( Friendly f : Friendly.values() ){
            if( friendly[i] == Palette.GOOD ){
                set.add( f );
            }
            i++;
        }
        return set;
    }
    /** One of GOOD, BAD, DOUBTFUL */
    private static EnumSet<Friendly> doubtful( int[] friendly){
        EnumSet<Friendly> set = EnumSet.noneOf( Friendly.class );
        int i = 0;
        for( Friendly f : Friendly.values() ){
            if( friendly[i] == Palette.DOUBTFUL ){
                set.add( f );
            }
            i++;
        }
        return set;
    }
    private static List<Color> colors( int [][] scheme ){
        List<Color> list = new ArrayList<Color>();
        int R = 0, G = 1, B = 2;                
        for( int rgb[] : scheme ){
            list.add( new Color( rgb[R], rgb[G], rgb[B]));
        }
        return Collections.unmodifiableList( list );
    }
}
