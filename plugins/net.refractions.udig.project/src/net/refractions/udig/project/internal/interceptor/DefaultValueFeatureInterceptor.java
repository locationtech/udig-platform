package net.refractions.udig.project.internal.interceptor;

import net.refractions.udig.project.interceptor.FeatureInterceptor;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.PropertyDescriptor;


/**
 * Used to fill in default values from attribute descriptor.
 * 
 * @author Jody
 * @since 1.2.0
 */
public class DefaultValueFeatureInterceptor implements FeatureInterceptor {

    public Boolean run( Feature feature ) {
        if( feature instanceof SimpleFeature ){
            SimpleFeatureType type = (SimpleFeatureType) feature.getType();
            SimpleFeature simpleFeature = (SimpleFeature) feature;
            for( int i = 0; i < type.getAttributeCount(); i++ ) {
                if( simpleFeature.getAttribute(i) == null ){
                    Object value = toDefaultValue(type.getDescriptor(i));
                    simpleFeature.setAttribute(i, value );
                }
            }
        }
        else {
            for( Property property : feature.getProperties() ){
                Object defaultValue = toDefaultValue( property.getDescriptor() );
                property.setValue( defaultValue );
            }
        }
        return true;
    }
    /**
     * Retrieves a default value for the provided descriptor.
     * <p>
     * The descriptor getDefaultValue() is used if available; if not
     * a default value is created base on the descriptor binding. The default
     * values mirror those used by Java; empty string, boolean false, 0 integer, 0.0 double, etc...
     * >p>
     * @param type attribute descriptor
     */
    private Object toDefaultValue( PropertyDescriptor descriptor ) {
        Object value = null;
        if( descriptor instanceof AttributeDescriptor ){
            AttributeDescriptor attributeDescriptor = (AttributeDescriptor) descriptor;
            value = attributeDescriptor.getDefaultValue();
            if( value != null ){
                return value;
            }
        }
        Class< ? > type = descriptor.getType().getBinding();
        if (Boolean.class.isAssignableFrom(type)
                || boolean.class.isAssignableFrom(type)){
            return Boolean.FALSE;
        }
        if (String.class.isAssignableFrom(type)){
            return ""; //$NON-NLS-1$
        }
        if (Integer.class.isAssignableFrom(type)){
            return Integer.valueOf(0);
        }
        if (Double.class.isAssignableFrom(type)){
            return  Double.valueOf(0);
        }
        if (Float.class.isAssignableFrom(type)){
            return Float.valueOf(0);
        }
        return null;
    }

}
