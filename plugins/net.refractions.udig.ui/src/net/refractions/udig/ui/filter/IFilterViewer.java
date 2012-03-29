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
 * @author leviputna (QPWS)
 * @since 1.3.0
 */
public abstract class IFilterViewer extends Viewer {

    protected boolean required = true;

    /**
     * 
     */
    public IFilterViewer( Composite parent) {
        this(parent, SWT.SINGLE);
    }
    
    /**
     * 
     */
    public IFilterViewer( Composite parent, int style ) {
        
    }

    /**
     * Set the input for this Filter.
     * 
     * @param input Expression, String or other data object to use as the input for this filter
     */
    public abstract void setInput( Object input );

    /**
     * Provides access to the Expression being used by this filter.
     * <p>
     * 
     * @return Expression being filter; may be Expression.NIL if empty (but will not be null)
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
     * Note that the decision on whather FilterEditor canProcess() should be informed by the presence of
     * attributes it design to work with.
     * </p>
     * 
     * @param input the imput that this filterEditor will take
     * @return
     */
    public abstract Boolean canProcess( Object input );
    
    public abstract Control getControl();
    
    public abstract void setRequired(boolean required);
    
    public abstract void setSelection(ISelection selection, boolean reveal);
    public abstract void feedback();
    public abstract void feedback(String warning);
    public abstract void feedback(String exception, Exception eek);

    // context!
    public abstract void setSchema(SimpleFeatureType schema); // pass in context to list attributes name
    public abstract SimpleFeatureType getSchema(); 
    public abstract void setExpected( Class<?> binding );
    public abstract Class<?> getExpected(); // Expected class - example Colour

}
