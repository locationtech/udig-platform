package net.refractions.udig.ui.filter;

import java.awt.Color;
import java.util.SortedSet;
import java.util.TreeSet;

import net.refractions.udig.ui.filter.ViewerFactory.Appropriate;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.util.Converters;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;

/**
 * ExpressionViewer with Slider controls to allow the user to change the colour attribute of an
 * Expression
 * 
 * @author Scott
 * @since 1.3.0
 */
public class RGBExpressionViewer extends IExpressionViewer {
    private final int MIN = 0;
    private final int MAX = 255;

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
                Color color = literal.evaluate(null, Color.class);
                if (color != null) {
                    return Appropriate.APPROPRIATE.getScore();
                }
            }
            return Appropriate.INCOMPLETE.getScore();
        }

        @Override
        public IExpressionViewer createViewer(Composite parent, int style) {
            return new RGBExpressionViewer(parent, style);
        }
    }

    Composite control;

    protected Scale red;

    protected Text redVal;

    protected Scale blue;

    protected Text blueVal;

    protected Scale green;

    protected Text greenVal;

    protected Text hex;
    
    /**
     * Used to {@link #validate()} when any of the Scale controls change.
     */
    private SelectionListener listener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            Expression newExpression = validate();
            if( newExpression != null ){
                internalUpdate( newExpression );
            }
        }
        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
        }
    };

    protected void listen(boolean listen) {
        if (listen) {
            red.addSelectionListener(listener);
            green.addSelectionListener(listener);
            blue.addSelectionListener(listener);
        } else {
            red.removeSelectionListener(listener);
            green.removeSelectionListener(listener);
            blue.removeSelectionListener(listener);
        }
    }

    public RGBExpressionViewer(Composite parent, int style) {
        control = new Composite(parent, style);

        red = new Scale(control, SWT.NONE);
        red.setBounds(71, 8, 170, 38);
        red.setMaximum(MAX);
        red.setMinimum(MIN);

        green = new Scale(control, SWT.NONE);
        green.setBounds(71, 52, 170, 38);
        green.setMaximum(MAX);
        green.setMinimum(MIN);

        blue = new Scale(control, SWT.NONE);
        blue.setBounds(71, 96, 170, 38);
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

        listen(true);
    }

    @Override
    public Control getControl() {
        return control;
    }

    public Expression validate() {
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
        Expression expression = ff.literal(hexStr);

        return expression;
    }
    /** Used to supply a filter for display or editing */
    @Override
    public void setExpression(Expression expression) {
        if (this.expression == expression) {
            return;
        }
        this.expression = expression;
        refreshExpression();
        fireSelectionChanged(new SelectionChangedEvent(this, getSelection()));
    }
    @Override
    public void refresh() {
        if (input != null) {
            boolean isColor = Color.class.isAssignableFrom( input.getBinding() );
            if( isColor ){
                feedback();
                control.setEnabled(true);
            }
            else {
                feedback("Used with colour");
                control.setEnabled(false);
            }
        }
        refreshExpression();
    }
    public void refreshExpression() {
        if (hex != null && !hex.isDisposed()) {
            hex.getDisplay().asyncExec(new Runnable() {
                public void run() {
                    if (hex == null || hex.isDisposed()) {
                        return; // must of been disposed while we are in the queue
                    }
                    try {
                        listen(false); // don't listen while updating controls
                        Expression expr = getExpression();

                        if (expr instanceof Literal) {
                            Literal literal = (Literal) expr;
                            Color color = literal.evaluate(null, Color.class);
                            if (color != null) {
                                feedback();
                                red.setSelection(color.getRed());
                                redVal.setText(String.valueOf(color.getRed()));
                                green.setSelection(color.getGreen());
                                greenVal.setText(String.valueOf(color.getGreen()));
                                blue.setSelection(color.getBlue());
                                blueVal.setText(String.valueOf(color.getBlue()));
                                String text = Converters.convert(color, String.class);
                                if (text != null) {
                                    hex.setText(text);
                                } else {
                                    hex.setText(Integer.toHexString(color.getRGB()));
                                }
                                return; // literal color displayed!
                            }
                        }
                        String cql = ECQL.toCQL(expr);
                        hex.setText(cql);
                        feedback("Used to define a colour.\n"+cql+"\nYou can replace the current value");
                    } finally {
                        listen(true);
                    }
                }
            });
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

}
