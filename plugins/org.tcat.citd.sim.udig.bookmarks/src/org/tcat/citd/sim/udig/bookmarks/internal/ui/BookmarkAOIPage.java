/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

package org.tcat.citd.sim.udig.bookmarks.internal.ui;

import java.util.Collection;
import java.util.List;

import net.refractions.udig.aoi.AOIProxy;
import net.refractions.udig.aoi.IAOIService;
import net.refractions.udig.aoi.IAOIStrategy;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.tcat.citd.sim.udig.bookmarks.BookmarkAOIStrategy;
import org.tcat.citd.sim.udig.bookmarks.BookmarkListener;
import org.tcat.citd.sim.udig.bookmarks.BookmarksPlugin;
import org.tcat.citd.sim.udig.bookmarks.IBookmark;
import org.tcat.citd.sim.udig.bookmarks.IBookmarkService;

/**
 * A page to add to the AOI (Area of Interest) View used for additional configuration of the AOI.
 * <p>
 * Idea:add a combo to select a bookmark and use the current bookmark as the AOI. 
 * 
 * @author jody
 * @version 1.3.0
 */
public class BookmarkAOIPage extends Page {

    private Composite page;
    private AOIProxy strategy;
    private ComboViewer comboViewer;

    private ISelectionChangedListener comboListener = new ISelectionChangedListener(){
        @Override
        public void selectionChanged( SelectionChangedEvent event ) {
            IStructuredSelection selectedBookmark = (IStructuredSelection) event.getSelection();
            IBookmark selected = (IBookmark) selectedBookmark.getFirstElement();
            
            IAOIService service = PlatformGIS.getAOIService();
            IAOIStrategy bookmarkStrategy = service.findProxy(BookmarkAOIStrategy.ID).getStrategy();
            
            if (bookmarkStrategy instanceof BookmarkAOIStrategy) {
                ((BookmarkAOIStrategy) bookmarkStrategy).setCurrentBookmark(selected);
            }
        }
    };

    /*
     * Listens to the workbench IBookmarkService and updates our view if anything changes!
     */
    private BookmarkListener serviceWatcher = new BookmarkListener(){
        
        public void handleEvent( BookmarkListener.Event event ) {
            // must be run in the UI thread to be able to call setSelected
            PlatformGIS.asyncInDisplayThread(new Runnable(){
                
                @Override
                public void run() {
                    IBookmark currentBookmark = getSelected();
                    Collection<IBookmark> bookmarks = BookmarksPlugin.getBookmarkService().getBookmarks();
                    comboViewer.setInput(bookmarks);
                    // check if the current bookmark still exists
                    if (bookmarks.contains(currentBookmark)) {
                        setSelected(currentBookmark);
                    }
                    else {
                        // this may need to reset the strategy but at this stage 
                        // the bookmarkAOIStrategy holds on to the current bookmark
                        // even when it is deleted
                        setSelected(null);
                    }
                    
                }
            }, true);
        }
        
    };

    public BookmarkAOIPage() {
        // careful don't do any work here
    }
    
    // We would overrride init if we needed to (remmeber to call super)
    @Override
    public void init(IPageSite pageSite){
        super.init(pageSite); // provides access to stuff
        IAOIService service = PlatformGIS.getAOIService();
        strategy = service.findProxy(BookmarkAOIStrategy.ID);        
    }
    
    protected BookmarkAOIStrategy getStrategy(){
        if( strategy == null ){
            return null;
        }
        return (BookmarkAOIStrategy) strategy.getStrategy();
    }
    

    @Override
    public void createControl( Composite parent ) {
        page = new Composite(parent, SWT.NONE);
        
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        page.setLayout(layout);

        Label label = new Label(page, SWT.LEFT);
        //label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        label.setText("Bookmarks ");
        label.pack();
        
        IBookmarkService bookmarkService = BookmarksPlugin.getBookmarkService();
        listenService(true);
        
        comboViewer = new ComboViewer(page, SWT.READ_ONLY);
        comboViewer.setContentProvider(new ArrayContentProvider());
        comboViewer.setLabelProvider(new LabelProvider(){
            @Override
            public String getText( Object element ) {
                if (element instanceof IBookmark) {
                    IBookmark bookmark = (IBookmark) element;
                    return bookmark.getName();
                }
                return super.getText(element);
            }
        });
        List<IBookmark> bookmarks = (List<IBookmark>)bookmarkService.getBookmarks();
        comboViewer.setInput(bookmarks);
        
        comboViewer.addSelectionChangedListener(comboListener);
        
        if (strategy != null ){
            // add any listeners to strategy
            
        }
    }
    
    /*
     * This will update the combo viewer (carefully unhooking events while the viewer is updated).
     * 
     * @param selected
     */
    private void setSelected( IBookmark selected ) {
//        if (selected == null) {
//            return;
//        }
        
        boolean disposed = comboViewer.getControl().isDisposed();
        if (comboViewer == null || disposed) {
            listenService(false);
            return; // the view has shutdown!
        }
        
        IBookmark current = getSelected();
        // check combo
        if (current != selected) {
            try {
                //listenCombo(false);
                comboViewer.setSelection(new StructuredSelection(selected), true);
            } finally {
                //listenCombo(true);
            }
        }

    }
    
    /*
     * Get the Bookmark selected by the user
     * 
     * @return Ibookmark selected by the user
     */
    private IBookmark getSelected() {
        if (comboViewer.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) comboViewer.getSelection();
            return (IBookmark) selection.getFirstElement();
        }
        return null;
    }

    protected void listenService( boolean listen ){
        IBookmarkService bookmarkService = BookmarksPlugin.getBookmarkService();
        if (listen) {
            bookmarkService.addListener(serviceWatcher);
        } else {
            bookmarkService.removeListener(serviceWatcher);
        }
    }

    @Override
    public Composite getControl() {
        return page;
    }

    @Override
    public void setFocus() {
        if (page != null && !page.isDisposed()) {
            page.setFocus();
        }
    }
    
    @Override
    public void dispose() {
        if( page != null ){
            // remove any listeners            
        }
        if( strategy != null ){
            // remove any listeners
            strategy = null;
        }
        super.dispose();
    }

}
