package net.refractions.udig.ui.filter;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

/**
 * FilterViewer with a simple radio button enabling the user to choose between Filter.INCLUDE and
 * Filter.Exclude
 * 
 * @author Scott
 * @since 1.3.0
 */
public class IncludeFilterViewer extends IFilterViewer {
    /**
     * This is the expression we are working on here.
     * <p>
     * We are never going to be "null"; Expression.EXCLUDE is used to indicate an intentionally
     * empty expression.
     */
    protected Filter filter = Filter.EXCLUDE;

    protected Composite control;

    protected Button enableButton;

    protected Button disableButton;

    /**
     * Indicates this is a required field
     */
    private boolean isRequired;

    public IncludeFilterViewer( Composite parent ) {
        this(parent, SWT.SINGLE);
    }
    /**
     * Creates an ExpressionViewer using the provided style.
     * <ul>
     * <li>SWT.SINGLE - A simple text field showing the expression using extended CQL notation; may
     * be shown as a combo box if either a FeatureType or DialogSettings are provided
     * <li>SWT.MULTI - A multi line text field; may be shown as an ExpressionBuilder later on
     * <li>SWT.READ_ONLY - read only
     * <li>
     * <li>SWT.WRAP - useful with SWT.MULTI
     * <li>SWT.LEFT - alignment
     * <li>SWT.RIGHT - alignment
     * <li>SWT.CENTER - alignment
     * </ul>
     * 
     * @param parent
     * @param none
     */
    public IncludeFilterViewer( Composite parent, int style ) {
        super(parent, style);
        control = new Composite(parent, style);

        Label filterLabel = new Label(control, SWT.NONE);
        filterLabel.setBounds(10, 10, 55, 15);
        filterLabel.setText("Filter:");

        enableButton = new Button(control, SWT.RADIO);
        enableButton.addSelectionListener(new SelectionAdapter(){
            @Override
            public void widgetSelected( SelectionEvent e ) {
                filter = Filter.INCLUDE;
            }
        });

        enableButton.setBounds(20, 31, 90, 16);
        enableButton.setText("Enable");

        disableButton = new Button(control, SWT.RADIO);
        disableButton.addSelectionListener(new SelectionAdapter(){
            @Override
            public void widgetSelected( SelectionEvent e ) {
                filter = Filter.EXCLUDE;
            }
        });
        disableButton.setBounds(20, 53, 90, 16);
        disableButton.setText("Disable");
    }

    /**
     * This is the widget used to display the Expression; its parent has been provided in the
     * ExpressionViewer's constructor; but you may need direct access to it in order to set layout
     * data etc.
     * 
     * @return
     */
    public Control getControl() {
        return control;
    }
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
    public void setRequired( boolean isRequired ) {
        this.isRequired = isRequired;
    }

    /**
     * @return true if this is a required field
     */
    public boolean isRequired() {
        return isRequired;
    }

    /**
     * Check if the expr is valid.
     * <p>
     * The default implementation checks that the expr is not null (which would be an error); and
     * that if isRequired is true that a required decoration is shown.
     * <p>
     * Subclasses can overide to perform additional checks (say for entering dates). They should
     * take care to use the feedback decoration in order to indicate to the user any problems
     * encountered.
     * 
     * @return true if the field is valid
     */
    public boolean validate() {
        return true;
    }

    /**
     * Used to check for any validation messages (such as required field etc...)
     * 
     * @return Validation message
     */
    public String getValidationMessage() {

        return null; // all good then
    }
    /**
     * Provides access to the Expression being used by this viewer.
     * <p>
     * 
     * @return Expression being viewed; may be Expression.NIL if empty (but will not be null)
     */
    @Override
    public Filter getInput() {
        return filter;
    }

    @Override
    public ISelection getSelection() {
        if (filter == null) return null;

        IStructuredSelection selection = new StructuredSelection(filter);
        return selection;
    }

    @Override
    public void refresh() {
        if (filter == Filter.INCLUDE) {
            enableButton.setSelection(true);
            disableButton.setSelection(false);
        } else {
            enableButton.setSelection(false);
            disableButton.setSelection(true);
        }
    }

    /**
     * Set the input for this viewer.
     * <p>
     * This viewer accepts several alternative forms of input to get started:
     * <ul>
     * <li>Expression - is used directly
     * <li>String - is parsed by ECQL.toExpression; and if successful it is used
     * </ul>
     * If you have other suggestions (PropertyName could be provided by an AttributeType for
     * example) please ask on the mailing list.
     * 
     * @param input Expression or String to use as the input for this viewer
     */
    @Override
    public void setInput( Object input ) {
        if (input instanceof Filter) {
            filter = (Filter) input;
        }
        refresh();
    }

    @Override
    public void setSelection( ISelection selection, boolean reveal ) {
        // do nothing by default
    }

    /**
     * Provide the feedback that everything is fine.
     * <p>
     * This method will make use of an associated ControlDecoration if available; if not it will
     * make use of a tooltip or something.
     * </p>
     */
    public void feedback() {

    }
    /**
     * Provide the feedback that everything is fine.
     * <p>
     * This method will make use of an associated ControlDecoration if available; if not it will
     * make use of a tooltip or something.
     * </p>
     */
    public void feedback( String warning ) {

    }
    /**
     * Provide the feedback that everything is fine.
     * <p>
     * This method will make use of an associated ControlDecoration if available; if not it will
     * make use of a tooltip or something.
     * </p>
     */
    public void feedback( String error, Exception eek ) {

    }
    /**
     * Feature Type to use for attribute names.
     * 
     * @param type
     */
    public void setSchema( SimpleFeatureType type ) {

    }

    @Override
    public Boolean canProcess( Object input ) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public SimpleFeatureType getSchema() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void setExpected( Class< ? > binding ) {
        // TODO Auto-generated method stub

    }
    @Override
    public Class< ? > getExpected() {
        // TODO Auto-generated method stub
        return null;
    }
}