/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2015 Refractions Research Inc. and Others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.validation;

import static org.junit.Assert.*;

import org.geotools.data.DataUtilities;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;

public class ValidateLineStringTest {

    private SimpleFeatureType lineStringFeatureType;
    private SimpleFeatureType polygonFeatureType;
    
    @Before
    public void setUp() throws Exception {
        lineStringFeatureType = DataUtilities.createType("lineStringType", "geom:LineString,name:String");
        polygonFeatureType = DataUtilities.createType("polygonType", "geom:Polygon,name:String");
    }

    @Test
    public void testCanValidateLineStringValidateLineMustBeASinglePart() throws Exception {
        assertTypeValidation(new ValidateLineMustBeASinglePart());
    }

    @Test
    public void testCanValidateLineStringValidateLineNoSelfIntersect() throws Exception {
        assertTypeValidation(new ValidateLineNoSelfIntersect());
    }

    @Test
    public void testCanValidateLineStringValidateLineNoSelfOverlapping() throws Exception {
        assertTypeValidation(new ValidateLineNoSelfOverlapping());
    }

    private void assertTypeValidation(FeatureValidationOp op) {
        assertTrue(op.canValidate(lineStringFeatureType));
        assertFalse(op.canValidate(polygonFeatureType));
    }
}
