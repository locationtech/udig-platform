package net.refractions.udig.ui.filter;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.batik.gvt.event.SelectionAdapter;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.PageBook;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;

/**
 * {@link IFilterViewer} allowing user to switch between implementations.
 * <p>
 * Note this implementation makes use of FilterViewerFactory when creating each viewer.
 * We ask that you remember the "viewerId" in dialog settings or IMento so the
 * user is not forced to choose which viewer is displayed each time. You can also
 * use this facility as a hint when configuring the viewer for use.
 * <pre>
 * FilterViewer viewer = new FilterViewer( composite, SWT.MULTI );
 * viewer.setViewerId("cql");
 * </pre>
 * You will need to consult the extension point for the list of valid viewerIds.
 * @author Jody Garnett
 * @since 1.3.2
 */
public class FilterViewer extends IFilterViewer {
    /**
     * IFilterViewer currently displayed in {@link #pageBook}.
     */
    protected IFilterViewer delegate;

    private ISelectionChangedListener listener = new ISelectionChangedListener() {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            internalUpdate( delegate.getFilter() );
        }
    };
    
    /** Control used to display {@link #pageBook} and {@link #viewerCombo}. */
    // Composite control;
    
    /**
     * PageBook acting as our control; used to switch between availabel implementations.
     */
    private PageBook pageBook;

    // private ComboViewer viewerCombo;
    
    /**
     * Id of the viewer set by the user using the provided combo; may be supplied as an
     * initial hint or saved and restored using IMemento or DialogSettings in order to preserve
     * user context.
     */
    private String viewerId;

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
    public FilterViewer(Composite parent, int style) {
        super(parent, style);
        
        pageBook = new PageBook( parent, SWT.DEFAULT );
        delegate = new CQLFilterViewer( pageBook, style );
        
        delegate.addSelectionChangedListener(listener);
    }

    public void setViewerId(String id){
        this.viewerId = id;
    }
    
    public String getViewerId() {
        return viewerId;
    }
    
    /**
     * This is the widget used to display the Filter; its parent has been provided in the
     * ExpressionViewer's constructor; but you may need direct access to it in order to set layout
     * data etc.
     * 
     * @return
     */
    public Control getControl() {
        return pageBook;
    }

    @Override
    public void refresh() {
        if (delegate != null) {
            delegate.refresh();
        }
    }
    
    @Override
    public void setInput(Object filterInput) {
        super.setInput(filterInput);
        if( delegate != null ){
            delegate.setInput(filterInput);
        }
    }
    
    /** Used to supply a filter for display or editing */
    @Override
    public void setFilter(Filter filter) {
        if (this.filter == filter) {
            return;
        }
        this.filter = filter;
        if( delegate != null && delegate.getControl() != null && !delegate.getControl().isDisposed() ){
            try {
                delegate.removeSelectionChangedListener(listener);
                delegate.setFilter(filter);
            }
            finally {
                delegate.addSelectionChangedListener(listener);
            }
        }
        fireSelectionChanged(new SelectionChangedEvent(FilterViewer.this, getSelection()));
    }

}