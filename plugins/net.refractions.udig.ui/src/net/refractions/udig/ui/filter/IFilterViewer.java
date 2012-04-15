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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

/**
 * Used to create and edit Filters. Used to package up several controls following the {@link Viewer}
 * convention of a constructor to create the controls, an input providing context to help editing, and a
 * selection to set and retrieve the value being worked on.
 * <p>
 * Subclasses are expected to support:
 * <ul>
 * <li>{@link #setInput(Object)} supporting FilterInput or FeatureType</li>
 * <li>{@link #setSelection(ISelection)} and {@link #getSelection()} to retrieve the {@link Filter} being edited</li>
 * </ul>
 * @see FilterInput Used to provide context (such as feature type to suggest attribute names)
 * @author Scott
 * @since 1.3.0
 */
public abstract class IFilterViewer extends Viewer {
    
    /**
     * Input provided to help when creating a Filter.
     */
    FilterInput input;
    
    /**
     * Filter being edited.
     */
    Filter filter;
    
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
    public void setInput(Object filterInput){
        if( input == filterInput){
            return; // no change
        }
        if( input == null ){
            // stop any listeners!
        }
        if( filterInput instanceof SimpleFeatureType){
            input = new FilterInput( (SimpleFeatureType) filterInput );
        }
        else if (filterInput instanceof FilterInput ){
            input = (FilterInput) filterInput;
        }
        if( input != null ){
            // connect any listeners
        }
    }

    /**
     * Provides access to the Filter being used by this filter.
     * <p>
     * 
     * @return Filter being filter; may be Filter.EXCLUDE if empty (but will not be null)
     */
    public Filter getFilter(){
        return filter;
    }

    /**
     * Returns the current selection for this provider.
     * 
     * @return Current filter from {@link #getFilter()}
     */
    public ISelection getSelection(){
        if( filter != null ){
            return new StructuredSelection( filter );
        }
        return new StructuredSelection();
    }
    


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

}
