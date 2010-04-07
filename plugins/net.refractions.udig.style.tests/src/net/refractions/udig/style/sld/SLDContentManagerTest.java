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
