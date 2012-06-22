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
import org.eclipse.swt.widgets.Control;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.util.Utilities;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.expression.Expression;

/**
 * Viewer used to create and edit {@link Expression}.
 * <p>
 * Used to package up several controls following the {@link Viewer}
 * convention of a constructor to create the controls, an input providing context to help editing, and a
 * selection to set and retrieve the value being worked on.
 * <p>
 * Implementations are asked to respect the following style constants
 * <ul>
 * <li>{@link SWT#SINGLE}: Consider to be a single line</li>
 * <li>{@link SWT#MULTI}: Viewer can take additional height</li>
 * <li>{@link SWT#READ_ONLY}</li>
 * </ul>
 
 * @see ExpressionInput Used to provide context (such as feature type to suggest attribute names)
 * @author Jody Garnett
 * @since 1.3.2
 */
public abstract class IExpressionViewer extends Viewer {
    
    /**
     * Input provided to help when creating a Expression.
     */
    protected ExpressionInput input;
    
    /**
     * Expression being edited.
     */
    protected Expression expression;

    /**
     * Set the input for this Expression.
     * 
     * @param input Expression, String or other data object to use as the input for this expression
     */
    public void setInput(Object expressionInput){
        if( input == expressionInput){
            return; // no change
        }
        if( input == null ){
            // stop any listeners!
        }
        else if( expressionInput instanceof SimpleFeatureType){
            setInput( new ExpressionInput( (SimpleFeatureType) expressionInput ) );
        }
        else if (expressionInput instanceof ExpressionInput ){
            setInput(  (ExpressionInput) expressionInput );
        }
        if( input != null ){
            // connect any listeners
        }
    }
    public void setInput( ExpressionInput input ){
        this.input = input;
    }
    @Override
    public ExpressionInput getInput() {
        return input;
    }
    /**
     * Refreshes this viewer completely with information freshly obtained from {@link #input} and {@link #expression}.
     */
    public abstract void refresh();
    
    /**
     * Direct access to the Expression being defined.
     * <p>
     * 
     * @return Expression being defined
     */
    public Expression getExpression(){
        return expression;
    }

    /**
     * Returns the current selection for this provider.
     * 
     * @return Current expression from {@link #getExpression()} or {@link StructuredSelection#EMPTY} if not defined
     */
    public ISelection getSelection(){
        if( expression != null ){
            return new StructuredSelection( expression );
        }
        return StructuredSelection.EMPTY;
    }
    /**
     * Sets a new expression for this viewer; called from {@link #setSelection(ISelection)}
     * to update viewer to show expression contents.
     * <p>
     * Subclasses must implement this method.
     * </p>
     * @param expression
     */
    public abstract void setExpression( Expression expression );
    
    /**
     * Used internally to update the expression and issue a {@link SelectionChangedEvent}.
     * 
     * @param newExpression Expression used to update {@link #getExpression()} and the user interface components
     */
    protected void internalUpdate(Expression newExpression) {
        if( this.expression == newExpression ){
            return;
        }
        String before = expression != null ? ECQL.toCQL(expression) : "(empty)";
        String after = newExpression != null ? ECQL.toCQL(newExpression) : "(empty)";
        if (!Utilities.equals(before, after)){
            this.expression = newExpression;
            feedback(); // clear any outstanding feedback as our value matches our display now
            
            StructuredSelection selection = newExpression != null ? new StructuredSelection( newExpression) : StructuredSelection.EMPTY;
            fireSelectionChanged( new SelectionChangedEvent( this, selection ) );
        }
    }
    /**
     * Extracts a expression from the selection making use of {@link #setExpression(Expression)} to update the viewer.
     * 
     * @param selection Selection defining Expression
     * @param reveal <code>true</code> if the selection is to be made visible, and
     *        <code>false</code> otherwise
     */
    public void setSelection(ISelection selection, boolean reveal){
        if( selection != null && selection instanceof StructuredSelection){
            StructuredSelection sel = (StructuredSelection) selection;
            Object element = sel.getFirstElement();
            if( element instanceof Expression ){
                setExpression( (Expression) element);
                return;
            }
        }
        setExpression(Expression.NIL);
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

    protected void feedbackReplace( Expression expression ){
        feedback("Unable to display dynamic expression: \n" + ECQL.toCQL(expression)+ "\nEdit to replace expression.");
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
//   * Checks that the expression is valid using {@link IExpressionViewer#isValid()} and update the UI to
//   * desplay any error messages using {@link IExpressionViewer#getValidationMessage()} if not valid.
//   * 
//   * @return true if the expression is valid
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
//   * Check to see if we can expression the given input
//   * <p>
//   * Note that the decision on whather ExpressionEditor canProcess() should be informed by the
//   * presence of attributes it design to work with.
//   * </p>
//   * 
//   * @param input the imput that this expressionEditor will take
//   * @return
//   */
//  public abstract Boolean canProcess(Object input);
}
