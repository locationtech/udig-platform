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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

/**
 * Abstract class for creating a UI that allows the user to create and edit Filters
 * 
 * @author Scott
 * @since 1.3.0
 */
public abstract class IFilterViewer extends Viewer {

    protected boolean required = true;

    /**
     * Default constructor. Calls <code>IFilterViewer( Composite parent, SWT.SINGLE )</code>
     */
    public IFilterViewer(Composite parent) {
        this(parent, SWT.SINGLE);
    }

    /**
     * Constructor
     */
    public IFilterViewer(Composite parent, int style) {

    }

    /**
     * Set the input for this Filter.
     * 
     * @param input Filter, String or other data object to use as the input for this filter
     */
    public abstract void setInput(Object input);

    /**
     * Provides access to the Filter being used by this filter.
     * <p>
     * 
     * @return Filter being filter; may be Filter.EXCLUDE if empty (but will not be null)
     */
    public abstract Filter getInput();

    /**
     * Returns the current selection for this provider.
     * 
     * @return chrrent selection
     */
    public abstract ISelection getSelection();

    /**
     * Refreshes this viewer completely with information freshly obtained from this viewer's model.
     */
    public abstract void refresh();

    /**
     * Checks that the filter is valid using {@link IFilterViewer#isValid()} and update the UI to
     * desplay any error messages using {@link IFilterViewer#getValidationMessage()} if not valid.
     * 
     * @return true if the filter is valid
     */
    public abstract boolean validate();

    /**
     * Used to check for any validation messages (such as required field etc...)
     * 
     * @return Validation message
     */
    public abstract String getValidationMessage();

    /**
     * @return true if this is a required field
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Check to see if we can filter the given input
     * <p>
     * Note that the decision on whather FilterEditor canProcess() should be informed by the
     * presence of attributes it design to work with.
     * </p>
     * 
     * @param input the imput that this filterEditor will take
     * @return
     */
    public abstract Boolean canProcess(Object input);

    /**
     * Returns the controller of the viewer. Used for setting size etc
     */
    public abstract Control getControl();

    /**
     * The isRequired flag will be used to determine the default decoration to show (if there is no
     * warning or error to take precedence).
     * <p>
     * Please note that if this is a required field Filter.EXLCUDE is not considered to be a valid
     * state.
     * </p>
     * 
     * @param isRequired true if this is a required field
     */
    public abstract void setRequired(boolean required);

    /**
     * Sets a new selection for this viewer and optionally makes it visible.
     * <p>
     * Subclasses must implement this method.
     * </p>
     * 
     * @param selection the new selection
     * @param reveal <code>true</code> if the selection is to be made visible, and
     *        <code>false</code> otherwise
     */
    public abstract void setSelection(ISelection selection, boolean reveal);

    /**
     * Provide the feedback that everything is fine.
     * <p>
     * This method will make use of an associated ControlDecoration if available; if not it will
     * make use of a tooltip or something.
     * </p>
     */
    public abstract void feedback();

    /**
     * Provide the feedback that everything is fine.
     * <p>
     * This method will make use of an associated ControlDecoration if available; if not it will
     * make use of a tooltip or something.
     * </p>
     */
    public abstract void feedback(String warning);

    /**
     * Provide the feedback that everything is fine.
     * <p>
     * This method will make use of an associated ControlDecoration if available; if not it will
     * make use of a tooltip or something.
     * </p>
     */
    public abstract void feedback(String exception, Exception eek);

    /**
     * Feature Type to use for attribute names.
     * 
     * @param type
     */
    public abstract void setSchema(SimpleFeatureType schema);

    /**
     * Feature Type used by the FilterViewer
     * 
     * @param type
     */
    public abstract SimpleFeatureType getSchema();

    public abstract void setExpected(Class<?> binding);

    public abstract Class<?> getExpected();

}
