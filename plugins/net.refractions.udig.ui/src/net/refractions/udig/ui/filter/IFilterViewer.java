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

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.util.Utilities;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

/**
 * Used to create and edit Filters. Used to package up several controls following the {@link Viewer}
 * convention of a constructor to create the controls, an input providing context to help editing, and a
 * selection to set and retrieve the value being worked on.
 * <p>
 * Implementations are asked to respect the following style constants
 * <ul>
 * <li>{@link SWT#SINGLE}: Consider to be a single line</li>
 * <li>{@link SWT#MULTI}: Viewer can take additional height</li>
 * <li>{@link SWT#READ_ONLY}</li>
 * </ul>
 
 * @see FilterInput Used to provide context (such as feature type to suggest attribute names)
 * @author Scott
 * @author Jody Garnett
 * @since 1.3.2
 */
public abstract class IFilterViewer extends Viewer {
    
    /**
     * Input provided to help when creating a Filter.
     */
    protected FilterInput input;
    
    /**
     * Filter being edited.
     */
    protected Filter filter;

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

    @Override
    public FilterInput getInput() {
        return input;
    }
    /**
     * Refreshes this viewer completely with information freshly obtained from {@link #input} and {@link #filter}.
     */
    public abstract void refresh();
    
    /**
     * Direct access to the Filter being defined.
     * <p>
     * 
     * @return Filter being defined
     */
    public Filter getFilter(){
        return filter;
    }

    /**
     * Returns the current selection for this provider.
     * 
     * @return Current filter from {@link #getFilter()} or {@link StructuredSelection#EMPTY} if not defined
     */
    public ISelection getSelection(){
        if( filter != null ){
            return new StructuredSelection( filter );
        }
        return StructuredSelection.EMPTY;
    }
    /**
     * Sets a new filter for this viewer; called from {@link #setSelection(ISelection)}
     * to update viewer to show filter contents.
     * <p>
     * Subclasses must implement this method.
     * </p>
     * @param filter
     */
    public abstract void setFilter( Filter filter );
    
    /**
     * Used internally to update the filter and issue a {@link SelectionChangedEvent}.
     * 
     * @param newFilter
     */
    protected void internalUpdate(Filter newFilter) {
        if( this.filter == newFilter ){
            return;
        }
        String before = filter != null ? ECQL.toCQL(filter) : "(empty)";
        String after = newFilter != null ? ECQL.toCQL(newFilter) : "(empty)";
        if (!Utilities.equals(before, after)){
            this.filter = newFilter;
            StructuredSelection selection = newFilter != null ? new StructuredSelection( newFilter) : StructuredSelection.EMPTY;
            fireSelectionChanged( new SelectionChangedEvent( this, selection ) );
        }
    }
    /**
     * Extracts a filter from the selection making use of {@link #setFilter(Filter)} to update the viewer.
     * 
     * @param selection Selection defining Filter
     * @param reveal <code>true</code> if the selection is to be made visible, and
     *        <code>false</code> otherwise
     */
    public void setSelection(ISelection selection, boolean reveal){
        if( selection != null && selection instanceof StructuredSelection){
            StructuredSelection sel = (StructuredSelection) selection;
            Object element = sel.getFirstElement();
            if( element instanceof Filter ){
                setFilter( (Filter) element);
                return;
            }
        }
        setFilter(Filter.EXCLUDE);
    }
    
    //
    // Helper methods to assist implementors
    //
    /**
     * Provide the feedback that everything is fine.
     * <p>
     * This method will make use of an associated ControlDecoration if available.
     * </p>
     */
    protected void feedback(){
        if( input != null && input.getFeedback() != null ){
            input.getFeedback().hide();
        }
    }

    /**
     * Provide warning feedback.
     * <p>
     * This method will make use of an associated ControlDecoration if available.
     * </p>
     */
    protected void feedback(String warning){
        if( input != null && input.getFeedback() != null ){
            ControlDecoration feedback = input.getFeedback();
            
            feedback.setDescriptionText(warning);
            
            FieldDecorationRegistry decorations = FieldDecorationRegistry.getDefault();
            FieldDecoration errorDecoration = decorations.getFieldDecoration(FieldDecorationRegistry.DEC_WARNING);
            feedback.setImage(errorDecoration.getImage());
            feedback.show();
        }
        Control control = getControl();
        if (control != null && !control.isDisposed()) {
            control.setToolTipText(warning);
        }
    }
    /**
     * Provide required feedback.
     * <p>
     * This method will make use of an associated ControlDecoration if available.
     * </p>
     */
    protected void feedback(String warning, boolean isRequired){
        if( isRequired ){
            if( input != null && input.getFeedback() != null ){
                ControlDecoration feedback = input.getFeedback();
                
                feedback.setDescriptionText(warning);
                
                FieldDecorationRegistry decorations = FieldDecorationRegistry.getDefault();
                if( isRequired ){
                    FieldDecoration requiredDecoration = decorations.getFieldDecoration(FieldDecorationRegistry.DEC_REQUIRED);
                    feedback.setImage(requiredDecoration.getImage());
                } else {
                    FieldDecoration warningDecoration = decorations.getFieldDecoration(FieldDecorationRegistry.DEC_WARNING );
                    feedback.setImage(warningDecoration.getImage());
                }
                feedback.show();
            }
            Control control = getControl();
            if (control != null && !control.isDisposed()) {
                control.setToolTipText(warning);
            }
        }
        else {
            feedback( warning );
        }
    }
    /**
     * Provide error feedback.
     * <p>
     * This method will make use of an associated ControlDecoration if available.
     * </p>
     */
    protected void feedback(String error, Throwable exception){
        if( input != null && input.getFeedback() != null ){
            ControlDecoration feedback = input.getFeedback();
            
            feedback.setDescriptionText(error);
            FieldDecorationRegistry decorations = FieldDecorationRegistry.getDefault();
            FieldDecoration warningDecoration = decorations.getFieldDecoration(FieldDecorationRegistry.DEC_WARNING);
            feedback.setImage(warningDecoration.getImage());
            feedback.show();
        }
        Control control = getControl();
        if (control != null && !control.isDisposed()) {
            control.setToolTipText(error+":"+exception);
        }
    }

//  /**
//   * Checks that the filter is valid using {@link IFilterViewer#isValid()} and update the UI to
//   * desplay any error messages using {@link IFilterViewer#getValidationMessage()} if not valid.
//   * 
//   * @return true if the filter is valid
//   */
//  public abstract boolean validate();
//
//  /**
//   * Used to check for any validation messages (such as required field etc...)
//   * 
//   * @return Validation message
//   */
//  public abstract String getValidationMessage();
//
//
//  /**
//   * Check to see if we can filter the given input
//   * <p>
//   * Note that the decision on whather FilterEditor canProcess() should be informed by the
//   * presence of attributes it design to work with.
//   * </p>
//   * 
//   * @param input the imput that this filterEditor will take
//   * @return
//   */
//  public abstract Boolean canProcess(Object input);
}
