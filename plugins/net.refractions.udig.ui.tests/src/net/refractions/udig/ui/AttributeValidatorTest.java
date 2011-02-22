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

import org.geotools.feature.AttributeType;
import org.geotools.feature.AttributeTypeFactory;
import org.geotools.feature.FeatureTypeBuilder;
import org.geotools.filter.CompareFilter;
import org.geotools.filter.FilterFactory;
import org.geotools.filter.FilterFactoryFinder;
import org.geotools.filter.FilterType;

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
        FilterFactory fac=FilterFactoryFinder.createFilterFactory();

        CompareFilter filter = fac.createCompareFilter(FilterType.COMPARE_EQUALS);
        filter.addLeftValue(fac.createAttributeExpression("string")); //$NON-NLS-1$
        filter.addRightValue(fac.createLiteralExpression("Value")); //$NON-NLS-1$

        FeatureTypeBuilder builder=FeatureTypeBuilder.newInstance("test"); //$NON-NLS-1$
        AttributeType type=AttributeTypeFactory.newAttributeType("string", String.class, true, filter, null, null); //$NON-NLS-1$
        builder.addType(type);

        AttributeValidator validator=new AttributeValidator(type, builder.getFeatureType());

        assertNull( validator.isValid("Value") ); //$NON-NLS-1$
        assertNotNull( validator.isValid("IllegalValue") ); //$NON-NLS-1$
        assertNotNull( validator.isValid(3) );

        type=AttributeTypeFactory.newAttributeType("string", String.class, true, 5); //$NON-NLS-1$
        builder=FeatureTypeBuilder.newInstance("test"); //$NON-NLS-1$
        builder.addType(type);

        validator=new AttributeValidator(type, builder.getFeatureType());

        assertNull( validator.isValid("name") ); //$NON-NLS-1$
        assertNotNull( validator.isValid("IllegalValue") ); //$NON-NLS-1$
        assertNotNull( validator.isValid(3) );

    }

}
