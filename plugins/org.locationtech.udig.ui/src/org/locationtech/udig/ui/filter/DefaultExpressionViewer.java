/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.ui.filter;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.locationtech.udig.ui.internal.Messages;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;

import net.miginfocom.swt.MigLayout;

/**
 * A very simple {@link IFilterViewer} using a text with Constraint Query Language and a few combo
 * boxes to help with suggestions.
 * <p>
 * Remember that although Viewers are a wrapper around some SWT Control or Composite you still have
 * direct access using the getControl() method so that you can do your layout data thing.
 * </p>
 *
 * @author Jody Garnett
 * @since 1.2.0
 */
public class DefaultExpressionViewer extends CQLExpressionViewer {

    /**
     * Factory used to hook this into filterViewer extension point.
     *
     * @see FilterViewer for details of programatic use
     *
     * @author Jody Garnett
     * @since 1.3.2
     */
    public static class Factory extends ExpressionViewerFactory {
        @Override
        public int score(ExpressionInput input, Expression expression) {
            return Appropriate.COMPLETE.getScore(1);
        }

        @Override
        public IExpressionViewer createViewer(Composite parent, int style) {
            return new DefaultExpressionViewer(parent, style);
        }
    }

    /**
     * This is the expression we are working on here.
     * <p>
     * We are never going to be "null"; Expression.EXCLUDE is used to indicate an intentionally
     * empty expression.
     * </p>
     */
    protected Filter filter = Filter.EXCLUDE;

    protected Composite control;

    private Label lblAttribute;

    /**
     * Combo box to allow the user to select an attribute to base a filter on
     */
    protected Combo attribute;

    private Label lblOperation;

    /**
     * Combo box to allow the user to select an operation to apply to an attribute
     */
    protected Combo operation;

    private Label lblValue;

    /**
     * Text box to allow the user to enter a value to base a filter on
     */
    protected Combo value;

    private static final String ADD = "+"; //$NON-NLS-1$

    private static final String SUB = "-"; //$NON-NLS-1$

    private static final String MUL = "*"; //$NON-NLS-1$

    private static final String DIV = "/"; //$NON-NLS-1$

    private static final String[] OPERATORS = { ADD, SUB, MUL, DIV };

    private SelectionAdapter comboListener = new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            if (e.widget instanceof Combo) {
                final Combo combo = (Combo) e.widget;
                if (combo.getSelectionIndex() != -1) {
                    text.insert(combo.getText());
                    text.setFocus();
                    combo.deselectAll();
                    changed();
                }
            }
        }
    };

    /**
     * Creates ExpressionViewer using the provided style.
     * <ul>
     * <li>SWT.SINGLE - viewer is restricted to a single line</li>
     * <li>SWT.MULTI - viewer is able to assume additional vertical space is available</li>
     * <li>SWT.READ_ONLY - read only</li>
     * </ul>
     *
     * @param parent composite viewer is being added to
     * @param style used to layout the viewer
     */
    public DefaultExpressionViewer(Composite parent, int style) {
        super(new Composite(parent, SWT.NO_SCROLL) {
            @Override
            public void setEnabled(boolean enabled) {
                super.setEnabled(enabled);
                for (Control child : getChildren()) {
                    child.setEnabled(enabled);
                }
            }
        }, style);

        control = text.getParent(); // refers to to the composite created above
        boolean isMultiline = (SWT.MULTI & style) != 0;

        lblAttribute = null;
        if (isMultiline) {
            lblAttribute = new Label(control, SWT.NONE);
            lblAttribute.setText(Messages.DefaultExpressionViewer_attribute);
        }
        attribute = new Combo(control, SWT.DROP_DOWN | SWT.READ_ONLY);
        attribute.addSelectionListener(comboListener);

        lblOperation = null;
        if (isMultiline) {
            lblOperation = new Label(control, SWT.NONE);
            lblOperation.setText(Messages.DefaultExpressionViewer_operation);
        }
        operation = new Combo(control, SWT.DROP_DOWN | SWT.READ_ONLY);
        operation.setItems(OPERATORS);
        operation.addSelectionListener(comboListener);

        lblValue = null;
        if (isMultiline) {
            lblValue = new Label(control, SWT.NONE);
            lblValue.setText(Messages.DefaultExpressionViewer_value);
        }
        value = new Combo(control, SWT.DROP_DOWN | SWT.READ_ONLY);
        value.addSelectionListener(comboListener);
        value.setEnabled(false);

        setLayout(isMultiline);

    }

    /**
     * This sets the layout of the UI elements in the viewer.
     *
     * @param isMultiline
     */
    private void setLayout(boolean isMultiline) {
        if (isMultiline) {
            MigLayout layout = new MigLayout("insets 0", "[][][][][][][grow]", "[grow][]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            control.setLayout(layout);

            text.setLayoutData("cell 0 0,span,grow,width 200:100%:100%,height 60:100%:100%"); //$NON-NLS-1$
            setPreferredTextSize(40, 5);

            lblAttribute.setLayoutData("cell 0 1,alignx trailing,gapx related"); //$NON-NLS-1$
            attribute.setLayoutData("cell 1 1,wmin 60,alignx left,gapx rel"); //$NON-NLS-1$

            lblOperation.setLayoutData("cell 2 1,alignx trailing,gapx related"); //$NON-NLS-1$
            operation.setLayoutData("cell 3 1,wmin 60,alignx left,gapx rel"); //$NON-NLS-1$

            lblValue.setLayoutData("cell 4 1,alignx trailing,gapx related"); //$NON-NLS-1$
            value.setLayoutData("cell 5 1,wmin 60,alignx left,gapx related"); //$NON-NLS-1$
        } else {
            control.setLayout(new MigLayout("insets 0, flowx", "", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

            text.setLayoutData("grow,width 200:70%:100%, gap unrelated"); //$NON-NLS-1$
            attribute.setLayoutData("width 90:20%:100%, gap related"); //$NON-NLS-1$
            operation.setLayoutData("width 60:10%:100%, gap related"); //$NON-NLS-1$
            value.setLayoutData("width 60:10%:100%, gap related"); //$NON-NLS-1$
        }
    }

    /**
     * This is the widget used to display the Filter; its parent has been provided in the
     * ExpressionViewer's constructor; but you may need direct access to it in order to set layout
     * data etc.
     *
     * @return control used to display the filter
     */
    @Override
    public Control getControl() {
        return control;
    }

    /**
     * This refreshes the viewer's UI elements (combo options, etc.) this should be called in
     * response to setting the input to re-populate the attribute and value drop downs.
     */
    @Override
    public void refresh() {
        super.refresh();
        if (input != null) {
            // Set attribute options
            final SortedSet<String> names = new TreeSet<>(input.toPropertyList());
            attribute.setItems(names.toArray(new String[names.size()]));
            // Set value options
            final List<Object> options = input.getOptions();
            if (options != null && !options.isEmpty()) {
                value.setItems(new String[0]);
                for (Object option : options) {
                    if (option != null) {
                        value.add(option.toString());
                    }
                }
                value.setEnabled(true);
            } else {
                value.setEnabled(false);
            }
        }
    }

}
