package net.refractions.udig.ui.filter;

import java.awt.Color;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.cql2.CQL;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;

/**
 * ExpressionViewer with Slider controls to allow the user to change the colour attribute of an
 * Expression
 * 
 * @author Scott
 * @since 1.3.0
 */
public class RGBExpressionViewer extends IExpressionViewer {

    /**
     * This is the expression we are working on here.
     * <p>
     * We are never going to be "null"; Expression.NIL is used to indicate an intentionally empty
     * expression.
     */
    protected Expression expr = Expression.NIL;

    Composite control;

    protected Scale red;
    protected Scale blue;
    protected Scale green;

    protected Text hex;
    protected Text redVal;
    protected Text blueVal;
    protected Text greenVal;

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

    public RGBExpressionViewer( Composite parent, int style ) {
        super(parent);
        control = new Composite(parent, style);

        red = new Scale(control, SWT.NONE);
        red.setBounds(71, 8, 170, 38);
        red.addSelectionListener(listener);
        red.setMaximum(MAX);
        red.setMinimum(MIN);

        green = new Scale(control, SWT.NONE);
        green.setBounds(71, 52, 170, 38);
        green.addSelectionListener(listener);
        green.setMaximum(MAX);
        green.setMinimum(MIN);

        blue = new Scale(control, SWT.NONE);
        blue.setBounds(71, 96, 170, 38);
        blue.addSelectionListener(listener);
        blue.setMaximum(MAX);
        blue.setMinimum(MIN);

        Label lblRed = new Label(control, SWT.NONE);
        lblRed.setBounds(10, 20, 55, 15);
        lblRed.setText("Red");

        Label lblGreen = new Label(control, SWT.NONE);
        lblGreen.setText("Green");
        lblGreen.setBounds(10, 63, 55, 15);

        Label lblBlue = new Label(control, SWT.NONE);
        lblBlue.setText("Blue");
        lblBlue.setBounds(10, 108, 55, 15);

        hex = new Text(control, SWT.BORDER);
        hex.setBounds(71, 151, 170, 18);

        Label lblHex = new Label(control, SWT.NONE);
        lblHex.setText("Hex");
        lblHex.setBounds(10, 154, 55, 15);

        redVal = new Text(control, SWT.BORDER);
        redVal.setBounds(247, 17, 40, 21);

        greenVal = new Text(control, SWT.BORDER);
        greenVal.setBounds(247, 60, 40, 21);

        blueVal = new Text(control, SWT.BORDER);
        blueVal.setBounds(247, 105, 40, 21);
    }

    @Override
    public Control getControl() {
        return control;
    }

    /**
     * The isRequired flag will be used to determine the default decoration to show (if there is no
     * warning or error to take precedence).
     * <p>
     * Please note that if this is a required field Expression.NIL is not considered to be a valid
     * state.
     * </p>
     * 
     * @param isRequired true if this is a required field
     */
    public void setRequired( boolean isRequired ) {
        this.isRequired = isRequired;
    }

    @Override
    public boolean isRequired() {
        return isRequired;
    }

    public boolean validate() {

        int r = red.getSelection();
        int g = green.getSelection();
        int b = blue.getSelection();

        redVal.setText(String.valueOf(r));
        greenVal.setText(String.valueOf(g));
        blueVal.setText(String.valueOf(b));

        Color c = new Color(r, g, b);
        String hexStr = Integer.toHexString(c.getRGB() & 0x00ffffff);
        hex.setText(hexStr);

        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
        expr = ff.literal(hexStr);

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
        if (hex != null && !hex.isDisposed()) {
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
