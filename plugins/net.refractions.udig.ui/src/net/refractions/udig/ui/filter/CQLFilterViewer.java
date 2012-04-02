package net.refractions.udig.ui.filter;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.util.Utilities;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;

/**
 * A JFace Style Filter Viewer that can be used to build a Filter. Has both a text box for manually
 * entering a Filter and a series of combo boxes to allow the user to build their own filter
 * 
 * @author Scott Henderson
 * @since 1.3.0
 */
public class CQLFilterViewer extends IFilterViewer {
    /**
     * This is the expression we are working on here.
     * <p>
     * We are never going to be "null"; Expression.EXCLUDE is used to indicate an intentionally
     * empty expression.
     */
    protected Filter filter = Filter.EXCLUDE;

    protected Composite control;

    /**
     * This is our internal widget we are sharing with the outside world; in many cases it will be a
     * Composite.
     */
    private Text text;

    /**
     * Combo box to allow the user to select an attribute to base a filter on
     */
    private Combo attribute;

    /**
     * Combo box to allow the user to select an operation to apply to an attribute
     */
    private Combo operation;

    /**
     * Text box to allow the user to enter a value to base a filter on
     */
    private Text value;

    private Button insert;

    /**
     * Indicates this is a required field
     */
    private boolean isRequired;

    private KeyListener keyListener = new KeyListener(){
        public void keyReleased( KeyEvent e ) {
            // we can try and parse this puppy; and issue a selection changed
            // event when we actually have an expression that works
            String before = filter != null ? CQL.toCQL(filter) : "(empty)";
            validate();
            String after = filter != null ? CQL.toCQL(filter) : "(empty)";
            if (filter != null && !Utilities.equals(before, after)) {
                fireSelectionChanged(new SelectionChangedEvent(CQLFilterViewer.this, getSelection()));
            }
        }
        public void keyPressed( KeyEvent e ) {
        }
    };

    private ControlDecoration feedback;

    private FunctionContentProposalProvider proposalProvider;

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
    public CQLFilterViewer( Composite parent, int style ) {
        super(parent, style);
        control = new Composite(parent, style);

        text = new Text(control, SWT.BORDER);
        text.setBounds(10, 10, 430, 60);
        feedback = new ControlDecoration(text, SWT.TOP | SWT.LEFT);

        proposalProvider = new FunctionContentProposalProvider();
        proposalProvider.setFiltering(true);
        ContentProposalAdapter adapter = new ContentProposalAdapter(text, new TextContentAdapter(),
                proposalProvider, null, null);

        // Need to set adapter to replace existing text. Default is insert.
        adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

        text.addKeyListener(keyListener);

        Label lblAttribute = new Label(control, SWT.NONE);
        lblAttribute.setBounds(10, 85, 55, 15);
        lblAttribute.setText("Attribute");

        attribute = new Combo(control, SWT.NONE);
        attribute.setBounds(10, 105, 91, 23);

        Label lblOperation = new Label(control, SWT.NONE);
        lblOperation.setBounds(124, 85, 55, 15);
        lblOperation.setText("Operation:");

        operation = new Combo(control, SWT.NONE);
        operation.setBounds(124, 105, 91, 23);
        operation.add("=");
        operation.add("<");
        operation.add(">");
        operation.add("LIKE");

        Label lblValue = new Label(control, SWT.NONE);
        lblValue.setBounds(241, 85, 55, 15);
        lblValue.setText("Value");

        value = new Text(control, SWT.BORDER);
        value.setBounds(241, 105, 91, 23);

        insert = new Button(control, SWT.NONE);
        insert.setBounds(354, 103, 75, 25);
        insert.setText("Insert");
        insert.addSelectionListener(new SelectionListener(){

            @Override
            public void widgetSelected( SelectionEvent e ) {
                StringBuffer str = new StringBuffer();
                str.append(attribute.getText() + " " + operation.getText() + " " + value.getText());
                text.setText(str.toString());
                validate();
            }

            @Override
            public void widgetDefaultSelected( SelectionEvent e ) {
                // TODO Auto-generated method stub

            }
        });

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
        FieldDecorationRegistry decorations = FieldDecorationRegistry.getDefault();
        try {
            filter = ECQL.toFilter(text.getText());
        } catch (CQLException e) {
            filter = null;
            feedback.setDescriptionText(e.getSyntaxError());
            feedback.setImage(decorations.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR)
                    .getImage());
            feedback.show();
            return false;
        }
        if (filter == null) {
            feedback.setDescriptionText("(empty)");
            feedback.setImage(decorations.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR)
                    .getImage());
            feedback.show();

            return false; // so not valid!
        }
        if (isRequired && filter == Expression.NIL) {
            feedback.setDescriptionText("Required");
            feedback.setImage(decorations.getFieldDecoration(FieldDecorationRegistry.DEC_REQUIRED)
                    .getImage());
            feedback.show();

            return false;
        }
        feedback.setDescriptionText(null);
        feedback.setImage(null);
        feedback.hide();
        return true;
    }

    /**
     * Used to check for any validation messages (such as required field etc...)
     * 
     * @return Validation message
     */
    public String getValidationMessage() {
        FieldDecorationRegistry decorations = FieldDecorationRegistry.getDefault();
        if (feedback.getImage() == decorations
                .getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage()) {
            String errorMessage = feedback.getDescriptionText();
            if (errorMessage == null) {
                errorMessage = "invalid";
            }
            return errorMessage;
        }
        if (feedback.getImage() == decorations.getFieldDecoration(
                FieldDecorationRegistry.DEC_REQUIRED).getImage()) {
            String requiredMessage = feedback.getDescriptionText();
            if (requiredMessage == null) {
                requiredMessage = "invalid";
            }
            return requiredMessage;
        }
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
        if (text != null && !text.isDisposed()) {
            text.getDisplay().asyncExec(new Runnable(){
                public void run() {
                    if (text == null || text.isDisposed()) return;
                    String cql = CQL.toCQL(filter);
                    text.setText(cql);
                }
            });
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
            refresh();
        } else if (input instanceof String) {
            final String txt = (String) input;
            try {
                filter = ECQL.toFilter(txt);
            } catch (CQLException e) {
                // feedback that things are bad
            }
            // use the text as provided
            text.getDisplay().asyncExec(new Runnable(){
                public void run() {
                    text.setText(txt);
                }
            });
        }
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
        feedback.hide();
    }
    /**
     * Provide the feedback that everything is fine.
     * <p>
     * This method will make use of an associated ControlDecoration if available; if not it will
     * make use of a tooltip or something.
     * </p>
     */
    public void feedback( String warning ) {
        if (feedback != null) {
            feedback.setDescriptionText(warning);
            feedback.show();
        }

        if (text != null && !text.isDisposed()) {
            text.setToolTipText(warning);
        }
    }
    /**
     * Provide the feedback that everything is fine.
     * <p>
     * This method will make use of an associated ControlDecoration if available; if not it will
     * make use of a tooltip or something.
     * </p>
     */
    public void feedback( String error, Exception eek ) {

        if (text != null && !text.isDisposed()) {
            text.setToolTipText(error + ":" + eek);
        }
    }
    /**
     * Feature Type to use for attribute names.
     * 
     * @param type
     */
    public void setSchema( SimpleFeatureType type ) {

        if (type == null) {
            return;
        }
        Set<String> names = new HashSet<String>();
        for( AttributeDescriptor att : type.getAttributeDescriptors() ) {
            //add to Text area
            names.add(att.getLocalName());
            //add to combo box
            attribute.add(att.getLocalName());
        }
        proposalProvider.setExtra(names);
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