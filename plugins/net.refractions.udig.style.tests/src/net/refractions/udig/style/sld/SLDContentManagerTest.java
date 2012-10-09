/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.style.sld;

import static org.junit.Assert.*;

import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.junit.Test;

/**
 * Test SLDContentManager utility class; you can run this as a normal JUnit test.
 * 
 * @author Jody
 * @since 1.1.0
 */
public class SLDContentManagerTest {
    @Test
    public void testDefault(){
        StyleBuilder styleBuilder = new StyleBuilder();
        SLDContentManager manager = new SLDContentManager();
        
        Style style = manager.getStyle();
        assertNotNull( "empty style created", style );
        
        assertTrue( style.featureTypeStyles().isEmpty() );
        assertNotNull( manager.getDefaultFeatureTypeStyle() );
        assertFalse( style.featureTypeStyles().isEmpty() );        
        
        
    }
}
