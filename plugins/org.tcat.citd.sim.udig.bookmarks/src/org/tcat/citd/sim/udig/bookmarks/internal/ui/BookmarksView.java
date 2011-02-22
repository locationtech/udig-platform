package org.tcat.citd.sim.udig.bookmarks.internal.ui;

import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.part.ViewPart;
import org.tcat.citd.sim.udig.bookmarks.Bookmark;
import org.tcat.citd.sim.udig.bookmarks.internal.BookmarksContentProvider;
import org.tcat.citd.sim.udig.bookmarks.internal.BookmarksLabelProvider;
import org.tcat.citd.sim.udig.bookmarks.internal.actions.BookmarkAction;

/**
 * This is the view that displays the <code>Bookmark</code>s. The content provider connects to
 * the <code>BookmarkManager</code> which is the model.
 * <p>
 *
 * @author cole.markham
 * @since 1.0.0
 */

public class BookmarksView extends ViewPart implements ISetSelectionTarget {
    private TreeViewer bookmarksTree;
    private MenuManager menuMgr;
    private BookmarksLabelProvider labelProvider;
    private BookmarksContentProvider bookmarksProvider;

    /**
     * The constructor.
     */
    public BookmarksView() {
        // nothing to do
    }

    @Override
    public void dispose() {
        menuMgr.dispose();
        labelProvider.dispose();
        bookmarksProvider.dispose();
        bookmarksTree = null;
        super.dispose();
    }

    private void initPopup() {
        menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener(){
            public void menuAboutToShow( IMenuManager manager ) {
                fillContextMenu(manager);
            }
        });
        Menu m = menuMgr.createContextMenu(bookmarksTree.getTree());
        bookmarksTree.getTree().setMenu(m);
        getViewSite().registerContextMenu(menuMgr, bookmarksTree);
    }

    private void fillContextMenu( IMenuManager mm ) {
        mm.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    @Override
    public void createPartControl( Composite parent ) {
        bookmarksTree = new TreeViewer(parent);
        bookmarksProvider = new BookmarksContentProvider();
        bookmarksTree.setContentProvider(bookmarksProvider);
        getSite().getWorkbenchWindow().getPartService().addPartListener(bookmarksProvider);
        labelProvider = new BookmarksLabelProvider();
        bookmarksTree.setLabelProvider(labelProvider);
        bookmarksTree.setSorter(new ViewerSorter());
        bookmarksTree.setInput(bookmarksProvider);
        bookmarksTree.setAutoExpandLevel(2);
        bookmarksTree.addDoubleClickListener(new BookmarkAction());
        bookmarksTree.addDoubleClickListener(new IDoubleClickListener(){

            public void doubleClick( DoubleClickEvent event ) {
                final Object obj = ((IStructuredSelection) bookmarksTree.getSelection())
                        .getFirstElement();
                if (!(obj instanceof Bookmark)) {
                    Display.getCurrent().asyncExec(new Runnable(){

                        public void run() {
                            bookmarksTree.setExpandedState(obj, !bookmarksTree
                                    .getExpandedState(obj));
                        }
                    });
                    return;
                }
            }

        });
        initPopup();

        ApplicationGIS.getToolManager().contributeGlobalActions(this, getViewSite().getActionBars());
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        bookmarksTree.getControl().setFocus();
    }

    /**
     * Refresh the view
     */
    public void refresh() {
        ISelection selection = bookmarksTree.getSelection();
        bookmarksTree.refresh();
        if(selection.isEmpty()){
            bookmarksTree.setSelection(new StructuredSelection(bookmarksProvider.getCurrentMap()));
        }else {
            bookmarksTree.setSelection(selection);
        }
    }

    /*
     * @see org.eclipse.ui.part.ISetSelectionTarget#selectReveal(org.eclipse.jface.viewers.ISelection)
     */
    public void selectReveal( ISelection selection ) {
        bookmarksTree.setSelection(selection);
    }

}
