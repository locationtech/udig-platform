/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2010, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.feature.editor.field;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.locationtech.udig.project.ui.IFeaturePanel;
import org.locationtech.udig.project.ui.IFeatureSite;
import org.locationtech.udig.project.ui.feature.EditFeature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.Name;

/**
 * Abstract base class for all attribute fields.
 * <p>
 * An attribute field presents the value of an attribute to the end user; the value is loaded from a
 * feature; if modified by the end user the valie is validated and eventually stored back to the
 * feature. An attribute field reports an event when the value or validity of the value changes.
 * <p>
 * Attribute fields are often used with your own implementation of IFeaturePanel.
 *
 * @since 1.2.0
 */
public abstract class AttributeField {

    /** indicate validity changed */
    public static final String IS_VALID = "attribute_field_is_valid";//$NON-NLS-1$

    /** value changed */
    public static final String VALUE = "attribute_field_value";//$NON-NLS-1$

    /**
     * Edit feature policing our feature for us The edit feature is thread safe and will allow our
     * fields to update the value from the ui thread without throwing a fit (ie deadlock)
     */
    private EditFeature feature;

    /**
     * Name of the attribute being displayed Should be the same as name.getLocalPart()
     */
    private String attributeName;

    /** The full Name of the attribute being displayed in case we get in trouble with namespaces. */
    private Name name;

    /**
     * is default value is currently displayed (false by default)
     */
    private boolean isDefaultPresented = false;

    /**
     * The label's text.
     */
    protected String labelText;

    /**
     * The label control.
     */
    private Label label;

    /**
     * Listener, or <code>null</code> if none
     */
    private IPropertyChangeListener propertyChangeListener; // interesting there is only one?

    /**
     * The page containing this field editor
     * <p>
     * You can get access to the IFeatureSite from the page and generally have fun.
     */
    private IFeaturePanel page;

    /**
     * Creates a new attribute field.
     * <p>
     * Subclass should set the label text, and createControl prior to use.
     */
    protected AttributeField() {
    }

    /**
     * Creates a new attribute field.
     *
     * @param name the name of the attribute this attribute field works on
     * @param labelText the label text of the attribute field
     * @param parent the parent of the attribute field's control
     */
    protected AttributeField(String name, String labelText, Composite parent) {
        init(name, labelText);
        createControl(parent);
    }

    /**
     * Adjusts the horizontal span of this attribute field's basic controls.
     * <p>
     * Subclasses must implement this method to adjust the horizontal span of controls so they
     * appear correct in the given number of columns.
     * </p>
     * <p>
     * The number of columns will always be equal to or greater than the value returned by this
     * editor's <code>getNumberOfControls</code> method.
     * </p>
     *
     * @param numColumns the number of columns
     */
    public abstract void adjustForNumColumns(int numColumns); // TODO: revisit

    /**
     * Applies a font.
     * <p>
     * The default implementation of this framework method does nothing. Subclasses should override
     * this method if they want to change the font of the SWT control to a value different than the
     * standard dialog font.
     * </p>
     */
    protected void applyFont() {
    }

    /**
     * Checks if the given parent is the current parent of the supplied control; throws an
     * (unchecked) exception if they are not correctly related.
     *
     * @param control the control
     * @param parent the parent control
     */
    protected void checkParent(Control control, Composite parent) {
        Assert.isTrue(control.getParent() == parent, "Different parents");//$NON-NLS-1$
    }

    /**
     * Clears the error message from the message line.
     */
    protected void clearErrorMessage() {
        if (page == null || page.getSite() == null) {
            return;
        }
        IFeatureSite site = page.getSite();
        if (site.getActionBars() == null || site.getActionBars().getStatusLineManager() == null) {
            return;
        }
        site.getActionBars().getStatusLineManager().setErrorMessage(null);
    }

    /**
     * Clears the normal message from the message line.
     */
    protected void clearMessage() {
        if (page == null || page.getSite() == null) {
            return;
        }
        IFeatureSite site = page.getSite();
        if (site.getActionBars() == null || site.getActionBars().getStatusLineManager() == null) {
            return;
        }
        site.getActionBars().getStatusLineManager().setMessage(null);
    }

    /**
     * Creates this attribute field's main control containing all of its basic controls.
     *
     * @param parent the parent control
     */
    protected void createControl(Composite parent) {
        GridLayout layout = new GridLayout();
        layout.numColumns = getNumberOfControls();
        layout.marginWidth = 5;
        layout.marginHeight = 0;
        layout.makeColumnsEqualWidth = false;
        layout.horizontalSpacing = 8; // use miglayout "realed" later
        parent.setLayout(layout);
        doFillIntoGrid(parent, layout.numColumns);
    }

    /**
     * Disposes the SWT resources used by this attribute field.
     */
    public void dispose() {
        // nothing to dispose
    }

    /**
     * Fills this attribute field's basic controls into the given parent.
     * <p>
     * Subclasses must implement this method to create the controls for this attribute field.
     * </p>
     * <p>
     * Note this method may be called by the constructor, so it must not access fields on the
     * receiver object because they will not be fully initialized.
     * </p>
     *
     * @param parent the composite used as a parent for the basic controls; the parent's layout must
     *        be a <code>GridLayout</code>
     * @param numColumns the number of columns
     */
    protected abstract void doFillIntoGrid(Composite parent, int numColumns);

    /**
     * Initializes this attribute field with the attribute value from the feature.
     * <p>
     * Subclasses must implement this method to properly initialize the attribute field.
     * </p>
     * Usually this is done with feature.getAttribute(name)
     */
    public abstract void doLoad();

    /**
     * Initializes this attribute field with the default attribute value from the feature.
     * <p>
     * Subclasses must implement this method to properly initialize the attribute field.
     * </p>
     * Usually done with feature.getFeatureType().getDescriptor(Name).getDefaultValue()
     */
    protected abstract void doLoadDefault();

    /**
     * Stores the attribute value from this attribute field into the feature.
     * <p>
     * Subclasses must implement this method to save the entered value into the feature.
     * </p>
     * Usually done with feature.setAttribute(Name, value)
     */
    protected abstract void doStore();

    /**
     * Fills this attribute field's basic controls into the given parent.
     *
     * @param parent the composite used as a parent for the basic controls; the parent's layout must
     *        be a <code>GridLayout</code>
     * @param numColumns the number of columns
     */
    public void fillIntoGrid(Composite parent, int numColumns) {
        Assert.isTrue(numColumns >= getNumberOfControls());
        Assert.isTrue(parent.getLayout() instanceof GridLayout);
        doFillIntoGrid(parent, numColumns);
    }

    /**
     * Informs this attribute field's listener, if it has one, about a change to one of this
     * attribute field's boolean-valued properties. Does nothing if the old and new values are the
     * same.
     *
     * @param property the attribute field property name, such as <code>VALUE</code> or
     *        <code>IS_VALID</code>
     * @param oldValue the old value
     * @param newValue the new value
     */
    protected void fireStateChanged(String property, boolean oldValue, boolean newValue) {
        if (oldValue == newValue) {
            return;
        }
        Boolean isOld = oldValue ? Boolean.TRUE : Boolean.FALSE;
        Boolean isNew = newValue ? Boolean.TRUE : Boolean.FALSE;
        fireValueChanged(property, isOld, isNew);
    }

    /**
     * Informs this attribute field's listener, if it has one, about a change to one of this
     * attribute field's properties.
     *
     * @param property the attribute field property name, such as <code>VALUE</code> or
     *        <code>IS_VALID</code>
     * @param oldValue the old value object, or <code>null</code>
     * @param newValue the new value, or <code>null</code>
     */
    protected void fireValueChanged(String property, Object oldValue, Object newValue) {
        if (propertyChangeListener == null) {
            return;
        }
        PropertyChangeEvent event = new PropertyChangeEvent(this, property, oldValue, newValue);
        propertyChangeListener.propertyChange(event);
    }

    /**
     * Returns the symbolic font name used by this attribute field.
     *
     * @return the symbolic font name
     */
    public String getFieldEditorFontName() {
        return JFaceResources.DIALOG_FONT;
    }

    /**
     * Returns the label control.
     *
     * @return the label control, or <code>null</code> if no label control has been created
     */
    public Label getLabelControl() {
        return label;
    }

    /**
     * Control suitable for decorating
     *
     * @return
     */
    public abstract Control getControl();

    /**
     * Returns this attribute field's label component.
     * <p>
     * The label is created if it does not already exist
     * </p>
     *
     * @param parent the parent
     * @return the label control
     */
    public Label getLabelControl(Composite parent) {
        if (label == null) {
            label = new Label(parent, SWT.LEFT);
            label.setFont(parent.getFont());
            String text = getLabelText();
            if (text != null) {
                label.setText(text);
            }
            label.addDisposeListener(new DisposeListener() {
                @Override
                public void widgetDisposed(DisposeEvent event) {
                    label = null;
                }
            });
        } else {
            checkParent(label, parent);
        }
        return label;
    }

    /**
     * Returns this attribute field's label text.
     *
     * @return the label text
     */
    public String getLabelText() {
        return labelText;
    }

    /**
     * Returns the number of basic controls this attribute field consists of.
     *
     * @return the number of controls
     */
    public abstract int getNumberOfControls();

    /**
     * Returns the name of the attribute this attribute field operates on.
     *
     * @return the name of the attribute
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Return the IFeaturePanel that the receiver is sending updates to.
     *
     * @return IFeaturePanel or <code>null</code> if it has not been set.
     */
    protected IFeaturePanel getFeaturePanel() {
        return page;
    }

    /**
     * Returns the feature used by this attribute field.
     *
     * @return the feature, or <code>null</code> if none
     * @see #setattributeStore
     */
    public EditFeature getFeature() {
        return feature;
    }

    /**
     * Initialize the attribute field with the given attribute name and label.
     *
     * @param name the name of the attribute this attribute field works on
     * @param text the label text of the attribute field
     */
    protected void init(String name, String text) {
        Assert.isNotNull(name);
        Assert.isNotNull(text);
        attributeName = name;
        this.labelText = text;
    }

    /**
     * Returns whether this attribute field contains a valid value.
     * <p>
     * The default implementation of this framework method returns <code>true</code>. Subclasses
     * wishing to perform validation should override both this method and
     * <code>refreshValidState</code>.
     * </p>
     *
     * @return <code>true</code> if the field value is valid, and <code>false</code> if invalid
     * @see #refreshValidState()
     */
    public boolean isValid() {
        return true;
    }

    /**
     * Initializes this attribute field with the attribute value from the feature.
     */
    public void load() {
        if (feature != null) {
            isDefaultPresented = false;
            doLoad();
            refreshValidState();
        }
    }

    /**
     * Initializes this attribute field with the default attribute value from the feature.
     */
    public void loadDefault() {
        if (feature != null) {
            isDefaultPresented = true;
            doLoadDefault();
            refreshValidState();
        }
    }

    /**
     * Returns whether this attribute field currently presents the default value for its attribute.
     *
     * @return <code>true</code> if the default value is presented, and <code>false</code> otherwise
     */
    public boolean presentsDefaultValue() {
        return isDefaultPresented;
    }

    /**
     * Refreshes this attribute field's valid state after a value change and fires an
     * <code>IS_VALID</code> property change event if warranted.
     * <p>
     * The default implementation of this framework method does nothing. Subclasses wishing to
     * perform validation should override both this method and <code>isValid</code>.
     * </p>
     *
     * @see #isValid
     */
    protected void refreshValidState() {
    }

    /**
     * Refresh this attribute field's visible state after a value change.
     * <p>
     * Default implementation should call setVisible if needed.
     */
    protected void refreshVisibleState() {

    }

    /**
     * Sets the focus to this attribute field.
     * <p>
     * The default implementation of this framework method does nothing. Subclasses may reimplement.
     * </p>
     */
    public void setFocus() {
        // do nothing;
    }

    /**
     * Sets this attribute field's label text. The label is typically presented to the left of the
     * entry field.
     *
     * @param text the label text
     */
    public void setLabelText(String text) {
        Assert.isNotNull(text);
        labelText = text;
        if (label != null) {
            label.setText(text);
        }
    }

    /**
     * Sets the name of the attribute this attribute field operates on.
     * <p>
     * The ability to change this allows the same attribute field object to be reused for different
     * attributes.
     * </p>
     * <p>
     * For example:
     * <pre>
     *  ...
     *  field.setAttributeName("font");
     *  field.load();
     * </pre>
     *
     * </p>
     *
     * @param name the name of the attribute
     */
    public void setAttributeName(String name) {
        attributeName = name;
    }

    /**
     * Set the page to be the receiver.
     *
     * @param dialogPage
     * @since 3.1
     */
    public void setPage(IFeaturePanel featurePanel) {
        page = featurePanel;

    }

    /**
     * Sets the feature used by this attribute field.
     *
     * @param store the feature, or <code>null</code> if none
     * @see #getfeature
     */
    public void setFeature(EditFeature feature) {
        this.feature = feature;
    }

    /**
     * Sets whether this attribute field is presenting the default value.
     *
     * @param booleanValue <code>true</code> if the default value is being presented, and
     *        <code>false</code> otherwise
     */
    protected void setPresentsDefaultValue(boolean booleanValue) {
        isDefaultPresented = booleanValue;
    }

    /**
     * Sets or removes the property change listener for this attribute field.
     * <p>
     * Note that attribute fields can support only a single listener.
     * </p>
     *
     * @param listener a property change listener, or <code>null</code> to remove
     */
    public void setPropertyChangeListener(IPropertyChangeListener listener) {
        propertyChangeListener = listener;
    }

    /**
     * Shows the given error message in the page for this attribute field if it has one.
     *
     * @param msg the error message
     */
    protected void showErrorMessage(String msg) {
        if (page == null || page.getSite() == null) {
            return;
        }
        IFeatureSite site = page.getSite();
        if (site.getActionBars() == null || site.getActionBars().getStatusLineManager() == null) {
            return;
        }
        site.getActionBars().getStatusLineManager().setErrorMessage(msg);
    }

    /**
     * Shows the given message in the page for this attribute field if it has one.
     *
     * @param msg the message
     */
    protected void showMessage(String msg) {
        if (page == null && page.getSite() == null) {
            return;
        }
        IFeatureSite site = page.getSite();
        if (site.getActionBars() == null || site.getActionBars().getStatusLineManager() == null) {
            return;
        }
        site.getActionBars().getStatusLineManager().setMessage(msg);
    }

    /**
     * Stores this attribute field's value back into the feature.
     */
    public void store() {
        if (feature == null) {
            return;
        }

        if (isDefaultPresented) {
            SimpleFeatureBuilder builder = new SimpleFeatureBuilder(feature.getFeatureType());
            SimpleFeature temp = builder.buildFeature(feature.getID());

            feature.setAttributes(temp.getAttributes());
        } else {
            doStore();
        }
    }

    /** Default implementation checks the getLabelControl */
    public boolean isVisible() {
        Label check = getLabelControl();
        return check != null && !check.isDisposed() && check.isVisible();
    }

    /**
     * Hide attribute field.
     * <p>
     * The default implementation will call setVisible on both getLabelControl and getControl;
     * override if you have several controls to take care of.
     * </p>
     *
     * @param visible true to show the attribute field
     */
    public void setVisible(boolean visible) {
        if (getLabelControl() != null && !getLabelControl().isDisposed()) {
            getLabelControl().setVisible(visible);
        }
        getLabelControl().setVisible(visible);
        if (getControl() != null && !getControl().isDisposed()) {
            getControl().setVisible(visible);
        }
    }

    /**
     * Default implementation checks the getLabelControl
     */
    public boolean isEnabled() {
        Label check = getLabelControl();
        return check != null && !check.isDisposed() && check.isEnabled();
    }

    /**
     * Set whether or not the controls in the attribute field are enabled.
     *
     * @param enabled The enabled state.
     * @param parent The parent of the controls in the group. Used to create the controls if
     *        required.
     */
    public void setEnabled(boolean enabled) {
        if (getLabelControl() != null && !getLabelControl().isDisposed()) {
            getLabelControl().setEnabled(enabled);
        }
        if (getControl() != null && !getControl().isDisposed()) {
            getControl().setEnabled(enabled);
        }
    }

}
