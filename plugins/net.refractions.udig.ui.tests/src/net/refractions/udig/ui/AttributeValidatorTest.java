/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.ui;

import junit.framework.TestCase;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.PropertyIsEqualTo;

/**
 * Test the attribute type cell editor validator
 * @author Jesse
 * @since 1.1.0
 */
public class AttributeValidatorTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Test method for {@link net.refractions.udig.ui.AttributeValidator#isValid(java.lang.Object)}.
     * @throws Exception 
     */
    public void testIsValid() throws Exception {
        FilterFactory fac=CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        
        String attributeName = "string";
		PropertyIsEqualTo filter = fac.equals(fac.property(attributeName), fac.literal("Value"));
        
        SimpleFeatureTypeBuilder builder=new SimpleFeatureTypeBuilder(); //$NON-NLS-1$
        builder.setName("test");
        builder.restriction(filter).add(attributeName, String.class);
        
        SimpleFeatureType featureType = builder.buildFeatureType();
        
        AttributeValidator validator=new AttributeValidator(featureType.getDescriptor(attributeName), featureType);
        
        if( false ){
            String valid = validator.isValid("Value");
            assertNull( "Valid", valid ); //$NON-NLS-1$
            
            assertNotNull( "Should not allow 'IllegalValue'", validator.isValid("IllegalValue") ); //$NON-NLS-1$
            
            assertNotNull( "Should not allow 3", validator.isValid(3) );
            
            builder.length(5).nillable(true).add(attributeName,String.class);
            featureType = builder.buildFeatureType();
    
            validator=new AttributeValidator(featureType.getDescriptor(attributeName), featureType);
            
            assertNull( validator.isValid("name") ); //$NON-NLS-1$
            assertNotNull( validator.isValid("IllegalValue") ); //$NON-NLS-1$
            assertNotNull( validator.isValid(3) );
        }
    }

}
