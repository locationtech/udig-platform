package net.refractions.udig.ui.filter;

import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;

/**
 * Simple {@link IFilterViewer} which uses a {@link Text} to edit a Filter using Constraint Query
 * Language.
 * <p>
 * This represents a simple, full featured, filter viewer and can be extended as a starting point
 * for your own implementation.
 * <p>
 * If you would like to extend this viewer please keep in mind that the constructor will add a
 * single Text control to the provided composite. You can make use of this in your own
 * implementation as follows:
 * 
 * <pre>
 * public MyFilterViewer( Composite composite, int style ){
 *     super( panel = new Composite( parent), style );
 *     ...
 * }
 * </pre>
 * <p>
 * Each time the text is successfully parsed the {@link #getFilter()} is is updated with the latest
 * value and a {@link SelectionEvent} sent out - usin the {@link #internalUpdate(Filter)} method.
 * <p>
 * Suggestions are provided as you type using {@link #proposalProvider}. The {@link #refresh()}
 * method is called each time {@link #setInput(Object)} is used to change the {@link FilterInput}
 * giving you a chance to call {@link FunctionContentProposalProvider#setExtra(java.util.Set)} with
 * any suggestions you would like to add into the mix.
 * 
 * @author Jody Garnett
 * @since 1.3.0
 */
public class CQLFilterViewer extends IFilterViewer {
    /**
     * Factory used to create our basic CQLFilterViewer as a bare bones
     * {@link FilterViewerFactory#COMPLETE} implementation capable of editing any filter.
     * 
     * @author Jody Garnett
     * @since 1.3.2
     */
    public static class Factory extends FilterViewerFactory {
        @Override
        /**
         * Slightly prefer CQLFilterViewer to DefaultFilterViewer.
         */
        public int appropriate(SimpleFeatureType schema, Filter filter) {
            return COMPLETE + 1;
        }

        public IFilterViewer createViewer(Composite parent, int style) {
            return new CQLFilterViewer(parent, style);
        }
    }

    /**
     * The text widget used to enter CQL; each time it is successfully parsed the filter will be
     * updated and a selection changed event sent out.
     */
    protected Text text;

    private KeyListener keyListener = new KeyAdapter() {
        public void keyReleased(KeyEvent e) {
            changed();
        }
    };

    private FunctionContentProposalProvider proposalProvider;

    /**
     * Used to configure {@link Text#setSize(int, int)} in terms of a number of rows and columns
     * (with the calculation based on the current font).
     * <p>
     * This size is used as the "preferred" size when the layout manager is doing its work.
     * 
     * @param numberOfColumns
     * @param numberOfRows
     */
    protected void setPreferredTextSize( int numberOfColumns, int numberOfRows ){
        GC gc = new GC (text);
        FontMetrics fm = gc.getFontMetrics ();
        int width = 30 * fm.getAverageCharWidth ();
        int height = fm.getHeight ();
        gc.dispose();
        text.setSize (text.computeSize (width, height));
    }
    /**
     * Creates an FilterViewer using the provided style.
     * <ul>
     * <li>SWT.SINGLE - A simple text field showing the expression using extended CQL notation
     * <li>
     * <li>SWT.MULTI - A multi line text field</li>
     * <li>SWT.READ_ONLY - read only display of a filter</li>
     * </ul>
     * 
     * @param parent
     * @param style
     */
    public CQLFilterViewer(Composite parent, int style) {
        if ((style & SWT.SINGLE) != 0) {
            int textStyle = SWT.SINGLE | SWT.BORDER;
            text = new Text(parent, textStyle);
            if( (style & SWT.READ_ONLY) != 0 ){
                textStyle |= SWT.READ_ONLY;
            }
            // setPreferredTextSize(30,1 );
        }
        else if ((style & SWT.MULTI) != 0) {
            int textStyle = SWT.MULTI|SWT.WRAP|SWT.BORDER|SWT.V_SCROLL;
            if( (style & SWT.READ_ONLY) != 0 ){
                textStyle |= SWT.READ_ONLY;
            }
            text = new Text(parent, textStyle);
            setPreferredTextSize(60,3);
        }
        else { // SWT.DEFAULT for example
            text = new Text(parent, SWT.SINGLE | SWT.BORDER);
            setPreferredTextSize(30,1 );
        }

        proposalProvider = new FunctionContentProposalProvider();
        TextContentAdapter contentAdapter = new TextContentAdapter();
        
        ContentProposalAdapter adapter = new ContentProposalAdapter(text, contentAdapter,
                proposalProvider, null, null);

        // Need to set adapter to replace existing text. Default is insert.
        adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_INSERT);
        adapter.setPopupSize( new Point( 400, 300 ));
        text.addKeyListener(keyListener);
    }

    /**
     * This is the widget used to display the Filter; its parent has been provided in the
     * ExpressionViewer's constructor; but you may need direct access to it in order to set layout
     * data etc.
     * 
     * @return
     */
    public Control getControl() {
        return text;
    }

    /**
     * Called when a key is pressed to check if the filter has changed.
     */
    protected void changed() {
        Filter parsedFilter = validate();
        if (parsedFilter != null) {
            internalUpdate(parsedFilter);
        }
    }
    /** Workaround to support INCLUDE / EXCLUDE pending https://jira.codehaus.org/browse/GEOT-4110 */
    protected Filter toFilter( String txt ) throws CQLException{
        if( txt == null ){
            return null;
        }
        else if( "INCLDUE".equals( txt.trim() )){
            return Filter.INCLUDE;
        }
        else if( "EXCLUDE".equals( txt.trim() )){
            return Filter.EXCLUDE;
        }
        return ECQL.toFilter(txt);
    }
    /** Workaround to support INCLUDE / EXCLUDE pending https://jira.codehaus.org/browse/GEOT-4110 */
    protected String toCQL(Filter filter) {
        if( filter == null ){
            return null;
        }
        else if( filter == Filter.INCLUDE ){
            return "INCLUDE";
        }
        else if( filter == Filter.EXCLUDE ){
            return "EXCLUDE";
        }
        return ECQL.toCQL(filter);
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
    protected Filter validate() {
        Filter parsedFilter;
        try {
            parsedFilter = toFilter(text.getText());
        } catch (CQLException e) {
            feedback(e.getLocalizedMessage(), e);
            return null;
        }
        if (parsedFilter == null) {
            feedback("(empty)");
            return null;
        }
        if (input != null && input.isRequired() && filter == Expression.NIL) {
            feedback("Required", true);
            return null;
        }
        feedback();
        return parsedFilter;
    }

    @Override
    public void refresh() {
        if (input != null) {
            SortedSet<String> names = new TreeSet<String>(input.toPropertyList());
            proposalProvider.setExtra(names);
        }
        refreshFilter();
    }

    /** Used to supply a filter for display or editing */
    @Override
    public void setFilter(Filter filter) {
        if (this.filter == filter) {
            return;
        }
        this.filter = filter;
        refreshFilter();
        fireSelectionChanged(new SelectionChangedEvent(CQLFilterViewer.this, getSelection()));
    }

    /** Called to update the viewer text control to display the provided filter */
    private void refreshFilter() {
        if (text != null && !text.isDisposed()) {
            text.getDisplay().asyncExec(new Runnable() {
                public void run() {
                    if (text == null || text.isDisposed())
                        return;

                    if (filter == null) {
                        text.setText("");
                    } else {
                        String cql = toCQL(filter);
                        text.setText(cql);
                    }
                }
            });
        }
    }


}