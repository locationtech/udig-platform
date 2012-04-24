package net.refractions.udig.ui.filter;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.ui.filter.ViewerFactory.Appropriate;

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
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.FilterFactory2;
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
        public int score(ExpressionInput input, Expression expression) {
            if (expression instanceof Literal) {
                Literal literal = (Literal) expression;
                Double number = literal.evaluate(null, Double.class);
                if (number != null) {
                    if (number >= 0 && number <= 1.0) {
                        return Appropriate.COMPLETE.getScore();
                    }
                }
            }
            if (expression instanceof PropertyName) {
                PropertyName name = (PropertyName) expression;
                if( input != null && input.getSchema() != null ){
                    SimpleFeatureType schema = input.getSchema();
                    AttributeDescriptor descriptor = schema.getDescriptor(name.getPropertyName());
                    if (descriptor != null) {
                        Class<?> binding = descriptor.getType().getBinding();
                        if (Number.class.isAssignableFrom(binding)) {
                            return Appropriate.COMPLETE.getScore();
                        }
                    }
                }
            }
            return Appropriate.INCOMPLETE.getScore();
        }

        @Override
        public IExpressionViewer createViewer(Composite parent, int style) {
            return new SpinnerExpressionViewer(parent, style);
        }
    }

    Composite control;

    protected ComboViewer combo;

    protected Spinner spinner;

    protected Text text;

    private SelectionListener listener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            Expression newExpression = validate();
            internalUpdate(newExpression);
        }
        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }
    };

    public SpinnerExpressionViewer(Composite parent, int style) {
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

    protected Expression validate() {
        if( !combo.getSelection().isEmpty() ){
            Object selection = ((StructuredSelection)combo.getSelection()).getFirstElement();
            if( selection != NONE ){
                if ( selection instanceof String){
                    String propertyName = (String) selection;
                    FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
                    
                    return ff.property( propertyName );
                }
                // confused ... sigh
            }
            
        }
        return null;
    }

    @Override
    public Control getControl() {
        return control;
    }
    
    /**
     * Mostly used to update the combo with {@link FilterInput#getNumericPropertyList()} if available.
     */
    @Override
    public void refresh() {
        if( input != null ){
            combo.setInput( input.getNumericPropertyList() );
            combo.getControl().setEnabled(true);
        }
        else {
            combo.setInput(null);
            combo.getControl().setEnabled(false);
        }
    }
    
    public void refreshExpression() {
        Expression expr = getExpression();
        if (expr instanceof PropertyName) {
            PropertyName property = (PropertyName) expr;
            String name = property.getPropertyName();
            refreshControls(null, name, null);
            return;
        } else if (expr instanceof Literal) {
            Literal literal = (Literal) expr;
            Double percent = literal.evaluate(null, Double.class);
            if (percent != null) {
                refreshControls(percent, null, null);
                return;
            }
        }
        // We cannot display this expression - put up a warning
        String message = ECQL.toCQL(expr);
        refreshControls(null, null, message);
    }

    protected void refreshControls(final Double percent, final String propertyName, final String message) {
        if (control != null && !control.isDisposed()) {
            control.getDisplay().asyncExec(new Runnable() {
                public void run() {
                    if (control == null || control.isDisposed()) {
                        return; // must of been disposed while in the display thread queue
                    }
                    combo.setInput(input.getNumericPropertyList());

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
    public void setExpression(Expression newExpression) {
        if (!newExpression.equals(this.expression)) {
            this.expression = (Expression) input;
            refreshExpression();
        }
    }

}
