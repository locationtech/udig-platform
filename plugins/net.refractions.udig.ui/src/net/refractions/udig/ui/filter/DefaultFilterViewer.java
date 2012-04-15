/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.ui.filter;

import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;

/**
 * A very simple {@link IFilterViewer} using a text with Constraint Query Language.
 * <p>
 * Remember that although Viewers are a wrapper around some SWT Control or Composite you still have
 * direct access using the getControl() method so that you can do your layout data thing.
 * </p>
 * 
 * @author Jody Garnett
 * @since 1.2.0
 */
public class DefaultFilterViewer extends IFilterViewer {
    /**
     * Factory used for the general purpose CQLFilterViewer.
     * 
     * @author jody
     * @since 1.2.0
     */
    public static class Factory extends FilterViewerFactory {
        @Override
        /**
         * Slightly prefer CQLFilterViewer to DefaultFilterViewer.
         */
        public int appropriate(SimpleFeatureType schema, Filter filter) {
            return COMPLETE + 1;
        }

        public IFilterViewer createViewer(Composite parent, int style) {
            return new DefaultFilterViewer(parent, style);
        }
    }

    /**
     * This is the expression we are working on here.
     * <p>
     * We are never going to be "null"; Expression.EXCLUDE is used to indicate an intentionally
     * empty expression.
     */
    protected Filter filter = Filter.EXCLUDE;

    protected Composite control;

    /**
     * This is our internal widget we are sharing with the outside world; in many cases it will be a
     * Composite.
     */
    private Text text;

    /**
     * Combo box to allow the user to select an attribute to base a filter on
     */
    private Combo attribute;

    /**
     * Combo box to allow the user to select an operation to apply to an attribute
     */
    private Combo operation;

    /**
     * Text box to allow the user to enter a value to base a filter on
     */
    private Combo value;

    private Button insert;

    private KeyListener keyListener = new KeyListener() {
        public void keyReleased(KeyEvent e) {
            changed();
        }

        public void keyPressed(KeyEvent e) {
        }
    };

    private FunctionContentProposalProvider proposalProvider;

    /**
     * Creates an ExpressionViewer using the provided style.
     * <ul>
     * <li>SWT.SINGLE - A simple text field showing the expression using extended CQL notation; may
     * be shown as a combo box if either a FeatureType or DialogSettings are provided
     * <li>SWT.MULTI - A multi line text field; may be shown as an ExpressionBuilder later on
     * <li>SWT.READ_ONLY - read only
     * <li>
     * <li>SWT.WRAP - useful with SWT.MULTI
     * <li>SWT.LEFT - alignment
     * <li>SWT.RIGHT - alignment
     * <li>SWT.CENTER - alignment
     * </ul>
     * 
     * @param parent
     * @param none
     */
    public DefaultFilterViewer(Composite parent, int style) {
        super(parent, style);
        control = new Composite(parent, style);

        int textStyle = style == SWT.DEFAULT ? SWT.BORDER : SWT.MULTI | SWT.V_SCROLL | SWT.BORDER;

        text = new Text(control, textStyle);
        text.setBounds(10, 10, 430, 60);
        // feedback = new ControlDecoration(text, SWT.TOP | SWT.LEFT);

        proposalProvider = new FunctionContentProposalProvider();
        ContentProposalAdapter adapter = new ContentProposalAdapter(text, new TextContentAdapter(),
                proposalProvider, null, null);

        // Need to set adapter to replace existing text. Default is insert.
        adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_INSERT);

        text.addKeyListener(keyListener);

        Label lblAttribute = new Label(control, SWT.NONE);
        lblAttribute.setBounds(10, 85, 55, 15);
        lblAttribute.setText("Attribute");

        attribute = new Combo(control, SWT.SIMPLE | SWT.READ_ONLY );
        
        attribute.setBounds(10, 105, 91, 23);
        attribute.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String attributeName = attribute.getText();

                // populate values based on this attribute
                if (attributeName != null && getInput() != null && getInput().getSchema() != null) {
                    SimpleFeatureType schema = getInput().getSchema();
                    AttributeDescriptor descriptor = schema.getDescriptor(attributeName);

                    value.removeAll();

                    SortedSet<String> options = new TreeSet<String>();

                    Object defaultValue = descriptor.getDefaultValue();
                    if (defaultValue != null) {
                        options.add(String.valueOf(defaultValue));
                    }
                    AttributeType type = descriptor.getType();
                    if (Number.class.isAssignableFrom(type.getBinding())) {
                        options.add("0");
                        options.add("1");
                        options.add("2");
                        options.add("3");
                        options.add("4");
                        options.add("5");
                    }
                    if (options.isEmpty()) {
                        value.setEnabled(false);
                    } else {
                        value.setEnabled(true);
                        for (String item : options) {
                            value.add(item);
                        }
                    }
                } else {
                    value.removeAll();
                    value.setEnabled(false);
                }
            }
        });

        Label lblOperation = new Label(control, SWT.NONE);
        lblOperation.setBounds(124, 85, 55, 15);
        lblOperation.setText("Operation:");

        operation = new Combo(control, SWT.SIMPLE | SWT.READ_ONLY );

        operation.setBounds(124, 105, 91, 23);
        operation.add("=");
        operation.add("<");
        operation.add(">");
        operation.add("LIKE");

        Label lblValue = new Label(control, SWT.NONE);
        lblValue.setBounds(241, 85, 55, 15);
        lblValue.setText("Value");

        value = new Combo(control, SWT.SIMPLE | SWT.READ_ONLY );
        value.setBounds(241, 105, 91, 23);

        insert = new Button(control, SWT.NONE);
        insert.setBounds(354, 103, 75, 25);
        insert.setText("Insert");
        insert.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (attribute.isFocusControl() && attribute.getSelectionIndex() != -1) {
                    String selectedAttribute = attribute.getText();
                    text.insert(selectedAttribute);

                    attribute.clearSelection();
                    changed();
                    text.setFocus();
                    return;
                }

                if (operation.isFocusControl() && operation.getSelectionIndex() != -1) {
                    String selectedOperation = operation.getText();
                    text.insert(selectedOperation);

                    operation.clearSelection();

                    changed();
                    text.setFocus();
                    return;
                }

                if (value.isFocusControl() && value.getSelectionIndex() != -1) {
                    String selectedValue = value.getText();
                    text.insert(selectedValue);

                    value.clearSelection();

                    changed();
                    text.setFocus();
                    return;
                }
            }
        });

    }

    /**
     * This is the widget used to display the Expression; its parent has been provided in the
     * ExpressionViewer's constructor; but you may need direct access to it in order to set layout
     * data etc.
     * 
     * @return
     */
    public Control getControl() {
        return control;
    }

    protected void changed() {
        Filter parsedFilter = validate();
        if (parsedFilter != null) {
            internalUpdate(parsedFilter);
        }
    }

    /**
     * Check if the expr is valid.
     * <p>
     * The default implementation checks that the expr is not null (which would be an error); and
     * that if isRequired is true that a required decoration is shown.
     * <p>
     * Subclasses can overide to perform additional checks (say for entering dates). They should
     * take care to use the feedback decoration in order to indicate to the user any problems
     * encountered.
     * 
     * @return true if the field is valid
     */
    protected Filter validate() {
        Filter parsedFilter;
        try {
            parsedFilter = ECQL.toFilter(text.getText());
        } catch (CQLException e) {
            feedback(e.getLocalizedMessage(), e);
            return null;
        }
        if (parsedFilter == null) {
            feedback("(empty)");
            return null;
        }
        if (input != null && input.isRequired() && filter == Expression.NIL) {
            feedback("Required", true);
            return null;
        }
        feedback();
        return parsedFilter;
    }

    @Override
    public void refresh() {
        if (input != null) {
            SortedSet<String> names = new TreeSet<String>(input.toPropertyList());
            proposalProvider.setExtra(names);

            attribute.setItems(names.toArray(new String[names.size()]));

        }
        refreshFilter();
    }

    private void refreshFilter() {
        if (text != null && !text.isDisposed()) {
            text.getDisplay().asyncExec(new Runnable() {
                public void run() {
                    if (text == null || text.isDisposed())
                        return;

                    if (filter == null) {
                        text.setText("");
                    } else {
                        String cql = CQL.toCQL(filter);
                        text.setText(cql);
                    }
                }
            });
        }
    }

    @Override
    public void setFilter(Filter filter) {
        if (this.filter == filter) {
            return;
        }
        this.filter = filter;
        refreshFilter();
        fireSelectionChanged(new SelectionChangedEvent(this, getSelection()));
    }
}