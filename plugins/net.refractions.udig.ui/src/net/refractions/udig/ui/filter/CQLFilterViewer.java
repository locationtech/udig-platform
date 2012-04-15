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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
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
     * Creates an ExpressionViewer using the provided style.
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
        super(parent, style);

        if ((style & SWT.SINGLE) != 0) {
            text = new Text(parent, SWT.SINGLE | SWT.BORDER);
        }
        else if ((style & SWT.MULTI) != 0) {
            text = new Text(parent, SWT.MULTI|SWT.WRAP|SWT.BORDER|SWT.V_SCROLL);
        }
        else { // SWT.DEFAULT for example
            text = new Text(parent, SWT.SINGLE | SWT.BORDER);
        }
        
        proposalProvider = new FunctionContentProposalProvider();
        TextContentAdapter contentAdapter = new TextContentAdapter();
        
        ContentProposalAdapter adapter = new ContentProposalAdapter(text, contentAdapter,
                proposalProvider, null, null);

        // Need to set adapter to replace existing text. Default is insert.
        adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_INSERT);

        text.addKeyListener(keyListener);
    }

    /**
     * This is the widget used to display the Expression; its parent has been provided in the
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
            parsedFilter = ECQL.toFilter(text.getText());
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
                        String cql = CQL.toCQL(filter);
                        text.setText(cql);
                    }
                }
            });
        }
    }

}