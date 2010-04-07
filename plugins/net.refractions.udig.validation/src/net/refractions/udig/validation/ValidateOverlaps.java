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

import net.refractions.udig.project.ILayer;

import org.geotools.validation.IntegrityValidation;
import org.geotools.validation.relate.OverlapsIntegrity;

/**
 * Overrides the FeatureValidationOp abstract class and returns the appropriate validation method for the
 * validation type.
 * <p>
 * </p>
 * 
 * @author chorner
 * @since 1.0.1
 */
public class ValidateOverlaps extends IntegrityValidationOp {
    public IntegrityValidation getValidator(ILayer[] layer) {
        OverlapsIntegrity overlapsIntegrity = new OverlapsIntegrity(); 
        overlapsIntegrity.setExpected(false); //(for now we'll assume overlaps are unexpected)
        String nameSpace = layer[0].getSchema().getName().getNamespaceURI();
        String typeName = layer[0].getSchema().getName().getLocalPart();
        overlapsIntegrity.setGeomTypeRefA(nameSpace+":"+typeName); //$NON-NLS-1$
        //check for the existence of a second layer
        if (layer.length > 1) {
            nameSpace = layer[1].getSchema().getName().getNamespaceURI();
            typeName = layer[1].getSchema().getName().getLocalPart();
            overlapsIntegrity.setGeomTypeRefB(nameSpace+":"+typeName); //$NON-NLS-1$
        }
        return overlapsIntegrity;
    }
}
