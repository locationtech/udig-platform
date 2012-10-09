/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.ui.filter;

import java.util.Arrays;
import java.util.List;

import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Input for use with an {@link IExpressionViewer} extending {@link IFilterInput} with additional
 * information such as range, option and expected java class.
 * <p>
 * Example:
 * 
 * <pre>
 * // Opacity is defined as an Expression in the Symbology Encoding Specification
 * IExpressionViewer opacityViewer = new CQLExpressionViewer(composite, SWT.DEFAULT);
 * 
 * ExpressionInput opacity = new ExpressionInput();
 * opacity.setRequired(false);
 * opacity.setBinding(Float.class);
 * opacity.setMin(0.0);
 * opacity.setMax(1.0);
 * opacity.setDefault(1.0);
 * 
 * // used to suggest literal values
 * opacity.setOptions(0.0, 0.25, 0.5, 0.75, 0.8, 0.85, 0.90, 0.95, 1.0);
 * 
 * // used to suggest numeric attributes
 * opacity.setSchema(featureType);
 * 
 * opacityViewer.setInput(opacity);
 * 
 * // Acts as a normal viewer with the selection being the edited Expression
 * opacityViewer.setSelection(new StructuredSelection(expr));
 * opacityViewer.setExpression(expr); // direct access via helper method
 * </pre>
 * 
 * @author Jody Garnett
 * @since 1.3.0
 */
public class ExpressionInput extends FilterInput {

    private Class<?> binding;

    private Comparable<?> min;

    private Comparable<?> max;

    private List<Object> options;

    private Object defaultLiteral;

    private boolean percent;

    public ExpressionInput() {
        this(null);
    }

    public ExpressionInput(SimpleFeatureType schema) {
        this(schema, false);
    }

    public ExpressionInput(SimpleFeatureType schema, boolean required) {
        this(schema, required, Object.class);
    }

    public ExpressionInput(SimpleFeatureType schema, boolean required, Class<?> binding) {
        super(schema, required);
        this.binding = binding;
    }

    /**
     * Class expression is expected to evaluate to.
     * 
     * @return java class binding
     */
    public Class<?> getBinding() {
        return binding;
    }

    /**
     * Class expression is expected to evaluate to.
     * 
     * param binding java class binding
     */
    public void setBinding(Class<?> binding) {
        this.binding = binding;
    }

    /**
     * Default literal value assumed if the user does not provide an expression.
     * <p>
     * As an example the default mark size for an Icon when rendering is 16 unless otherwise
     * specified, the default value is often not written out.
     * 
     * @return default literal value
     */
    public Object getDefault() {
        return defaultLiteral;
    }
    /**
     * Default literal value assumed if the user does not provide an expression.
     * <p>
     * As an example the default mark size for an Icon when rendering is 16 unless otherwise
     * specified, the default value is often not written out.
     * 
     * @param defaultLiteral default literal value
     */
    public void setDefault(Object defaultLiteral) {
        this.defaultLiteral = defaultLiteral;
    }

    /**
     * Minimum ranged value; used when entering a literal value.
     * <p>
     * The minimum value can be used when validating a literal; or used to define the lower limit of
     * a slider or spinner.
     * 
     * @return minimum ranged value when entering a literal.
     */
    public Comparable<?> getMin() {
        return min;
    }

    /**
     * Minimum ranged value; used when entering a literal value.
     * <p>
     * The minimum value can be used when validating a literal; or used to define the lower limit of
     * a slider or spinner.
     * 
     * @param min minimum ranged value when entering a literal.
     */
    public void setMin(Comparable<?> min) {
        this.min = min;
    }

    /**
     * Maximum ranged value; used when entering a literal value.
     * <p>
     * The maximum value can be used when validating a literal; or used to define the upper limit of
     * a slider or spinner.
     * 
     * @return maximum ranged value when entering a literal.
     */
    public Comparable<?> getMax() {
        return max;
    }
    /**
     * Used to flag an expression that is expected to be treated as a percentage.
     * <p>
     * This setting can be used to ask viewers to rener the value as a percentage( example: 50%).
     * <p>
     * For consistency it is expected that:<pre>input.setMin(0);
     * input.setMax(1.0);</pre>
     * @return flag indicating input is expected to be a percentage.
     */
    public boolean isPercent(){
        return percent;
    }
    /**
     * Used to flag an expression so that numeric values are treated as a percentage.
     * <p>
     * This setting can be used to ask viewers to display the value as a percentage( example: 50%).
     * <p>
     * For consistency this method calls input.setMin(0) and setMax(1.0).
     * 
     * @return flag indicating input is expected to be a percentage.
     */
    public void setPercent(boolean percent) {
        this.percent = percent;
        if( percent ){
            setMin(0.0);
            setMax(1.0);
        }
    }
    /**
     * Maximum ranged value; used when entering a literal value.
     * <p>
     * The maximum value can be used when validating a literal; or used to define the upper limit of
     * a slider or spinner.
     * 
     * @param max maximum ranged value when entering a literal.
     */
    public void setMax(Comparable<?> max) {
        this.max = max;
    }

    /**
     * Options suggested to user when entering a literal value.
     * <p>
     * Options may just be used as suggestions when typing; or presented as a fixed set using a
     * combo control.
     * 
     * @param options Options suggested to the user when entering a literal value
     */
    public void setOptions(Object... options) {
        setOptionList(Arrays.asList(options));
    }

    /**
     * Options suggested to user when entering a literal value.
     * <p>
     * Options may just be used as suggestions when typing; or presented as a fixed set using a
     * combo control.
     * 
     * @param options Options suggested to the user when entering a literal value
     */
    public void setOptionList(List<Object> options) {
        this.options = options;
    }

    /**
     * Options suggested to user when entering a literal value.
     * <p>
     * Options may just be used as suggestions when typing; or presented as a fixed set using a
     * combo control.
     * 
     * @param options Options suggested to the user when entering a literal value
     */

    public List<Object> getOptions() {
        return options;
    }
    @Override
    public String toString() {
        StringBuilder build = new StringBuilder();
        build.append( getClass().getSimpleName() );
        build.append(" ");
        if( required ){
            build.append("required ");
        }
        if( viewerId != null ){
            build.append(" viewerId:");
            build.append( viewerId );
            build.append(" ");
        }
        if( feedback != null ){
            build.append( " feedback:" );
            build.append(feedback.getControl().getClass().getSimpleName() );
            build.append(" ");
        }
        if( schema != null ){
            build.append(" schema:");
            build.append( schema.getTypeName() );
            build.append(" ");
        }
        if( binding != null ){
            build.append(" binding:");
            build.append( binding.getSimpleName() );
            build.append(" " );
        }
        if( defaultLiteral != null ){
            build.append(" default:");
            build.append( defaultLiteral );
            build.append(" " );
        }
        if( min != null || max != null ){
            build.append("limit: ");
            build.append( min );
            build.append(" .. ");
            build.append( max );
            build.append(" ");
        }
        if( options != null ){
            build.append(" options:");
            build.append( options );
            build.append(" " );
            
        }
        return build.toString();
    }

}
