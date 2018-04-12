/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.ui;

import org.locationtech.udig.ui.internal.Messages;

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
    private Object value;
    
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
        for( int i = 0; i < featureType.getAttributeCount(); i++ ) {
            AttributeDescriptor attributeType2 = featureType.getDescriptor(i);
            if( attributeType2==attributeType ){
                this.indexof=i;
                value=attributeType2.getDefaultValue();
            }
            
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
