/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2006, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.bookmarks.internal.ui;

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
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.part.ViewPart;
import org.locationtech.udig.bookmarks.Bookmark;
import org.locationtech.udig.bookmarks.internal.BookmarksContentProvider;
import org.locationtech.udig.bookmarks.internal.BookmarksLabelProvider;
import org.locationtech.udig.bookmarks.internal.actions.BookmarkAction;

/**
 * This is the view that displays the <code>Bookmark</code>s. The content provider connects to the
 * <code>BookmarkManager</code> which is the model.
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
        menuMgr.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                fillContextMenu(manager);
            }
        });
        Menu m = menuMgr.createContextMenu(bookmarksTree.getTree());
        bookmarksTree.getTree().setMenu(m);
        getViewSite().registerContextMenu(menuMgr, bookmarksTree);
    }

    private void fillContextMenu(IMenuManager mm) {
        mm.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(Composite parent) {
        bookmarksTree = new TreeViewer(parent);
        bookmarksProvider = new BookmarksContentProvider();
        bookmarksTree.setContentProvider(bookmarksProvider);
        getSite().getWorkbenchWindow().getPartService().addPartListener(bookmarksProvider);
        labelProvider = new BookmarksLabelProvider();
        bookmarksTree.setLabelProvider(labelProvider);
        bookmarksTree.setComparator(new ViewerComparator());
        bookmarksTree.setInput(bookmarksProvider);
        bookmarksTree.setAutoExpandLevel(2);
        bookmarksTree.addDoubleClickListener(new BookmarkAction());
        bookmarksTree.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick(DoubleClickEvent event) {
                final Object obj = ((IStructuredSelection) bookmarksTree.getSelection())
                        .getFirstElement();
                if (!(obj instanceof Bookmark)) {
                    Display.getCurrent().asyncExec(new Runnable() {

                        @Override
                        public void run() {
                            bookmarksTree.setExpandedState(obj,
                                    !bookmarksTree.getExpandedState(obj));
                        }
                    });
                    return;
                }
            }

        });
        initPopup();
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
        if (selection.isEmpty()) {
            bookmarksTree.setSelection(new StructuredSelection(bookmarksProvider.getCurrentMap()));
        } else {
            bookmarksTree.setSelection(selection);
        }
    }

    /**
     * @see org.eclipse.ui.part.ISetSelectionTarget#selectReveal(org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectReveal(ISelection selection) {
        bookmarksTree.setSelection(selection);
    }

}
