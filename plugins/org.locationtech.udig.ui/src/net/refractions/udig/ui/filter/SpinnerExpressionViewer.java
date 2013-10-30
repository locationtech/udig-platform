/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.ui.filter;

import net.miginfocom.swt.MigLayout;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.geotools.factory.CommonFactoryFinder;
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
 * @author Jody Garnett
 * @since 1.3.2
 */
public class SpinnerExpressionViewer extends IExpressionViewer {
    /**
     * Used to represent "none" option; should be internationalised
     */
    public static final String NONE = "- none -";

    /**
     * Factory SpinnerExpressionViewer is restricted to working with literal numbers and numeric PropertyName expressions.
     * 
     * @author Jody Garnett
     * @since 1.3.2
     */
    public static class Factory extends ExpressionViewerFactory {
        @Override
        public int score(ExpressionInput input, Expression expression) {
            if( input != null ){
                Class<?> binding = input.getBinding();
                
                if( !binding.isAssignableFrom(Double.class)){
                    return Appropriate.NOT_APPROPRIATE.getScore();
                }
            }
            if (expression instanceof Literal) {
                Literal literal = (Literal) expression;
                Double number = literal.evaluate(null, Double.class);
                
                if (number != null) {
                    if (number >= 0 && number <= 1.0) {
                        return Appropriate.APPROPRIATE.getScore();
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
                            return Appropriate.APPROPRIATE.getScore();
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

    private ISelectionChangedListener comboListener = new ISelectionChangedListener() {
        public void selectionChanged(SelectionChangedEvent event) {
            Expression newExpression = validate();
            internalUpdate(newExpression);
        }
    };

    public SpinnerExpressionViewer(Composite parent, int style) {
        boolean multiLine = (SWT.MULTI & style) != 0;
        boolean readOnly = (SWT.READ_ONLY & style) != 0;
        
        control = new Composite(parent, SWT.NO_SCROLL){
          @Override
            public void setEnabled(boolean enabled) {
                super.setEnabled(enabled);
                combo.getControl().setEnabled(enabled);
                spinner.setEnabled(enabled);
                text.setEnabled(enabled);
            }
        };
        Label comboLabel=null;
        if( multiLine){
            comboLabel = new Label(control, SWT.NONE);
            comboLabel.setText("Numeric:");
        } 
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
        
        Label spinnerLabel=null;
        if( multiLine){
            spinnerLabel = new Label(control, SWT.NONE);
            spinnerLabel.setText("Value:");
        } 
        spinner = new Spinner(control, SWT.DEFAULT);
        spinner.setMinimum(0);
        spinner.setMaximum(100);
        spinner.setIncrement(10);
        spinner.setEnabled(false);
        
        Label textLabel=null;
        if( multiLine){
            textLabel = new Label(control, SWT.NONE);
            textLabel.setText("Text:");
        }
        text = new Text(control,SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER | SWT.NO_SCROLL );
        text.setEditable(false);
        
        if( multiLine ){
            control.setLayout(new MigLayout("insets 0","[][][grow]","[]"));
            
            comboLabel.setLayoutData("cell 0 0, alignx trailing");
            combo.getControl().setLayoutData("cell 1 0, width 200:30%:100%,gap related");
            
            spinnerLabel.setLayoutData("cell 0 1, alignx trailing");
            spinner.setLayoutData("cell 1 1, width 200:30%:300%,gap related");
            
            textLabel.setLayoutData("cell 0 2, alignx trailing");
            text.setLayoutData("cell 1 2, grow,width 200:pref:100%,gap related");
        }
        else {
            control.setLayout(new MigLayout("insets 0","flowx",""));
            combo.getControl().setLayoutData("width 200:30%:100%");
            spinner.setLayoutData("width 200:30%:300%,gap unrelated");
            text.setLayoutData("grow,width 200:pref:100%,gap unrelated");
        }
        listen(true);
    }

    private void listen(boolean listen) {
        if( listen){
            spinner.addSelectionListener(listener);
            combo.addSelectionChangedListener(comboListener);
        }
        else {
            spinner.removeSelectionListener(listener);
            combo.removeSelectionChangedListener(comboListener);
        }
    }

    protected Expression validate() {
        if( combo.getControl().isEnabled() &&  !combo.getSelection().isEmpty() ){
            Object selection = ((StructuredSelection)combo.getSelection()).getFirstElement();
            if( selection != NONE ){
                if ( selection instanceof String){
                    String propertyName = (String) selection;
                    FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
                    
                    return ff.property( propertyName );
                }
            }
        }
        if( spinner.isEnabled() ){
            int number = spinner.getSelection();
            FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
            
            return ff.literal( ((double)number)/100.0);
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
        // combo update if we have numeric attributes
        try {
            listen(false);
            if( input != null ){
                combo.setInput( input.toNumericPropertyList() );
                combo.getControl().setEnabled(true);
            }
            else {
                combo.setInput(null);
                combo.getControl().setEnabled(false);
            }
            // spinner if we have min and max
            if( input != null ){
                Object min = input.getMin();
                Object max = input.getMax();
                if( min instanceof Number && max instanceof Number){
                    int minInt = ((Number)min).intValue();
                    int maxInt = ((Number)max).intValue();
                    
                    spinner.setMinimum(minInt);
                    spinner.setMaximum(maxInt);
                    
                    spinner.setEnabled( true );
                }
                else {
                    spinner.setEnabled(false);
                }
            }
            else {
                spinner.setEnabled(false);
            }
            text.setEnabled(false);
        }
        finally {
            listen(true);
        }

        refreshExpression();
    }
    
    public void refreshExpression() {
        Expression expr = getExpression();
        if (expr instanceof PropertyName) {
            PropertyName property = (PropertyName) expr;
            String name = property.getPropertyName();
            refreshControls(null, name, null);
            feedback();
            return;
        } else if (expr instanceof Literal) {
            Literal literal = (Literal) expr;
            Double percent = literal.evaluate(null, Double.class);
            if (percent != null) {
                refreshControls(percent, null, null);
                feedback();
                return;
            }
        }
        // We cannot display this expression - put up a warning
        String message = ECQL.toCQL(expr);
        refreshControls(null, null, message);
        feedbackReplace( expr );
    }

    protected void refreshControls(final Double number, final String propertyName, final String message) {
        if (control != null && !control.isDisposed()) {
            control.getDisplay().asyncExec(new Runnable() {
                public void run() {
                    if (control == null || control.isDisposed()) {
                        return; // must of been disposed while in the display thread queue
                    }
                    boolean isPercent = input != null && input.isPercent();
                    
                    combo.setInput(input.getNumericPropertyList());

                    if (number != null) {
                        spinner.setEnabled(true);
                        int value;
                        if(isPercent){
                            value = (int) (number * 100.0);
                        }
                        else {
                            value = (int) Math.round( number );
                        }
                        spinner.setSelection(value);
                        if (message == null) {
                            if( isPercent ){
                                text.setText( value +"%");
                            }
                            else {
                                text.setText( String.valueOf(value) );
                            }
                        }
                    } else {
                        spinner.setEnabled(false);
                        spinner.setSelection(0);
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
            this.expression = newExpression;
            refreshExpression();
        }
    }

}
