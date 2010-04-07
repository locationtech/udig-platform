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
package net.refractions.udig.validation;

import org.geotools.validation.FeatureValidation;
import org.geotools.validation.spatial.LineNoSelfIntersectValidation;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.LineString;

/**
 * Overrides the FeatureValidationOp abstract class to return LineNoSelfOverlappingValidation()
 * <p>
 * </p>
 * 
 * @author chorner
 * @since 1.0.1
 */
public class ValidateLineNoSelfIntersect extends FeatureValidationOp {
    public FeatureValidation getValidator() {
        return new LineNoSelfIntersectValidation();
    }

    @Override
    protected boolean canValidate( SimpleFeatureType featureType ) {
        if (featureType.getGeometryDescriptor() instanceof LineString) {
            return true;
        }
        return false;
    }
}
