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

}
