/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.ui.palette;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;

import org.geotools.util.SimpleInternationalString;

/**
 * Represent palette information, with conveince methods for SLD.
 * <p>
 * This has been inspired by the work of ColorBrewer, indeed this is used to capture some of the
 * information produced by that project.
 * </p>
 * @deprecated use org.geotools.brewer.color.BrewerPalette
 */
public class Palette {
    /** <code>name</code> field */
    final SimpleInternationalString name;        
    /** <code>contents</code> field */
    
    final public List<Scheme> contents;
    
    /** <code>type</code> field */
    final public SchemeType type;  
    
    /** Friendly indicators */
    static final int GOOD = 1;
    static final int DOUBTFUL = 0;
    static final int BAD = -1;
    
    /**
     * Construct <code>Palette</code>.
     */
    public Palette( String name, Scheme list[]) {
        this.name = new SimpleInternationalString( name );
        this.contents = Arrays.asList( list );
        this.type = SchemeType.QUALITATIVE;
    }
    /**
     * Construct <code>Palette</code>.
     * 
     * @param bundle
     * @throws IOException 
     */
    public Palette( String pal ) throws IOException{
        this( load( pal ) );        
    }
    static final Properties load( String pal ) throws IOException{
        URL find = Palette.class.getResource( pal );
        Properties load = new Properties();        
        load.load( find.openStream() );
        
        return load;
    }
    /**
     * Construct list from properties
     * <p>
     * Parse in a Palette from a properties file:<pre><code>
     * palette.key=example
     * example.display=Example
     * palette.min=2
     * palette.max=3
     * palette.type=SEQUENTIAL 
     * example2={158, 202, 225},{ 49, 130, 189}
     * example2.good=colorBlind, photoCopy, projector, lcd, crt, print
     * example2.doubtful=projector
     * example3={222, 235, 247},{158, 202, 225},{ 49, 130, 189}
     * example3.good=lcd, crt
     * </code></pre>
     * </p>
     * @param properties
     */
    public Palette( Properties properties ) {
        String KEY = properties.getProperty( "palette.key" ); //$NON-NLS-1$
        name = new SimpleInternationalString( properties.getProperty( "palette.display" )); //$NON-NLS-1$
               
        type = Enum.valueOf( SchemeType.class, properties.getProperty( "palette.type" ).trim() ); //$NON-NLS-1$
        
        int min = Integer.parseInt( properties.getProperty("palette.min") ); //$NON-NLS-1$
        int max = Integer.parseInt( properties.getProperty("palette.max") ); //$NON-NLS-1$
        contents = new ArrayList<Scheme>(); 
        
        for( int i=min; i<=max; i++ ){
            //String key = KEY+i;            
            String defn = properties.getProperty( KEY+i );
//          ie. {158, 202, 225},{ 49, 130, 189}
            List<Color> colours = new ArrayList<Color>();
            //StringBuffer hack = new StringBuffer( defn );
            defn = defn.replace("},", "|" ); //$NON-NLS-1$ //$NON-NLS-2$
            defn = defn.replace('}', ' ' );  
            defn = defn.replace('{', ' ' );            
            defn = noWhitespace( defn );
            for( String c : defn.split("\\|") ){ //$NON-NLS-1$
                if( c.length() == 0 ) continue;
                String rgb[] = c.trim().split(","); //$NON-NLS-1$
                int r = Integer.parseInt( rgb[0].trim() );
                int g = Integer.parseInt( rgb[1].trim() );
                int b = Integer.parseInt( rgb[2].trim() );
                colours.add( new Color( r, g, b ) );
            }
            String goodDefn = noWhitespace( properties.getProperty( KEY+i+".good" )); //$NON-NLS-1$
            EnumSet<Friendly> good = Friendly.parse( goodDefn );
            
            String doubtfulDefn = noWhitespace( properties.getProperty( KEY+i+".doubtful" )); //$NON-NLS-1$
            EnumSet<Friendly> doubtful = Friendly.parse( doubtfulDefn );
            Scheme scheme = new Scheme( colours, good, doubtful );
            //System.out.println( i+": "+scheme );            
            contents.add( scheme );
        }       
    }
    static final String noWhitespace( String str ){
        if( str == null ) return null;
        StringBuffer buf = new StringBuffer();
        for( int i=0; i< str.length(); i++ ){
            char c = str.charAt(i);
            if( Character.isWhitespace( c ) ) continue;
            buf.append( c );
        }
        return buf.toString();        
    }
}