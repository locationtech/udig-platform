package net.refractions.udig.ui.filter;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.geotools.data.Parameter;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.expression.Expression;

/**
 * Abstract class for creating a UI that allows the user to create and edit Expressions
 * 
 * @author Scott
 * @since 1.3.0
 */
public abstract class IExpressionViewer extends Viewer {

    /** Maximum value (may be used by a spinner or slider) */
    public static int MIN = 0;

    /** Minium value (may be used by a spinner or slider) */
    public static int MAX = 255;

    /** Used to list sample values */
    public static String OPTIONS = Parameter.OPTIONS;

    /** Used to indicate List element type */
    public static String ELEMENT = Parameter.ELEMENT;

    /**
     * Indicates this expression is required to be non null
     */
    protected boolean isRequired;

    /**
     * FeatureType we are working against if known.
     */
    protected SimpleFeatureType schema;

    /**
     * Default constructor. Calls <code>IExpressionViewer( Composite parent, SWT.SINGLE )</code>
     */
    public IExpressionViewer(Composite parent) {
        this(parent, SWT.SINGLE);
    }

    /**
     * Constructor. All UI components should be added here.
     */
    public IExpressionViewer(Composite parent, int style) {

    }

    /**
     * Returns the controller of the viewer. Used for setting size etc
     */
    public abstract Control getControl();

    /**
     * The isRequired flag will be used to determine the default decoration to show (if there is no
     * warning or error to take precedence).
     * <p>
     * Please note that if this is a required field Expression.NIL is not considered to be a valid
     * state.
     * </p>
     * 
     * @param isRequired true if this is a required field
     */
    public void setRequired(boolean required) {
        this.isRequired = required;
    }

    /**
     * The isRequired flag will be used to determine the default decoration to show (if there is no
     * warning or error to take precedence).
     * <p>
     * 
     * @return true if this is a required field
     */
    public boolean isRequired() {
        return isRequired;
    }

    /**
     * Used to check for any validation messages (such as required field etc...)
     * 
     * @return Validation message
     */
    public abstract String getValidationMessage();

    /**
     * Provides access to the Expression being used by the ExpressionViewer.
     * <p>
     * 
     * @return Expression. May be Expression.NIL if empty (but will not be null)
     */
    public abstract Expression getInput();

    /**
     * Returns the current selection for this provider.
     * 
     * @return chrrent selection
     */
    public abstract ISelection getSelection();

    /**
     * Refreshes this viewer completely with information freshly obtained from this viewer's input
     * (ie an Expression)..
     */
    public abstract void refresh();

    /**
     * Set the input for this ExpressionViewer.
     * 
     * @param input Expression, String or other data object to use as the input for this
     *        ExpressionViewer
     */
    public abstract void setInput(Object input);

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
    public void setSchema(SimpleFeatureType schema) {
        this.schema = schema;
    }

    /**
     * Feature Type used by the ExpressionViewer
     * 
     * @param type
     */
    public SimpleFeatureType getSchema() {
        return schema;
    }

    public abstract void setExpected(Class<?> binding);

    public abstract Class<?> getExpected();

}
