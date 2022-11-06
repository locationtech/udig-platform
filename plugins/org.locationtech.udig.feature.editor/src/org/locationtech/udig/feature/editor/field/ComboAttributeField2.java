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

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.geotools.util.Converters;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

/**
 * NOT FINISHED
 */
public class ComboAttributeField2 extends AttributeField {

    /**
     * The <code>Combo</code> widget. Works only with strings at the moment which might be
     * problematic if we need it to work with objects
     */
    private ComboViewer viewer;

    private List<?> options;

    private ISelectionChangedListener listener = new ISelectionChangedListener() {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            IStructuredSelection sel = (IStructuredSelection) event.getSelection();
            fireValueChanged(VALUE, null, sel.getFirstElement());
        }
    };

    /**
     * Create the combo box attribute field.
     *
     * @param name the name of the preference this attribute field works on
     * @param labelText the label text of the attribute field
     * @param values Underlying values to populate the combo viewer widget.
     * @param parent the parent composite
     */
    public ComboAttributeField2(String name, String labelText, List<?> values, Composite parent) {
        init(name, labelText);
        options = values;
        createControl(parent);
    }

    @Override
    public Control getControl() {
        if (viewer != null) {
            return viewer.getControl();
        }
        return null;
    }

    @Override
    public void adjustForNumColumns(int numColumns) {
        if (numColumns > 1) {
            Control control = getLabelControl();
            int left = numColumns;
            if (control != null) {
                ((GridData) control.getLayoutData()).horizontalSpan = 1;
                left = left - 1;
            }
            ((GridData) viewer.getCombo().getLayoutData()).horizontalSpan = left;
        } else {
            Control control = getLabelControl();
            if (control != null) {
                ((GridData) control.getLayoutData()).horizontalSpan = 1;
            }
            ((GridData) viewer.getCombo().getLayoutData()).horizontalSpan = 1;
        }
    }

    @Override
    protected void doFillIntoGrid(Composite parent, int numColumns) {
        int comboC = 1;
        if (numColumns > 1) {
            comboC = numColumns - 1;
        }
        Control control = getLabelControl(parent);
        GridData gd = new GridData();
        gd.horizontalSpan = 1;
        control.setLayoutData(gd);

        control = getComboBoxControl(parent);
        gd = new GridData();
        gd.horizontalSpan = comboC;
        gd.horizontalAlignment = GridData.FILL;
        control.setLayoutData(gd);
        control.setFont(parent.getFont());
    }

    @Override
    public void doLoad() {
        Object value = getFeature().getAttribute(getAttributeName());
        if (viewer.getInput() == null) {
            viewer.setInput(Collections.singletonList(value));
        }
        ISelection selection;
        if (value == null) {
            selection = StructuredSelection.EMPTY;
        } else {
            selection = new StructuredSelection(value);
        }
        viewer.removeSelectionChangedListener(listener);
        viewer.setSelection(selection, true);
        viewer.addSelectionChangedListener(listener);
    }

    @Override
    protected void doLoadDefault() {
        SimpleFeatureType schema = getFeature().getFeatureType();
        AttributeDescriptor descriptor = schema.getDescriptor(getAttributeName());
        Object value = descriptor.getDefaultValue();
        ISelection selection = new StructuredSelection(value);
        viewer.removeSelectionChangedListener(listener);
        viewer.setSelection(selection, true);
        viewer.addSelectionChangedListener(listener);
    }

    @Override
    protected void doStore() {
        ISelection selection = viewer.getSelection();
        if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
            IStructuredSelection sel = (IStructuredSelection) selection;
            Object value = sel.getFirstElement();
            getFeature().setAttribute(getAttributeName(), value);
        } else {
            SimpleFeatureType schema = getFeature().getFeatureType();
            AttributeDescriptor descriptor = schema.getDescriptor(getAttributeName());

            String text = viewer.getCombo().getText();
            Object value = Converters.convert(text, descriptor.getType().getBinding());
            getFeature().setAttribute(getAttributeName(), value);
        }

    }

    @Override
    public int getNumberOfControls() {
        return 2;
    }

    /**
     * Lazily create and return the Combo control.
     */
    private Combo getComboBoxControl(Composite parent) {
        if (viewer == null) {
            viewer = new ComboViewer(parent, SWT.READ_ONLY);
            viewer.getCombo().setFont(parent.getFont());
            viewer.setContentProvider(ArrayContentProvider.getInstance());
            viewer.setLabelProvider(new LabelProvider());
            viewer.setInput(options);
            viewer.addSelectionChangedListener(listener);
        }
        return viewer.getCombo();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (viewer.getCombo() != null && !viewer.getCombo().isDisposed()) {
            viewer.getCombo().setEnabled(enabled);
        }
    }

    public ComboViewer getViewer() {
        return viewer;
    }
}
