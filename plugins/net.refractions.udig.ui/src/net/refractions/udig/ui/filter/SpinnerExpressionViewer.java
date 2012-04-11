package net.refractions.udig.ui.filter;

import java.awt.Color;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.util.Converters;
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
 * The original has as spinner (from 0 to 100%) followed by an Attribute Combo. 
 * <p>
 * This implementation has a spinner which is enabled when a range (0 to 100%) is provided; an
 * Attribute combo which is enabled when a schema is provided.
 * 
 * @author Scott
 * @since 1.3.0
 */
public class SpinnerExpressionViewer extends IExpressionViewer {
    
    /**
     * Factory used for the general purpose DefaultExpressionViewer.
     * @author jody
     * @since 1.2.0
     */
    public static class Factory extends ExpressionViewerFactory {
        @Override
        public int appropriate( SimpleFeatureType schema, Expression expression ) {
            if( expression instanceof Literal){
                Literal literal = (Literal) expression;
                Double number = literal.evaluate(null,Double.class);
                if( number != null ){
                    if( number >= 0 && number <= 1.0 ){
                        return APPROPRIATE;
                    }
                }
            }
            if( expression instanceof PropertyName ){
                PropertyName name = (PropertyName) expression;
                AttributeDescriptor descriptor = schema.getDescriptor( name.getPropertyName() );
                if (descriptor != null ){
                    Class<?> binding = descriptor.getType().getBinding();
                    if( Number.class.isAssignableFrom(binding)){
                        return APPROPRIATE;
                    }
                }
            }
            return INCOMPLETE;
        }
        @Override
        public IExpressionViewer createViewer( Composite parent, int style ) {
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

    private SelectionListener listener = new SelectionListener(){

        @Override
        public void widgetSelected( SelectionEvent e ) {
            validate();
        }

        @Override
        public void widgetDefaultSelected( SelectionEvent e ) {

        }
    };

    public SpinnerExpressionViewer( Composite parent, int style ) {
        super(parent);
        
        control = new Composite(parent, style);
        combo = new ComboViewer(control, SWT.DEFAULT);
        combo.setContentProvider( ArrayContentProvider.getInstance() );
        combo.setLabelProvider( new LabelProvider(){
            @Override
            public String getText( Object element ) {
                if( element instanceof PropertyDescriptor ){
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
        if (expr == null) return null;

        IStructuredSelection selection = new StructuredSelection(expr);
        return selection;
    }

    @Override
    public void refresh() {
        if (control != null && !control.isDisposed()) {
            if( expr instanceof PropertyName ){
                PropertyName property = 
            }
            IStructuredSelection selection = (IStructuredSelection) combo.getSelection();
            Object item = selection.getFirstElement();
            if( item instanceof PropertyDescriptor ){
                PropertyDescriptor descriptor = (PropertyDescriptor) item;
                
                expr 
            }
            hex.getDisplay().asyncExec(new Runnable(){
                public void run() {
                    if (hex == null || hex.isDisposed()) return;
                    String cql = CQL.toCQL(expr);
                    hex.setText(cql);
                }
            });
        }
    }

    @Override
    public void setInput( Object input ) {
        if (input instanceof Expression) {
            expr = (Expression) input;
            refresh();
        }
    }

    @Override
    public void setSelection( ISelection selection, boolean reveal ) {
        // TODO Auto-generated method stub

    }

    @Override
    public void feedback() {
        // TODO Auto-generated method stub

    }

    @Override
    public void feedback( String warning ) {
        // TODO Auto-generated method stub

    }

    @Override
    public void feedback( String exception, Exception eek ) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setSchema( SimpleFeatureType schema ) {
        this.type = schema;
    }

    @Override
    public SimpleFeatureType getSchema() {

        return type;
    }

    @Override
    public void setExpected( Class< ? > binding ) {
        // TODO Auto-generated method stub

    }

    @Override
    public Class< ? > getExpected() {
        // TODO Auto-generated method stub
        return null;
    }
}
