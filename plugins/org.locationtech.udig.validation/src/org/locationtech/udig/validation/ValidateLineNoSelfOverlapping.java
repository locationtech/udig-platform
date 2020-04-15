/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.validation;

import org.geotools.validation.FeatureValidation;
import org.geotools.validation.spatial.LineNoSelfOverlappingValidation;
import org.opengis.feature.simple.SimpleFeatureType;

import org.locationtech.jts.geom.LineString;

/**
 * Overrides the FeatureValidationOp abstract class and returns the appropriate validation method for the
 * validation type.
 * <p>
 * </p>
 * 
 * @author chorner
 * @since 1.0.1
 */
public class ValidateLineNoSelfOverlapping extends FeatureValidationOp {
    public FeatureValidation getValidator() {
        return new LineNoSelfOverlappingValidation();
    }

    @Override
    protected boolean canValidate( SimpleFeatureType featureType ) {
        if (featureType.getGeometryDescriptor() instanceof LineString) {
            return true;
        }
        return false;
    }

}
