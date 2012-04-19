package net.refractions.udig.ui.filter;

import net.miginfocom.swt.MigLayout;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.part.PageBook;
import org.opengis.filter.Filter;

/**
 * {@link IFilterViewer} allowing user to switch between implementations.
 * <p>
 * Note this implementation makes use of FilterViewerFactory when creating each viewer. We ask that
 * you remember the "viewerId" in dialog settings or IMento so the user is not forced to choose
 * which viewer is displayed each time. You can also use this facility as a hint when configuring
 * the viewer for use.
 * 
 * <pre>
 * FilterViewer viewer = new FilterViewer(composite, SWT.MULTI);
 * viewer.setViewerId(&quot;cql&quot;);
 * </pre>
 * 
 * You will need to consult the extension point for the list of valid viewerIds.
 * 
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
            internalUpdate(delegate.getFilter());
            // The above internalUpdate will issue a fireSelectionChanged(event)
        }
    };

    /** Control used to display {@link #pageBook} and {@link #viewerCombo}. */
    Composite control;

    /**
     * PageBook acting as our control; used to switch between availabel implementations.
     */
    private PageBook pageBook;

    // private ComboViewer viewerCombo;

    /**
     * Id of the viewer set by the user using the provided combo; may be supplied as an initial hint
     * or saved and restored using IMemento or DialogSettings in order to preserve user context.
     */
    private String viewerId;

    private int style;

    private SelectionListener menuListener = new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            for (MenuItem item : menu.getItems()) {
                if (item == e.widget) {
                    // this is the one we are selecting!
                    // we need to change to match
                    if (!item.getSelection()) {
                        // select this one
                        item.removeSelectionListener(this);
                        item.setSelection(true);
                        item.addSelectionListener(this);
                    }
                } else {
                    if (item.getSelection()) {
                        // unselect this one
                        item.removeSelectionListener(this);
                        item.setSelection(false);
                        item.addSelectionListener(this);
                    }
                }
            }

        }
    };

    private Menu menu;

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
        control = new Composite(parent, SWT.NO_SCROLL);
        control.setLayout(new MigLayout("insets 0", "[fill][]", "[fill]"));

        pageBook = new PageBook(control, SWT.NO_SCROLL);

        pageBook.setLayoutData("cell 0 0,grow,width 200:100%:100%,height 16:75%:90%");

        delegate = new DefaultFilterViewer(pageBook, style);
        delegate.addSelectionChangedListener(listener);
        pageBook.showPage(delegate.getControl());

        Label config = new Label(control, SWT.SINGLE);
        config.setImage(JFaceResources.getImage(PopupDialog.POPUP_IMG_MENU));
        config.setLayoutData("cell 1 0,aligny top,height 16!, width 16!");
        
        menu = new Menu( config );
        MenuItem builderMenuItem = new MenuItem(menu, SWT.RADIO);
        builderMenuItem.setSelection(true);
        builderMenuItem.setText("Builder");
        builderMenuItem.setData("Builder");
        builderMenuItem.addSelectionListener(menuListener);

        MenuItem cqlMenuItem = new MenuItem(menu, SWT.RADIO);
        cqlMenuItem.setText("Constratin Query Language");
        cqlMenuItem.setData("CQL");
        cqlMenuItem.addSelectionListener(menuListener);

        //config.getControl().setMenu(menu);

        config.addMouseListener(new MouseAdapter() {
            public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
                menu.setVisible(true);
            }
        });
        this.style = style;
    }

    public void setViewerId(String id) {
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
        return control;
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
        if (delegate != null) {
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
        if (delegate != null && delegate.getControl() != null
                && !delegate.getControl().isDisposed()) {
            try {
                delegate.removeSelectionChangedListener(listener);
                delegate.setFilter(filter);
            } finally {
                delegate.addSelectionChangedListener(listener);
            }
        }
        fireSelectionChanged(new SelectionChangedEvent(FilterViewer.this, getSelection()));
    }

}