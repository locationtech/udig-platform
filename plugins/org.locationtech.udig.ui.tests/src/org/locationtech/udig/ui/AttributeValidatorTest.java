/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.util.factory.GeoTools;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.PropertyIsEqualTo;

/**
 * Test the attribute type cell editor validator
 * @author Jesse
 * @since 1.1.0
 */
@SuppressWarnings("nls")
public class AttributeValidatorTest {

    /**
     * Test method for {@link org.locationtech.udig.ui.AttributeValidator#isValid(java.lang.Object)}.
     * @throws Exception 
     */
    @Ignore
    @Test
    public void testIsValid() throws Exception {
        FilterFactory fac=CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        
        String attributeName = "string";
        PropertyIsEqualTo filter = fac.equals(fac.property(attributeName), fac.literal("Value"));
        
        SimpleFeatureTypeBuilder builder=new SimpleFeatureTypeBuilder();
        builder.setName("test");
        builder.restriction(filter).add(attributeName, String.class);
        
        SimpleFeatureType featureType = builder.buildFeatureType();
        
        AttributeValidator validator=new AttributeValidator(featureType.getDescriptor(attributeName), featureType);
        
        String valid = validator.isValid("Value");
        assertNull( "Valid", valid );
        
        assertNotNull( "Should not allow 'IllegalValue'", validator.isValid("IllegalValue") );
        
        assertNotNull( "Should not allow 3", validator.isValid(3) );
        
        builder.length(5).nillable(true).add(attributeName,String.class);
        featureType = builder.buildFeatureType();

        validator=new AttributeValidator(featureType.getDescriptor(attributeName), featureType);
        
        assertNull( validator.isValid("name") );
        assertNotNull( validator.isValid("IllegalValue") );
        assertNotNull( validator.isValid(3) );
    }

}
