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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.IMessagePrefixProvider;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.locationtech.udig.feature.panel.FeaturePanelWidgetFactory;
import org.locationtech.udig.project.ui.IFeaturePanel;

/**
 * Abstract FetaurePanel with some helper methods for the care and feeding of AttributeFields.
 * <p>
 * We may be able to "retire" our AttributeFields and make use of PropertyEditors directly. If so
 * the add methods defined here should still work.
 * </p>
 * This implementation maintains a list of AttributeField and provides an implementation of refresh
 * that will.
 *
 * @author Jody
 * @since 1.2.0
 */
public abstract class FeaturePanel extends IFeaturePanel implements IPropertyChangeListener {

    protected List<AttributeField> fields = new ArrayList<>();

    protected ScrolledForm scrolled;

    private AttributeField invalidField;

    private boolean isValid;

    private FeaturePanelWidgetFactory widgetFactory;

    private IMessageManager messages;

    private ManagedForm managedForm;

    private Form form;

    /**
     * Subclasses should call adjustGridLayout after they have populated parent with their fields.
     */
    @Override
    public void createPartControl(Composite parent) {
        FeaturePanelWidgetFactory factory = getWidgetFactory();
        this.scrolled = factory.createScrolledForm(parent);
        this.form = scrolled.getForm();
        form.setText(getTitle());
        factory.decorateFormHeading(form);

        managedForm = new ManagedForm(factory, scrolled);
        messages = managedForm.getMessageManager();
        messages.setMessagePrefixProvider(new IMessagePrefixProvider() {
            @Override
            public String getPrefix(Control control) {
                if (control instanceof Label) {
                    return ((Label) control).getText();
                }
                return null;
            }
        });

        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.marginWidth = 5;
        layout.marginHeight = 0;
        layout.horizontalSpacing = 8; // use miglayout "realed" later
        this.scrolled.setLayout(layout);
        this.scrolled.setFont(parent.getFont());

        createFieldEditors();

        adjustGridLayout(scrolled);
    }

    protected abstract void createFieldEditors();

    public Composite getParent() {
        return scrolled.getBody();
    }

    public FeaturePanelWidgetFactory getWidgetFactory() {
        synchronized (this) {
            if (widgetFactory == null) {
                widgetFactory = new FeaturePanelWidgetFactory();
            }
            return widgetFactory;
        }
    }

    protected void checkState() {
        boolean valid = true;
        invalidField = null;
        // The state can only be set to true if all
        // field editors contain a valid value. So we must check them all
        for (AttributeField field : fields) {
            valid = valid && field.isValid();
            if (!valid) {
                invalidField = field;
                break;
            }
        }
        setValid(valid);
    }

    public void setValid(boolean b) {
        boolean oldValue = isValid;
        isValid = b;
        if (oldValue != isValid) {
            // update feature site
            if (getSite() != null) {
                // getSite().updateStatus();
            }
            // update page state
            // updateApplyButton();
        }
    }

    /**
     * Calculates the number of columns needed to host all field editors.
     *
     * @return the number of columns
     */
    private int calcNumberOfColumns() {
        int result = 0;
        if (fields != null) {
            for (AttributeField field : fields) {
                result = Math.max(result, field.getNumberOfControls());
            }
        }
        return result;
    }

    /**
     * Adjust the layout of the field editors so that they are properly aligned.
     */
    protected void adjustGridLayout(Composite parent) {
        int numColumns = calcNumberOfColumns();
        if (parent.getLayout() instanceof GridLayout) {
            ((GridLayout) parent.getLayout()).numColumns = numColumns;
        } else {
            GridLayout layout = new GridLayout();
            layout.numColumns = numColumns;
            layout.marginHeight = 0;
            layout.marginWidth = 8;
            parent.setLayout(layout);
        }

        if (fields != null) {
            for (AttributeField field : fields) {
                field.adjustForNumColumns(numColumns);
            }
        }
    }

    /**
     * Remember the provided field; so it can be used with refresh/dispose/etc.
     * <p>
     * Subclasses may wish to override (and call super!) in order to process fields added to the
     * panel in one spot. As example this could be used to check security and set some of the fields
     * to read-only.
     *
     * @param <F>
     * @param field
     * @return
     */
    protected <F extends AttributeField> F addField(F field) {
        if (field == null) {
            throw new NullPointerException("AttributeField exepcted");
        }
        fields.add(field);
        return field;
    }

    @Override
    public void aboutToBeShown() {
        messages.setAutoUpdate(true);
        for (AttributeField field : fields) {
            field.setPropertyChangeListener(this);
            field.setFeature(getSite().getEditFeature());
            field.doLoad();
        }
    }

    @Override
    public void refresh() {
        for (AttributeField field : fields) {
            field.setFeature(getSite().getEditFeature());
            field.doLoad();
        }
    }

    @Override
    public void aboutToBeHidden() {
        messages.setAutoUpdate(false);
        for (AttributeField field : fields) {
            field.setPropertyChangeListener(null);
            // field.setFeature(null);
        }
    }

    @Override
    public void dispose() {
        for (AttributeField field : fields) {
            field.dispose();
            field.setPropertyChangeListener(null);
            field.setFeature(null);
        }
        super.dispose();
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        AttributeField field = (AttributeField) event.getSource();
        if (event.getProperty().equals(AttributeField.IS_VALID)) {
            boolean isValid = ((Boolean) event.getNewValue()).booleanValue();
            // If the new value is true then we must check all field editors.
            // If it is false, then the page is invalid in any case.
            if (isValid) {
                messages.removeMessage(field.getAttributeName(), field.getControl());
                field.getControl().setToolTipText(null);

                checkState();
            } else {
                String message = field.getAttributeName() + " invalid!";

                messages.addMessage(field.getAttributeName(), message, null, IMessageProvider.ERROR,
                        field.getControl());
                field.getControl().setToolTipText(message);

                invalidField = field;
                setValid(isValid);
            }
        }
        if (event.getProperty().equals(AttributeField.VALUE)) {
            if (field.isValid()) {
                field.store();
            }
        }
    }

}
