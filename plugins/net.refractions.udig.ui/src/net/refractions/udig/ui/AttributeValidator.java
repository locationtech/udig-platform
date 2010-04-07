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

import net.refractions.udig.ui.internal.Messages;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.geotools.feature.type.Types;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

/**
 * Validates that an input is a legal input
 * <p>
 * For details on the kind of work this class does (or needs to do)
 * check the following:
 * <a href="http://docs.codehaus.org/display/GEOTDOC/Feature+Model+Guide#FeatureModelGuide-ValidatingaFeature">Validating a Feature</a>.
 * <p>
 * This is for the table view cell editing; and the default feature editor.
 * 
 * @author jeichar
 * @since 0.3
 */
public class AttributeValidator implements ICellEditorValidator {
    private final AttributeDescriptor attributeDescriptor;
    private final SimpleFeatureType featureType;
    private Object[] values;
    
    /** "Expected" index of the attributeType in the SimpleFeatureType */
    private int indexof;
    
    /**
     * Creates a new instance of AttributeValidator
     * 
     * @param attributeType The AttributeDescriptor that the new instance will validate
     * @param featureType the featureType that contains the attributeType.
     */
    public AttributeValidator( AttributeDescriptor attributeType, SimpleFeatureType featureType) {
        this.attributeDescriptor = attributeType;
        this.featureType=featureType;
        values=new Object[featureType.getAttributeCount()];
        for( int i = 0; i < featureType.getAttributeCount(); i++ ) {
            AttributeDescriptor attributeType2 = featureType.getDescriptor(i);
            if( attributeType2==attributeType ){
                this.indexof=i;
            }
            values[i]=attributeType2.getDefaultValue();
        }
    }
    
    @SuppressWarnings("unchecked")
    public String isValid( Object value ) {
    	
        if( value==null || (value instanceof String && ((String)value).equals(""))){ //$NON-NLS-1$
            if( !attributeDescriptor.isNillable() )
                return Messages.AttributeValidator_missingAtt1+attributeDescriptor.getName()+Messages.AttributeValidator_missingAtt2;
            else
                return null; 
        }
            if( !attributeDescriptor.getType().getBinding().isAssignableFrom(value.getClass()) ){
                return Messages.AttributeValidator_wrongType+ attributeDescriptor.getType().getBinding().getSimpleName();
            }
            /*
            if( false ){
	            values[indexof]=value;
	            try {
	            	// FIXME: The following line is fatal when restrictions are in place
	                SimpleFeature feature = SimpleFeatureBuilder.build( featureType, values, null );
	                
	                for( Filter filter : attributeDescriptor.getType().getRestrictions() ){
	                	if( filter != null && !filter.evaluate(feature) ){
	                		return Messages.AttributeValidator_restriction+filter;
	                	}
	                }
	            } catch (Throwable e1) {
	                UiPlugin.log("Tried to create a feature for validating the attribute value but something went wrong (this may not be an error)", e1); //$NON-NLS-1$
	            }
		    }
		    */
            try {
            	Types.validate( attributeDescriptor, value );
                return null;
        } catch (Throwable e) {
            return e.getLocalizedMessage();
        }
        
    }
}
