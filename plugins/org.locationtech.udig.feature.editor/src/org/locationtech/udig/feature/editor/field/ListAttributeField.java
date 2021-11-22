/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.feature.editor.field;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.geotools.util.Converters;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

/**
 * Abstract attribute field managing a list of contents.
 * <p>
 * To use an implementor will need to override a couple of methods:
 * <ul>
 * <li>parseString</li>
 * <li>createList</li>
 * <li>getNewInputObject</li>
 * </p>
 *
 * @author Jody
 * @since 1.2.0
 * @see ListEditor
 */
public abstract class ListAttributeField extends AttributeField {
    /**
     * The list widget; <code>null</code> if none (before creation or after disposal).
     */
    private List list;

    /**
     * The button box containing the Add, Remove, Up, and Down buttons; <code>null</code> if none
     * (before creation or after disposal).
     */
    private Composite buttonBox;

    /**
     * The Add button.
     */
    private Button addButton;

    /**
     * The Remove button.
     */
    private Button removeButton;

    /**
     * The Up button.
     */
    private Button upButton;

    /**
     * The Down button.
     */
    private Button downButton;

    /**
     * The selection listener.
     */
    private SelectionListener selectionListener;

    /**
     * Creates a new list field editor
     */
    protected ListAttributeField() {
    }

    /**
     * Creates a list field editor.
     *
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */
    protected ListAttributeField(String name, String labelText, Composite parent) {
        init(name, labelText);
        createControl(parent);
    }

    /**
     * Notifies that the Add button has been pressed.
     */
    private void addPressed() {
        setPresentsDefaultValue(false);
        String input = getNewInputObject();

        if (input != null) {
            int index = list.getSelectionIndex();
            if (index >= 0) {
                list.add(input, index + 1);
            } else {
                list.add(input, 0);
            }
            selectionChanged();
        }
    }

    @Override
    public void adjustForNumColumns(int numColumns) {
        Control control = getLabelControl();
        ((GridData) control.getLayoutData()).horizontalSpan = numColumns;
        ((GridData) list.getLayoutData()).horizontalSpan = numColumns - 1;
    }

    /**
     * Creates the Add, Remove, Up, and Down button in the given button box.
     *
     * @param box the box for the buttons
     */
    private void createButtons(Composite box) {
        addButton = createPushButton(box, "add an Action");//$NON-NLS-1$
        removeButton = createPushButton(box, "remove an Action");//$NON-NLS-1$
        upButton = createPushButton(box, "move Action up");//$NON-NLS-1$
        downButton = createPushButton(box, "move Action down");//$NON-NLS-1$
    }

    /**
     * Combines the given list of items into a single string. This method is the converse of
     * <code>parseString</code>.
     * <p>
     * Subclasses must implement this method.
     * </p>
     *
     * @param items the list of items
     * @return the combined string
     * @see #parseString
     */
    protected abstract String createList(String[] items);

    /**
     * Helper method to create a push button.
     *
     * @param parent the parent control
     * @param key the resource name used to supply the button's label text
     * @return Button
     */
    private Button createPushButton(Composite parent, String key) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText(JFaceResources.getString(key));
        button.setFont(parent.getFont());
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        int widthHint = convertHorizontalDLUsToPixels(button, IDialogConstants.BUTTON_WIDTH);
        data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
        button.setLayoutData(data);
        button.addSelectionListener(getSelectionListener());
        return button;
    }

    private int convertHorizontalDLUsToPixels(Button control, int buttonWidth) {
        GC gc = new GC(control);
        gc.setFont(control.getFont());
        double averageWidth = gc.getFontMetrics().getAverageCharacterWidth();
        gc.dispose();

        double horizontalDialogUnitSize = averageWidth * 0.25;

        return (int) Math.round(buttonWidth * horizontalDialogUnitSize);
    }

    /**
     * Creates a selection listener.
     */
    public void createSelectionListener() {
        selectionListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Widget widget = event.widget;
                if (widget == addButton) {
                    addPressed();
                } else if (widget == removeButton) {
                    removePressed();
                } else if (widget == upButton) {
                    upPressed();
                } else if (widget == downButton) {
                    downPressed();
                } else if (widget == list) {
                    selectionChanged();
                }
            }
        };
    }

    @Override
    protected void doFillIntoGrid(Composite parent, int numColumns) {
        Control control = getLabelControl(parent);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns;
        control.setLayoutData(gd);

        list = getListControl(parent);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.verticalAlignment = GridData.FILL;
        gd.horizontalSpan = numColumns - 1;
        gd.grabExcessHorizontalSpace = true;
        list.setLayoutData(gd);

        buttonBox = getButtonBoxControl(parent);
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        buttonBox.setLayoutData(gd);
    }

    @Override
    public void doLoad() {
        if (list != null) {
            Object value = getFeature().getAttribute(getAttributeName());
            String text = Converters.convert(value, String.class);
            String[] array = parseString(text);
            for (int i = 0; i < array.length; i++) {
                list.add(array[i]);
            }
        }
    }

    @Override
    protected void doLoadDefault() {
        if (list != null) {
            list.removeAll();
            SimpleFeatureType schema = getFeature().getFeatureType();
            AttributeDescriptor descriptor = schema.getDescriptor(getAttributeName());
            Object value = descriptor.getDefaultValue();
            String text = Converters.convert(value, String.class);
            String[] array = parseString(text);
            for (int i = 0; i < array.length; i++) {
                list.add(array[i]);
            }
        }
    }

    @Override
    protected void doStore() {
        String s = createList(list.getItems());
        if (s != null) {
            getFeature().setAttribute(getAttributeName(), s);
        }
    }

    /**
     * Notifies that the Down button has been pressed.
     */
    private void downPressed() {
        swap(false);
    }

    /**
     * Returns this field editor's button box containing the Add, Remove, Up, and Down button.
     *
     * @param parent the parent control
     * @return the button box
     */
    public Composite getButtonBoxControl(Composite parent) {
        if (buttonBox == null) {
            buttonBox = new Composite(parent, SWT.NULL);
            GridLayout layout = new GridLayout();
            layout.marginWidth = 0;
            buttonBox.setLayout(layout);
            createButtons(buttonBox);
            buttonBox.addDisposeListener(new DisposeListener() {
                @Override
                public void widgetDisposed(DisposeEvent event) {
                    addButton = null;
                    removeButton = null;
                    upButton = null;
                    downButton = null;
                    buttonBox = null;
                }
            });

        } else {
            checkParent(buttonBox, parent);
        }

        selectionChanged();
        return buttonBox;
    }

    /**
     * Returns this field editor's list control.
     *
     * @param parent the parent control
     * @return the list control
     */
    public List getListControl(Composite parent) {
        if (list == null) {
            list = new List(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
            list.setFont(parent.getFont());
            list.addSelectionListener(getSelectionListener());
            list.addDisposeListener(new DisposeListener() {
                @Override
                public void widgetDisposed(DisposeEvent event) {
                    list = null;
                }
            });
        } else {
            checkParent(list, parent);
        }
        return list;
    }

    @Override
    public List getControl() {
        return list;
    }

    /**
     * Creates and returns a new item for the list.
     * <p>
     * Subclasses must implement this method.
     * </p>
     *
     * @return a new item
     */
    protected abstract String getNewInputObject();

    @Override
    public int getNumberOfControls() {
        return 3;
    }

    /**
     * Returns this field editor's selection listener. The listener is created if nessessary.
     *
     * @return the selection listener
     */
    private SelectionListener getSelectionListener() {
        if (selectionListener == null) {
            createSelectionListener();
        }
        return selectionListener;
    }

    /**
     * Returns this field editor's shell.
     * <p>
     * This method is internal to the framework; subclassers should not call this method.
     * </p>
     *
     * @return the shell
     */
    protected Shell getShell() {
        if (addButton == null) {
            return null;
        }
        return addButton.getShell();
    }

    /**
     * Splits the given string into a list of strings. This method is the converse of
     * <code>createList</code>.
     * <p>
     * Subclasses must implement this method.
     * </p>
     *
     * @param stringList the string
     * @return an array of <code>String</code>
     * @see #createList
     */
    protected abstract String[] parseString(String stringList);

    /**
     * Notifies that the Remove button has been pressed.
     */
    private void removePressed() {
        setPresentsDefaultValue(false);
        int index = list.getSelectionIndex();
        if (index >= 0) {
            list.remove(index);
            selectionChanged();
        }
    }

    /**
     * Invoked when the selection in the list has changed.
     *
     * <p>
     * The default implementation of this method utilizes the selection index and the size of the
     * list to toggle the enablement of the up, down and remove buttons.
     * </p>
     *
     * <p>
     * Sublcasses may override.
     * </p>
     *
     * @since 3.5
     */
    protected void selectionChanged() {

        int index = list.getSelectionIndex();
        int size = list.getItemCount();

        removeButton.setEnabled(index >= 0);
        upButton.setEnabled(size > 1 && index > 0);
        downButton.setEnabled(size > 1 && index >= 0 && index < size - 1);
    }

    @Override
    public void setFocus() {
        if (list != null) {
            list.setFocus();
        }
    }

    /**
     * Moves the currently selected item up or down.
     *
     * @param up <code>true</code> if the item should move up, and <code>false</code> if it should
     *        move down
     */
    private void swap(boolean up) {
        setPresentsDefaultValue(false);
        int index = list.getSelectionIndex();
        int target = up ? index - 1 : index + 1;

        if (index >= 0) {
            String[] selection = list.getSelection();
            Assert.isTrue(selection.length == 1);
            list.remove(index);
            list.add(selection[0], target);
            list.setSelection(target);
        }
        selectionChanged();
    }

    /**
     * Notifies that the Up button has been pressed.
     */
    private void upPressed() {
        swap(true);
    }

    /*
     * @see FieldEditor.setEnabled(boolean,Composite).
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (list != null && list.isDisposed()) {
            list.setEnabled(enabled);
            addButton.setEnabled(enabled);
            removeButton.setEnabled(enabled);
            upButton.setEnabled(enabled);
            downButton.setEnabled(enabled);
        }
    }

    @Override
    public void setVisible(boolean visble) {
        super.setVisible(visble);
        if (list != null && list.isDisposed()) {
            list.setVisible(visble);
            addButton.setVisible(visble);
            removeButton.setVisible(visble);
            upButton.setVisible(visble);
            downButton.setVisible(visble);
        }
    }

    /**
     * Return the Add button.
     *
     * @return the button
     * @since 3.5
     */
    protected Button getAddButton() {
        return addButton;
    }

    /**
     * Return the Remove button.
     *
     * @return the button
     * @since 3.5
     */
    protected Button getRemoveButton() {
        return removeButton;
    }

    /**
     * Return the Up button.
     *
     * @return the button
     * @since 3.5
     */
    protected Button getUpButton() {
        return upButton;
    }

    /**
     * Return the Down button.
     *
     * @return the button
     * @since 3.5
     */
    protected Button getDownButton() {
        return downButton;
    }

    /**
     * Return the List.
     *
     * @return the list
     * @since 3.5
     */
    protected List getList() {
        return list;
    }
}
