package net.refractions.udig.ui.filter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;

/**
 * ExpressionViewer compatible with how Opacity is handled in the advanced style pages.
 * <p>
 * The original has as spinner (from 0 to 100%) followed by an Combo to choose a PropertyName.
 * <p>
 * This implementation has a spinner which is enabled when a range (0 to 100%) is provided; an
 * Attribute combo which is enabled when a schema is provided.
 * 
 * @author Scott
 * @since 1.3.0
 */
public class SpinnerExpressionViewer extends IExpressionViewer {
    /**
     * Used to represent "none" option; should be internationalised
     */
    public static final String NONE = "- none -";

    /**
     * Factory used for the general purpose DefaultExpressionViewer.
     * 
     * @author jody
     * @since 1.2.0
     */
    public static class Factory extends ExpressionViewerFactory {
        @Override
        public int appropriate(SimpleFeatureType schema, Expression expression) {
            if (expression instanceof Literal) {
                Literal literal = (Literal) expression;
                Double number = literal.evaluate(null, Double.class);
                if (number != null) {
                    if (number >= 0 && number <= 1.0) {
                        return APPROPRIATE;
                    }
                }
            }
            if (expression instanceof PropertyName) {
                PropertyName name = (PropertyName) expression;
                AttributeDescriptor descriptor = schema.getDescriptor(name.getPropertyName());
                if (descriptor != null) {
                    Class<?> binding = descriptor.getType().getBinding();
                    if (Number.class.isAssignableFrom(binding)) {
                        return APPROPRIATE;
                    }
                }
            }
            return INCOMPLETE;
        }

        @Override
        public IExpressionViewer createViewer(Composite parent, int style) {
            return new SpinnerExpressionViewer(parent, style);
        }
    }

    /**
     * This is the expression we are working on here.
     * <p>
     * We are never going to be "null"; Expression.NIL is used to indicate an intentionally empty
     * expression.
     */
    protected Expression expr = Expression.NIL;

    Composite control;

    protected ComboViewer combo;

    protected Spinner spinner;

    protected Text text;

    boolean isRequired;

    private SimpleFeatureType type;

    private SelectionListener listener = new SelectionListener() {

        @Override
        public void widgetSelected(SelectionEvent e) {
            validate();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }
    };

    private Class<?> binding;

    public SpinnerExpressionViewer(Composite parent, int style) {
        super(parent);

        control = new Composite(parent, style);
        combo = new ComboViewer(control, SWT.DEFAULT);
        combo.setContentProvider(ArrayContentProvider.getInstance());
        combo.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof PropertyDescriptor) {
                    PropertyDescriptor descriptor = (PropertyDescriptor) element;
                    return descriptor.getName().getLocalPart();
                }
                return super.getText(element);
            }
        });
        combo.getControl().setEnabled(false);

        spinner = new Spinner(control, SWT.DEFAULT);
        spinner.setMinimum(0);
        spinner.setMaximum(100);
        spinner.setIncrement(10);
        spinner.setEnabled(false);
    }

    @Override
    public Control getControl() {
        return control;
    }

    public boolean validate() {
        return true;
    }

    @Override
    public String getValidationMessage() {
        return null;
    }

    @Override
    public Expression getInput() {
        return expr;
    }

    @Override
    public ISelection getSelection() {
        if (expr == null) {
            return null;
        }
        IStructuredSelection selection = new StructuredSelection(expr);
        return selection;
    }

    @Override
    public void refresh() {
        if (expr instanceof PropertyName) {
            PropertyName property = (PropertyName) expr;
            String name = property.getPropertyName();
            refresh(null, name, null);
            return;
        } else if (expr instanceof Literal) {
            Literal literal = (Literal) expr;
            Double percent = literal.evaluate(null, Double.class);
            if (percent != null) {
                refresh(percent, null, null);
                return;
            }
        }
        // We cannot display this expression - put up a warning
        String message = ECQL.toCQL(expr);
        refresh(null, null, message);
    }

    protected void refresh(final Double percent, final String propertyName, final String message) {
        if (control != null && !control.isDisposed()) {
            control.getDisplay().asyncExec(new Runnable() {
                public void run() {
                    if (control == null || control.isDisposed()) {
                        return; // must of been disposed while in the display thread queue
                    }
                    combo.setInput(toNumericAttributeList(schema));

                    if (percent != null) {
                        spinner.setEnabled(true);
                        int number = (int) (percent * 100.0);
                        spinner.setSelection(number);
                        if (message == null) {
                            text.setText(number + "%");
                        }
                    } else {
                        spinner.setEnabled(false);
                        spinner.setSelection(100);
                    }
                    if (propertyName != null) {
                        StructuredSelection selection = new StructuredSelection(propertyName);
                        combo.setSelection(selection, true);
                    } else {
                        StructuredSelection selection = new StructuredSelection(NONE);
                        combo.setSelection(selection, true);
                    }
                    if (message != null) {
                        text.setText(message);
                    }
                }
            });
        }
    }

    @Override
    public void setInput(Object input) {
        if (input instanceof Expression) {
            if (!input.equals(expr)) {
                expr = (Expression) input;
                refresh();
            }
        } else if (input instanceof String) {
            String text = (String) input;
            try {
                Expression inputExpression = ECQL.toExpression(text);
                if (inputExpression == null) {
                    inputExpression = Expression.NIL;
                }
                if (!inputExpression.equals(expr)) {
                    expr = (Expression) input;
                    refresh();
                }
                refresh();
            } catch (CQLException e) {
                throw new IllegalStateException("ExpressionViewer requires Expression for input");
            }
        } else {
            throw new IllegalStateException("ExpressionViewer requires Expression for input");
        }
    }

    @Override
    public void setSelection(ISelection selection, boolean reveal) {
        if (selection instanceof StructuredSelection) {
            StructuredSelection structuredSelection = (StructuredSelection) selection;
            Object value = structuredSelection.getFirstElement();

            if (value instanceof Expression) {
                setInput((Expression) value);
            }
        }
    }

    @Override
    public void feedback() {

    }

    @Override
    public void feedback(String warning) {

    }

    @Override
    public void feedback(String exception, Exception eek) {

    }

    @Override
    public void setSchema(SimpleFeatureType schema) {
        this.type = schema;
    }

    private List<String> toNumericAttributeList(SimpleFeatureType schema) {
        final List<String> options = new ArrayList<String>();
        if (schema != null) {
            for (AttributeDescriptor descriptor : schema.getAttributeDescriptors()) {
                if (Number.class.isAssignableFrom(descriptor.getType().getBinding())) {
                    options.add(descriptor.getLocalName());
                }
            }
        }
        options.add(NONE);
        return options;
    }

    @Override
    public SimpleFeatureType getSchema() {
        return type;
    }

    @Override
    public void setExpected(Class<?> binding) {
        if (!Number.class.isAssignableFrom(binding)) {
            feedback("Used to enter numbers");
        }
        this.binding = binding;
    }

    @Override
    public Class<?> getExpected() {
        return this.binding;
    }
}
