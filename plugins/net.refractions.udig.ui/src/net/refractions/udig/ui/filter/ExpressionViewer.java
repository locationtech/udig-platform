/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.ui.filter;

import java.util.HashMap;
import java.util.List;

import net.miginfocom.swt.MigLayout;
import net.refractions.udig.ui.filter.ViewerFactory.Appropriate;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.part.PageBook;
import org.opengis.filter.expression.Expression;

/**
 * {@link IExpressionViewer} allowing user to switch between implementations.
 * <p>
 * Note this implementation makes use of {@link ExpressionViewerFactory} when creating each viewer. We ask that
 * you remember the "viewerId" in dialog settings or IMento so the user is not forced to choose
 * which viewer is displayed each time. You can also use this facility as a hint when configuring
 * the viewer for use.
 * 
 * <pre>
 * ExpressionViewer viewer = new ExpressionViewer(composite, SWT.MULTI);
 * viewer.getControl().setLayoutData("grow, gap unrelated");
 * </pre>
 * 
 * You will need to consult the extension point for the list of valid viewerIds.
 * 
 * @author Jody Garnett
 * @since 1.3.2
 */
public class ExpressionViewer extends IExpressionViewer {
    /**
     * IExpressionViewer currently displayed in {@link #pageBook}.
     */
    protected IExpressionViewer delegate;

    private ISelectionChangedListener listener = new ISelectionChangedListener() {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            internalUpdate(delegate.getExpression());
            // The above internalUpdate will issue a fireSelectionChanged(event)
        }
    };

    /** Control used to display {@link #pageBook} and {@link #viewerCombo}. */
    Composite control;

    /**
     * PageBook acting as our control; used to switch between available implementations.
     */
    private PageBook pageBook;

    /**
     * Id of the viewer set by the user using the provided combo; may be supplied as an initial hint
     * or saved and restored using IMemento or DialogSettings in order to preserve user context.
     */
    private String viewerId;
    
    /**
     * Remember the style used so we can pass it on when we create a delegate
     */
    private int style;

    private SelectionListener menuListener = new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            MenuItem menuItem = (MenuItem) e.widget;
            
            Object data = menuItem.getData();
            boolean selected = menuItem.getSelection();

            if( selected && data instanceof String ){
                showViewer( (String) data );
            }
            if( selected && data instanceof ContributionItem ){
                ContributionItem item = (ContributionItem) data;
                
                showViewer( (String) item.getId() );
            }
        }
    };

    /** Cache of viewers responsible expression display and input */
    private HashMap<String, IExpressionViewer> pages;

    /** Place holder displayed to request the user choose a viewer */
    private Label placeholder;

    /** Label offering the popup menu symbol */
    private Label config;

    public ExpressionViewer(Composite parent) {
        this( parent, SWT.DEFAULT );
    }
    /**
     * Creates an ExpressionViewer using the provided style.
     * <ul>
     * <li>SWT.SINGLE - A simple text field showing the expression using extended CQL notation
     * <li>
     * <li>SWT.MULTI - A multi line text field</li>
     * <li>SWT.READ_ONLY - read only display of a expression</li>
     * </ul>
     * 
     * @param parent
     * @param style
     */
    public ExpressionViewer(Composite parent, int style) {
        control = new Composite(parent, SWT.NO_SCROLL){
            public void setEnabled(boolean enabled) {
                super.setEnabled(enabled);
                config.setEnabled(enabled);
                if( delegate != null ){
                    config.setEnabled(enabled);
                }
                if( input != null && input.getFeedback() != null && input.getFeedback().getControl() != null ){
                    input.getFeedback().getControl().setEnabled(enabled);
                }
            }
        };
        
        control.setLayout(new MigLayout("insets 0", "[fill][]", "[fill]"));

        pageBook = new PageBook(control, SWT.NO_SCROLL);
        pageBook.setLayoutData("cell 0 0,grow,width 200:100%:100%,height 18:75%:100%");
        
        placeholder = new Label( pageBook, SWT.SINGLE );
        placeholder.setText("Choose expression editor");
        
        delegate = new CQLExpressionViewer(pageBook, style);
        delegate.addSelectionChangedListener(listener);
        pageBook.showPage(delegate.getControl());
        
        this.pages = new HashMap<String,IExpressionViewer>();
        pages.put( ExpressionViewerFactory.CQL_EXPRESSION_VIEWER, delegate );
        
        config = new Label(control, SWT.SINGLE);
        config.setImage(JFaceResources.getImage(PopupDialog.POPUP_IMG_MENU));
        config.setLayoutData("cell 1 0,aligny top,height 16!, width 16!");
        
        createContextMenu( config );
        config.addMouseListener(new MouseAdapter() {
            public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
                Menu menu = config.getMenu();
                if( menu != null ){
                    menu.setVisible(true);
                }
            }
        });
        this.style = style;
    }

    protected void showViewer(String newViewerId) {
        if( newViewerId == null ){
            // show place holder label or default to CQL
            newViewerId = "net.refractions.udig.ui.cqlExpressionViewer";
        }
        this.viewerId = newViewerId;
        
        // update the pagebook if needed
        IExpressionViewer viewer = getViewer( this.viewerId );
        
        String cqlText = null;
        if( delegate instanceof CQLExpressionViewer ){
            CQLExpressionViewer cqlViewer = (CQLExpressionViewer) delegate;
            cqlText = cqlViewer.text.getText();
        }
        
        if( viewer == null ){
            pageBook.showPage(placeholder);
            // pageBook.setSize( placeholder.)
        }
        else {
            feedback(); // clear any warnings
            
            // configure viewer before display!
            ExpressionInput currentInput = getInput();
            Expression currentExpression = getExpression();
            
            viewer.setInput( currentInput );
            viewer.setExpression( currentExpression );
            viewer.refresh();
            viewer.getControl().getParent().layout();
            // if available we can carry over the users text - typos and all
            if( cqlText != null && viewer instanceof CQLExpressionViewer){
                CQLExpressionViewer cqlViewer = (CQLExpressionViewer) viewer;
                cqlViewer.text.setText( cqlText );
            }
            // show page and listen to it for changes
            pageBook.showPage(viewer.getControl());
            viewer.addSelectionChangedListener(listener);
        }
        if( delegate != null ){
            // showPage has already hidden delegate.getControl()
            // so now we need to unplug it
            delegate.removeSelectionChangedListener( listener );
            delegate.setInput(null);
        }
        delegate = viewer;
    }
    /**
     * Lookup viewer implementation for the provided viewerId.
     * <p>
     * The viewer will be created if needed; however it will not be hooked
     * up with {@link #setInput}, {@link #setExpression} and {@link #addSelectionChangedListener}
     * as this is done by {@link #showViewer(String)} when on an as needed basis.
     * 
     * @param viewerId
     * @return IExpressionViewer or null if not available
     */
    private IExpressionViewer getViewer(String lookupId) {
        IExpressionViewer viewer = pages.get( lookupId );
        if( viewer != null ){
            // already constructed
            return viewer;
        }
        for( ExpressionViewerFactory factory : ExpressionViewerFactory.factoryList() ){
            if( factory.getId().equals( lookupId )){
                viewer = factory.createViewer( pageBook, this.style );
                pages.put( factory.getId(), viewer );
                
                return viewer;
            }
        }
        return null; // user has requested an unknown id - please display placeholder
    }

    /**
     * ViewerId currently shown (as selected by the user)
     * 
     * @return viewerId currently shown (as selected by the user)
     */
    public String getViewerId() {
        return viewerId;
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

    @Override
    public void refresh() {
        if( viewerId == null && getInput() != null && getInput().getViewerId() != null ){
            // if the user has not already chosen a viewer; use the one marked down from dialog settings
            showViewer( getInput().getViewerId() );
        }
        if (delegate != null) {
            delegate.refresh();
        }
        List<ExpressionViewerFactory> list = ExpressionViewerFactory.factoryList( getInput(), getExpression() );
         if( !list.isEmpty() ){
             ExpressionViewerFactory factory = list.get(0);
             showViewer(factory.getId());
         }
    }

    @Override
    public void setInput(Object expressionInput) {
        super.setInput(expressionInput);
        if (delegate != null) {
            delegate.setInput(expressionInput);
        }
    }

    /** Used to supply a expression for display or editing */
    @Override
    public void setExpression(Expression expression) {
        if (this.expression == expression) {
            return;
        }
        this.expression = expression;
        if (delegate != null && delegate.getControl() != null
                && !delegate.getControl().isDisposed()) {
            try {
                delegate.removeSelectionChangedListener(listener);
                delegate.setExpression(expression);
            } finally {
                delegate.addSelectionChangedListener(listener);
            }
        }
        fireSelectionChanged(new SelectionChangedEvent(ExpressionViewer.this, getSelection()));
    }
    
    private void createContextMenu( Control control ){
        final MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true); // we are going to generate
        
        menuManager.addMenuListener( new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                Appropriate current = null;
                for( ExpressionViewerFactory factory : ExpressionViewerFactory.factoryList( getInput(), getExpression() ) ){
                    int currentScore = factory.score(getInput(), getExpression() );
                    Appropriate category = Appropriate.valueOf( currentScore );
                    if( current == null ){
                        current = category;
                    }
                    else if( current != category ){
                        menuManager.add( new Separator( current.name() ));
                        current = category;
                    }
                    ExpressionViewerFactoryContributionItem contributionItem = new ExpressionViewerFactoryContributionItem(factory);
                    
                    menuManager.add( contributionItem );
                }
            }
        });
        Menu menu = menuManager.createContextMenu( control );
        control.setMenu( menu );
    }
    
    class ExpressionViewerFactoryContributionItem extends ContributionItem {
        
        private ExpressionViewerFactory factory;
        
        ExpressionViewerFactoryContributionItem( ExpressionViewerFactory factory ){
            setId( factory.getId() );
            this.factory = factory;
        }
        @Override
        public void fill(Menu menu, int index) {
            MenuItem item = new MenuItem( menu, SWT.RADIO, index );
            
            item.setText( factory.getDisplayName() );
            item.setData( factory.getId() );
            item.setSelection( factory.getId().equals( viewerId ) );
            item.addSelectionListener( menuListener );
            
            int score = factory.score( getInput(), getExpression() );
            
            if( Appropriate.valueOf(score) == Appropriate.NOT_APPROPRIATE ){
                item.setEnabled(false);
            }
        }
    }

}