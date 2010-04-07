package net.refractions.udig.filter;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.FunctionFinder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.util.Utilities;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;

/**
 * A JFace Style Expression Viewer that can be used to show an Expression
 * to a user (using whatever SWT widgets are appropriate) and allow modification.
 * <p>
 * Initially we will just use a Text control; gradually working up to PropertyName, Integer and
 * Color Expressions. In each case the Expression may be retrieved by simple get/set methods
 * and we will provide some kind of consistent change notification.
 * <p>
 * Choosing which widgets to use will be based on constants; much like MapViewer switches
 * implementations. If there is something specific we need to handle (like say restrictions
 * based on FeatureType we may need to break out different ExpressionViewers kind of like
 * how Tree and TreeTable viewers work.
 * </p>
 * <p>
 * Remember that although Viewers are a wrapper around some SWT Control or Composite you still
 * have direct access using the getControl() method so that you can do your layout data thing.
 * </p>
 * <p>
 * Future directions from Mark:
 * <ul>
 * <li>
 * @author jive
 * @since 1.1.0
 */
public class ExpressionViewer extends Viewer {
    /**
     * This is the expression we are working on here.
     * <p>
     * We are never going to be "null"; Expression.NIL is used to indicate
     * an intentionally empty expression.
     */
    protected Expression expr = Expression.NIL;
    
    /**
     * This is our internal widget we are sharing with the outside world;
     * in many cases it will be a Composite.
     */
    private Text text;

    /**
     * Indicates this is a required field
     */
    private boolean isRequired;
    
    private KeyListener keyListener = new KeyListener(){        
        public void keyReleased( KeyEvent e ) {
            // we can try and parse this puppy; and issue a selection changed
            // event when we actually have an expression that works            
            String before = expr != null ? CQL.toCQL( expr ) : "(empty)";
            validate();
            String after = expr != null ? CQL.toCQL( expr ) : "(empty)";
            if( expr != null && !Utilities.equals( before, after )){              
                fireSelectionChanged( new SelectionChangedEvent( ExpressionViewer.this, getSelection() ));                
            }
        }
        public void keyPressed( KeyEvent e ) {
        }
    };

    private ControlDecoration feedback;
    
    public ExpressionViewer( Composite parent ){
        this( parent, SWT.SINGLE );
    }
    /**
     * Creates an ExpressionViewer using the provided style.
     * <ul>
     * <li>SWT.SINGLE - A simple text field showing the expression using extended CQL notation;
     *     may be shown as a combo box if either a FeatureType or DialogSettings are provided
     * <li>SWT.MULTI - A multi line text field; may be shown as an ExpressionBuilder later on
     * <li>SWT.READ_ONLY - read only 
     * <li><li>SWT.WRAP - useful with SWT.MULTI
     * <li>SWT.LEFT - alignment
     * <li>SWT.RIGHT - alignment
     * <li>SWT.CENTER - alignment
     * </ul>
     * @param parent
     * @param none
     */
    public ExpressionViewer( Composite parent, int style ) {
        text = new Text( parent, style );
        feedback = new ControlDecoration(text, SWT.TOP | SWT.LEFT);
        
        FunctionFinder ff = new FunctionFinder(null);

        FunctionContentProposalProvider proposalProvider = new FunctionContentProposalProvider();
        proposalProvider.setFiltering(true);
        ContentProposalAdapter adapter = new ContentProposalAdapter(
                text, new TextContentAdapter(), 
                proposalProvider,
                null, null);
        
        text.addKeyListener(keyListener);
    }
    
    /**
     * This is the widget used to display the Expression; its parent has been provided
     * in the ExpressionViewer's constructor; but you may need direct access to it
     * in order to set layout data etc.
     *
     * @return
     */
    public Text getControl(){
        return text;
    }
    /**
     * The isRequired flag will be used to determine the default decoration
     * to show (if there is no warning or error to take precedence).
     * <p>
     * Please note that if this is a required field Expression.NIL is not
     * considered to be a valid state.
     * </p>
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
     * The default implementation checks that the expr is not null (which would be an error);
     * and that if isRequired is true that a required decoration is shown.
     * <p>
     * Subclasses can overide to perform additional checks (say for entering dates). They should
     * take care to use the feedback decoration in order to indicate to the user any problems
     * encountered.
     * 
     * @return true if the field is valid
     */
    public boolean validate(){
        FieldDecorationRegistry decorations = FieldDecorationRegistry.getDefault();        
        try {
            expr = ECQL.toExpression( text.getText() );
        } catch (CQLException e) {
            expr = null;
            feedback.setDescriptionText( e.getSyntaxError() );
            feedback.setImage( decorations.getFieldDecoration( FieldDecorationRegistry.DEC_ERROR).getImage() );
            feedback.show();
            return false;
        }
        if( expr == null ){
            feedback.setDescriptionText("(empty)");
            feedback.setImage( decorations.getFieldDecoration( FieldDecorationRegistry.DEC_ERROR).getImage() );
            feedback.show();
            
            return false; // so not valid!
        }
        if( isRequired && expr == Expression.NIL ){
            feedback.setDescriptionText("Required");
            feedback.setImage( decorations.getFieldDecoration( FieldDecorationRegistry.DEC_REQUIRED ).getImage() );
            feedback.show();
            
            return false;
        }
        feedback.setDescriptionText(null);
        feedback.setImage( null );
        feedback.hide();
        return true;
    }
    
    /**
     * Used to check for any validation messages (such as required field etc...)
     * @return Validation message
     */
    public String getValidationMessage(){
        FieldDecorationRegistry decorations = FieldDecorationRegistry.getDefault();
        if( feedback.getImage() == decorations.getFieldDecoration( FieldDecorationRegistry.DEC_ERROR).getImage() ){
            String errorMessage = feedback.getDescriptionText();
            if( errorMessage == null ){
                errorMessage = "invalid";
            }
            return errorMessage;
        }
        if( feedback.getImage() == decorations.getFieldDecoration( FieldDecorationRegistry.DEC_REQUIRED ).getImage() ){
            String requiredMessage = feedback.getDescriptionText();
            if( requiredMessage == null ){
                requiredMessage = "invalid";
            }
            return requiredMessage;
        }
        return null; // all good then
    }
    /**
     * Provides access to the Expression being used by this viewer.
     * <p>
     * @return Expression being viewed; may be Expression.NIL if empty (but will not be null)
     */
    @Override
    public Expression getInput() {
        return expr;
    }
    
    @Override
    public ISelection getSelection() {
        if( expr == null ) return null;
        
        IStructuredSelection selection = new StructuredSelection(expr);
        return selection;
    }
    
    @Override
    public void refresh() {
        if( text != null && !text.isDisposed()){
            text.getDisplay().asyncExec( new Runnable(){                
                public void run() {
                    if (text == null || text.isDisposed() ) return;
                    String cql = CQL.toCQL(expr);
                    text.setText( cql );                    
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
     * If you have other suggestions (PropertyName could be provided by an AttributeType for example)
     * please ask on the mailing list.
     * @param input Expression or String to use as the input for this viewer
     */
    @Override    
    public void setInput( Object input ) {
        if( input instanceof Expression ){
            expr = (Expression) input;
            refresh();
        }
        else if (input instanceof String){
            final String txt = (String) input;
            try {
                expr = ECQL.toExpression( txt );
            } catch (CQLException e) {
                // feedback that things are bad
            }
            // use the text as provided
            text.getDisplay().asyncExec( new Runnable(){            
                public void run() {
                    text.setText( txt );
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
     * This method will make use of an associated ControlDecoration if available;
     * if not it will make use of a tooltip or something.
     * </p>
     */
    public void feedback(){
        feedback.hide();
    }
    /**
     * Provide the feedback that everything is fine.
     * <p>
     * This method will make use of an associated ControlDecoration if available;
     * if not it will make use of a tooltip or something.
     * </p>
     */
    public void feedback( String warning ){
        if( feedback != null ){
            feedback.setDescriptionText( warning );
            feedback.show();
        }
        Control control = getControl();
        if( control != null && !control.isDisposed() ){
            control.setToolTipText( warning );
        }
    }
    /**
     * Provide the feedback that everything is fine.
     * <p>
     * This method will make use of an associated ControlDecoration if available;
     * if not it will make use of a tooltip or something.
     * </p>
     */
    public void feedback( String error, Exception eek ){
        Control control = getControl();
        if( control != null && !control.isDisposed() ){
            control.setToolTipText( error +":"+ eek );
        }
    }
}

/**
 * SimpleContentProposalProvider is a class designed to map a static list of
 * Strings to content proposals.
 * 
 * @see IContentProposalProvider
 * @since 3.2
 * 
 */
@SuppressWarnings("deprecation")
class FunctionContentProposalProvider implements IContentProposalProvider {

    public static Set<String> proposals;
    static {
        proposals = new TreeSet<String>();
        Set<Function> oldFunctions = CommonFactoryFinder.getFunctionExpressions(null);   
        for( Function function : oldFunctions ){
            proposals.add( function.getName().toLowerCase() );
        }
        Set<Function> newFunctions = CommonFactoryFinder.getFunctions(null);
        for( Function function : newFunctions ){
            proposals.add( function.getName().toLowerCase() );
        }
    }
    
    /*
     * The proposals mapped to IContentProposal. Cached for speed in the case
     * where filtering is not used.
     */
    private IContentProposal[] contentProposals;

    /*
     * Boolean that tracks whether filtering is used.
     */
    private boolean filterProposals = false;

    /**
     * Construct a SimpleContentProposalProvider whose content proposals are
     * always the specified array of Objects.
     * 
     * @param proposals
     *            the array of Strings to be returned whenever proposals are
     *            requested.
     */
    public FunctionContentProposalProvider() {
    }

    /**
     * Return an array of Objects representing the valid content proposals for a
     * field. 
     * 
     * @param contents
     *            the current contents of the field (only consulted if filtering
     *            is set to <code>true</code>)
     * @param position
     *            the current cursor position within the field used to select a word
     * @return the array of Objects that represent valid proposals for the field
     *         given its current content.
     */
    public IContentProposal[] getProposals(String contents, int position) {
        String word = contents.substring(0,position);
        int start = contents.lastIndexOf(" ", position);
        if( start != -1 ){
            word = contents.substring( start, position );
        }
        word = word.trim();
        if( word.length() == 0 ){
            return new IContentProposal[0];
        }
        
        if (filterProposals) {
            ArrayList<IContentProposal> list = new ArrayList<IContentProposal>();
            for( String proposal : proposals ) {
                if (proposal.length() >= word.length()
                        && proposal.substring(0, word.length())
                                .equalsIgnoreCase(word)) {
                    list.add(makeContentProposal(proposal));
                }
            }
            return (IContentProposal[]) list.toArray(new IContentProposal[list
                    .size()]);
        }
        if (contentProposals == null) {
            contentProposals = new IContentProposal[proposals.size()];
            int i = 0;
            for( String proposal : proposals ){
                contentProposals[i] = makeContentProposal(proposal);
            }
        }
        return contentProposals;
    }

    /**
     * Set the boolean that controls whether proposals are filtered according to
     * the current field content.
     * 
     * @param filterProposals
     *            <code>true</code> if the proposals should be filtered to
     *            show only those that match the current contents of the field,
     *            and <code>false</code> if the proposals should remain the
     *            same, ignoring the field content.
     * @since 3.3
     */
    public void setFiltering(boolean filterProposals) {
        this.filterProposals = filterProposals;
        // Clear any cached proposals.
        contentProposals = null;
    }

    /*
     * Make an IContentProposal for showing the specified String.
     */
    private IContentProposal makeContentProposal(final String proposal) {
        return new IContentProposal() {
            public String getContent() {
                return proposal;
            }

            public String getDescription() {
                return null;
            }

            public String getLabel() {
                return null;
            }

            public int getCursorPosition() {
                return proposal.length();
            }
        };
    }
}

