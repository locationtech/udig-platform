/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.ui;

import net.refractions.udig.internal.ui.UiPlugin;
import net.refractions.udig.ui.internal.Messages;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.geotools.feature.AttributeType;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;

/**
 * Validates that an input is a legal input
 *
 * @author jeichar
 * @since 0.3
 */
public class AttributeValidator implements ICellEditorValidator {
    private final AttributeType attributeType;
    private final FeatureType featureType;
    private Object[] values;
    private int indexof;
    /**
     * Creates a new instance of AttributeValidator
     *
     * @param attributeType The AttributeType that the new instance will validate
     * @param featureType the featureType that contains the attributeType.
     */
    public AttributeValidator( AttributeType attributeType, FeatureType featureType) {
        this.attributeType = attributeType;
        this.featureType=featureType;
        values=new Object[featureType.getAttributeCount()];
        for( int i = 0; i < featureType.getAttributeCount(); i++ ) {
            AttributeType attributeType2 = featureType.getAttributeType(i);
            if( attributeType2==attributeType ){
                this.indexof=i;
            }
            values[i]=attributeType2.createDefaultValue();
        }
    }

    @SuppressWarnings("unchecked")
    public String isValid( Object value ) {
        if( value==null || (value instanceof String && ((String)value).equals(""))){ //$NON-NLS-1$
            if( !attributeType.isNillable() )
                return Messages.AttributeValidator_missingAtt1+attributeType.getName()+Messages.AttributeValidator_missingAtt2;
            else
                return null;
        }
            if( !attributeType.getType().isAssignableFrom(value.getClass()) ){
                return Messages.AttributeValidator_wrongType+ attributeType.getType().getSimpleName();
            }
            values[indexof]=value;
            try {
                Feature feature = featureType.create(values);
                if( attributeType.getRestriction()!=null && !attributeType.getRestriction().contains(feature) )
                    return Messages.AttributeValidator_restriction+attributeType.getRestriction();
            } catch (Throwable e1) {
                UiPlugin.log("Tried to create a feature for validating the attribute value but something went wrong (this may not be an error)", e1); //$NON-NLS-1$
            }

            try {
                attributeType.validate(value);
                return null;
        } catch (Throwable e) {
            return e.getLocalizedMessage();
        }
    }
}
