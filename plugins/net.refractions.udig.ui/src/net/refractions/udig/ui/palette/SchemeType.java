/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package net.refractions.udig.ui.palette;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.geotools.util.SimpleInternationalString;
import org.opengis.util.InternationalString;

/**
 * Enumeration on SchemeType.
 * 
 * @author jgarnett
 * @since 0.9.0
 * @deprecated
 */
public enum SchemeType {
    /**
     * Sequential schemes are suited to ordered data that progress from low to high.
     * <p>
     * Lightness steps dominate the look of these schemes, with light colors for low data values to
     * dark colors for high data values.
     */
    SEQUENTIAL,
    /**
     * Diverging schemes put equal emphasis on mid-range critical values and extremes at both ends
     * of the data range.
     * <p>
     * The critical class or break in the middle of the legend is emphasized with light colors and
     * low and high extremes are emphasized with dark colors that have contrasting hues.
     */
    DIVERGING,
    /**
     * Qualitative schemes do not imply magnitude differences between legend classes, and hues are
     * used to create the primary visual differences between classes.
     * <p>
     * Qualitative schemes are best suited to representing nominal or categorical data.
     */
    QUALITATIVE;
    
    /** <code>description</code> field */
    public final InternationalString description;
    
    /** <code>display</code> field */
    public final InternationalString display;
        
    private SchemeType(){        
        this.display = string( name()+".display" ); //$NON-NLS-1$
        this.description = string( name()+".description" );         //$NON-NLS-1$
    }
    
    private static ResourceBundle bundle = null;
    
    /**
     * Gets a string from the resource bundle. We don't want to crash because of a missing String.
     * Returns the key if not found.
     * 
     * @param key  the id to look up
     * @return the string with the given key
     */
    private static InternationalString string(String key) {
        if( bundle == null ){
            try {            
                bundle = ResourceBundle.getBundle( "net.refractions.udig.ui.palette.schemeType"); //$NON-NLS-1$
            }
            catch( Throwable t ){
                t.printStackTrace();
                bundle = null;
            }
        }
        try {
            if( bundle!=null )
                return new SimpleInternationalString( bundle.getString(key) );
            else
                return new SimpleInternationalString("!" + key + "!"); //$NON-NLS-1$ //$NON-NLS-2$
                
        } catch (MissingResourceException e) {
            return new SimpleInternationalString(key);
        }
    }
}
