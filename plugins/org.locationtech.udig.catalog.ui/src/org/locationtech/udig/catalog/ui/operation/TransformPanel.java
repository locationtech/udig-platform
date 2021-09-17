/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui.operation;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swt.MigLayout;
import org.locationtech.udig.internal.ui.UDigByteAndLocalTransfer;
import org.locationtech.udig.ui.filter.ExpressionInput;
import org.locationtech.udig.ui.filter.ExpressionViewer;
import org.locationtech.udig.ui.filter.IExpressionViewer;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.process.vector.TransformProcess;
import org.geotools.process.vector.TransformProcess.Definition;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;

/**
 * Panel that can be places in a UI used to ask the user to enter in a series of expression for use
 * with the Transform process.
 *
 * @author leviputna
 */
public class TransformPanel extends Composite {

    /**
     * A definition value has been modified
     */
    public static final String MODIFY = "Modify";

    /**
     * The list of definitions has been reorded
     */
    public static final String ORDER = "Order";

    private static final String NO_CONTENT = "--";

    private List<TransformProcess.Definition> transform;

    private ControlDecoration feedbackDecorator;

    private SimpleFeature sample;

    private Text name;

    private IExpressionViewer expression;

    private SimpleFeatureType schema;

    private Composite composite;

    /**
     * List of change listeners
     *
     * @see #fireChanged
     */
    private ListenerList changedListeners = new ListenerList();

    static List<Definition> createDefaultTransformDefinition(SimpleFeatureType featureType) {
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
        List<Definition> list = new ArrayList<TransformProcess.Definition>();
        if (featureType != null) {
            for (AttributeDescriptor descriptor : featureType.getAttributeDescriptors()) {
                Definition definition = new Definition();

                definition.name = descriptor.getLocalName();
                definition.binding = descriptor.getType().getBinding();
                definition.expression = ff.property(descriptor.getName());

                list.add(definition);
            }
        }
        return list;
    }

    /**
     * viewer used to review {@link #transform}
     */
    private TableViewer table;

    private ModifyListener nameListener = new ModifyListener() {
        public void modifyText(ModifyEvent e) {
            Definition definition = selectedDefinition();
            if( definition != null ){
                String text = name.getText();
                if (definition.name == null || !definition.name.equals(text)) {
                    definition.name = text;
                }
            }
            // refresh the display, including labels and display the row if needed
            table.refresh(definition, true, true);;
            fireChanged(new ChangeEvent(transform));
        }
    };

    private ISelectionChangedListener expressionListener = new ISelectionChangedListener() {
        public void selectionChanged(SelectionChangedEvent event) {
            Definition definition = selectedDefinition();
            Expression expr = expression.getExpression();

            if (definition.expression == null || !definition.equals(expr)) {
                definition.expression = expr;

                try {
                    Object value = definition.expression.evaluate(sample);
                    definition.binding = value.getClass();
                } catch (Throwable t) {
                    definition.binding = null; // unknown
                }
                // refresh the display, including labels and display the row if needed
                table.refresh(definition, true, true);
            }
        }
    };

    private ISelectionChangedListener tableListener = new ISelectionChangedListener() {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            Definition definition = selectedDefinition();

            listen(false);
            try {
                if (definition == null) {
                    name.setText("");
                    expression.setExpression(Expression.NIL);

                    enable(false);
                } else {
                    name.setText(definition.name);
                    expression.setExpression(definition.expression);

                    enable(true);
                }
            } finally {
                listen(true);
            }
        }
    };

    private Label definitionLabel;

    /**
     * Create the composite UI elements.
     * <p>
     * Only UI elements that are common to all implementations of this Composite should be added
     * here, Finish, Next and Done buttons are specific to form or wizard implementations and should
     * not be added here.
     * </p>
     * <p>
     * Recommend using the Design tab as it will help maintain layout.
     * </p>
     *
     * @param parent
     * @param style
     */
    public TransformPanel(Composite parent, int style) {
        super(parent, style);

        // setup the table part of the panel
        createExpressionTable(parent);
        this.listen(true);
    }

    public void addChangedListener(ChangeListener listener) {
        changedListeners.add(listener);
    }

    public void removeChangedListener(ChangeListener listener) {
        changedListeners.remove(listener);
    }

    /**
     * Notifies any selection changed listeners that the viewer's selection has changed. Only
     * listeners registered at the time this method is called are notified.
     *
     * @param changeEvent a selection changed event
     *
     * @see ISelectionChangedListener#selectionChanged
     */
    protected void fireChanged(final ChangeEvent changeEvent) {
        Object[] listeners = changedListeners.getListeners();
        for (int i = 0; i < listeners.length; ++i) {
            ((ChangeListener) listeners[i]).stateChanged(changeEvent);
        }
    }

    public void setInput(SimpleFeature sample) {
        listen(false);
        this.schema = sample.getFeatureType();
        this.sample = sample;
        this.transform = createDefaultTransformDefinition(schema);

        table.setInput(this.transform);
        table.refresh();

        ExpressionInput expressionInput = new ExpressionInput(schema, true);
        expressionInput.setFeedback(feedbackDecorator);
        expression.setInput(expressionInput);
        expression.refresh();
        listen(true);
    }

    /**
     * Get the Composite containing the UI
     *
     * @return the composite
     */
    public Composite getComposite() {
        return composite;
    }

    /**
     * Transform process definition;
     *
     * @return
     */
    public List<TransformProcess.Definition> getTransform() {
        return transform;
    }

    protected Control createExpressionTable(Composite parent) {
        setLayout(new MigLayout("insets 0", "[grow,fill][]",
                "[][][][][grow,fill][][][][grow,fill][][]"));

        Label label = new Label(this, SWT.LEFT);
        label.setText("Transform");
        label.setLayoutData("cell 0 0 2 1,width pref!,left");

        Button button = new Button(this, SWT.CENTER);
        button.setText("Add");
        button.setLayoutData("cell 1 1 1 1,grow");
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int row = table.getTable().getSelectionIndex();
                Definition definition = new Definition();
                definition.name = "";
                definition.expression = Expression.NIL;
                transform.add(row, definition);
                table.refresh();
                table.setSelection(new StructuredSelection(definition));
            }
        });

        button = new Button(this, SWT.CENTER);
        button.setText("Up");
        button.setLayoutData("cell 1 2 1 1,grow");
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int row = table.getTable().getSelectionIndex();
                if (row == 0 || row == -1) {
                    return;
                }
                row--;
                Definition definition = selectedDefinition();
                transform.remove(definition);
                transform.add(row, definition);
                table.refresh();
                table.setSelection(new StructuredSelection(definition));
            }
        });

        button = new Button(this, SWT.CENTER);
        button.setText("Down");
        button.setLayoutData("cell 1 3 1 1,grow");
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int row = table.getTable().getSelectionIndex();
                if (row == transform.size() - 1 || row == -1) {
                    return;
                }
                row++;
                Definition definition = selectedDefinition();
                transform.remove(definition);
                transform.add(row, definition);
                table.refresh();
                table.setSelection(new StructuredSelection(definition));
            }
        });

        button = new Button(this, SWT.CENTER);
        button.setText("Remove");
        button.setLayoutData("cell 1 5 1 1,grow");
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int row = table.getTable().getSelectionIndex();
                if (row == -1) {
                    return;
                }
                if (row > 0) {
                    row = row - 1;
                }
                Definition definition = selectedDefinition();
                transform.remove(definition);
                table.refresh();

                if (row < transform.size()) {
                    table.getTable().setSelection(row);
                } else {
                    table.getTable().deselectAll();
                }
            }
        });

        table = new TableViewer(this, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        table.setContentProvider(ArrayContentProvider.getInstance());
        table.getControl().setLayoutData(
                "cell 0 1 1 5, grow, height 200:50%:70%,width 300:pref:100%");

        TableViewerColumn column = new TableViewerColumn(table, SWT.NONE);
        column.getColumn().setWidth(100);
        column.getColumn().setMoveable(false);
        column.getColumn().setResizable(true);
        column.getColumn().setText("Attribute");
        column.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Definition definition = (Definition) element;
                return definition.name;
            }
        });
        column = new TableViewerColumn(table, SWT.NONE);
        column.getColumn().setWidth(60);
        column.getColumn().setMoveable(false);
        column.getColumn().setResizable(true);
        column.getColumn().setText("Type");
        column.getColumn().setAlignment(SWT.CENTER);
        column.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Definition definition = (Definition) element;
                return definition.binding == null ? NO_CONTENT : definition.binding.getSimpleName();
            }
        });
        column = new TableViewerColumn(table, SWT.NONE);
        column.getColumn().setWidth(140);
        column.getColumn().setMoveable(false);
        column.getColumn().setResizable(true);
        column.getColumn().setText("Expression");
        column.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Definition definition = (Definition) element;
                return definition.expression == null ? NO_CONTENT : ECQL
                        .toCQL(definition.expression);
            }
        });
        table.getTable().setHeaderVisible(true);
        table.getTable().setLinesVisible(true);
        table.addSelectionChangedListener(tableListener);

        Transfer[] types = new Transfer[] { UDigByteAndLocalTransfer.getInstance() };
        table.addDragSupport(DND.DROP_MOVE | DND.DROP_DEFAULT, types, new DragSourceAdapter() {
            @Override
            public void dragSetData(DragSourceEvent event) {
                IStructuredSelection selection = (IStructuredSelection) table.getSelection();

                if (UDigByteAndLocalTransfer.getInstance().isSupportedType(event.dataType)) {
                    event.data = selection.getFirstElement();
                }
            }
        });

        // drag drop order support
        table.addDropSupport(DND.DROP_MOVE | DND.DROP_DEFAULT, types, new ViewerDropAdapter(table) {

            @Override
            public boolean validateDrop(Object target, int operation, TransferData transferType) {
                if (target instanceof Definition) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public boolean performDrop(Object data) {
                if (data instanceof Definition) {
                    listen(false);

                    int index = transform.indexOf(getCurrentTarget());

                    // if (location == LOCATION_BEFORE)
                    // index--;

                    Definition definition = (Definition) data;
                    transform.remove(definition);
                    transform.add(index, definition);
                    table.refresh();
                    table.setSelection(new StructuredSelection(definition));

                    listen(true);
                    return true;
                }
                return false;
            }
        });

        definitionLabel = new Label(this, SWT.LEFT);
        definitionLabel.setText("Definition");
        definitionLabel.setLayoutData("cell 0 6 2 1, width pref!,left");

        feedbackDecorator = new ControlDecoration(definitionLabel, SWT.RIGHT | SWT.TOP);

        name = new Text(this, SWT.SINGLE | SWT.BORDER);
        name.setEditable(true);
        name.setText("");
        name.setLayoutData("cell 0 7 2 1");

        expression = new ExpressionViewer(this, SWT.MULTI);
        // expression.setInput(expressionInput);
        expression.getControl()
                .setLayoutData("cell 0 8 2 1,height 200:50%:50%,width 300:pref:100%");
        expression.addSelectionChangedListener(expressionListener);

        // start up with nothing selected
        table.setSelection(StructuredSelection.EMPTY);
        enable(false);
        return this;
    }

    protected void listen(boolean listen) {
        if (listen) {
            name.addModifyListener(nameListener);
            expression.addSelectionChangedListener(expressionListener);
        } else {
            name.removeModifyListener(nameListener);
            expression.removeSelectionChangedListener(expressionListener);
        }
    }

    protected void enable(boolean isEditEnable) {
        definitionLabel.setEnabled(isEditEnable);
        name.setEnabled(isEditEnable);
        expression.getControl().setEnabled(isEditEnable);
    }

    protected Definition selectedDefinition() {
        ISelection selectedRow = table.getSelection();
        if (!selectedRow.isEmpty() && selectedRow instanceof StructuredSelection) {
            StructuredSelection selection = (StructuredSelection) selectedRow;
            return (Definition) selection.getFirstElement();
        }
        return null; // nothing to see here
    }

}
