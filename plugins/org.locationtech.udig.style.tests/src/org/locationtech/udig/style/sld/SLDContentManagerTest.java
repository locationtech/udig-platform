/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.sld;

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
