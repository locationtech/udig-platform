/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2006, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.bookmarks.internal.actions;

import java.util.Collection;
import java.util.List;

import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.render.IViewportModel;
import org.locationtech.udig.project.ui.ApplicationGIS;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.udig.bookmarks.Bookmark;
import org.locationtech.udig.bookmarks.BookmarkCommandFactory;
import org.locationtech.udig.bookmarks.BookmarksPlugin;
import org.locationtech.udig.bookmarks.IBookmark;
import org.locationtech.udig.bookmarks.IBookmarkService;
import org.locationtech.udig.bookmarks.internal.MapReference;
import org.locationtech.udig.bookmarks.internal.MapWrapper;
import org.locationtech.udig.bookmarks.internal.Messages;
import org.locationtech.udig.bookmarks.internal.ui.BookmarksView;

import org.locationtech.jts.geom.Envelope;

/**
 * The action delegate the provides all of the actions for working with
 * bookmarks.
 * <p>
 * </p>
 * 
 * @author cole.markham
 * @since 1.0.0
 */
public class BookmarkAction extends Action implements IObjectActionDelegate,
        IViewActionDelegate, IDoubleClickListener {
    /**
     * id for action to remove a bookmark
     */
    public static final String REMOVE_BOOKMARK_ACTION_ID = "bookmarks.actions.removebookmarkaction"; //$NON-NLS-N$

    /**
     * id for action to remove a bookmark
     */
    public static final String REMOVE_MAP_ACTION_ID = "bookmarks.actions.removemapaction"; //$NON-NLS-1$

    /**
     * id for action to remove a bookmark
     */
    public static final String REMOVE_PROJECT_ACTION_ID = "bookmarks.actions.removeprojectaction"; //$NON-NLS-1$

    /**
     * id for action to remove all bookmarks
     */
    public static final String REMOVE_ALL_ACTION_ID = "bookmarks.actions.removeallbookmarksaction"; //$NON-NLS-1$

    /**
     * id for action to go to a bookmark
     */
    public static final String GOTO_BOOKMARK_ACTION_ID = "bookmarks.actions.gotobookmarkaction"; //$NON-NLS-1$

    /**
     * id for action to add a bookmark
     */
    public static final String ADD_BOOKMARK_ACTION_ID = "bookmarks.actions.addbookmarkaction"; //$NON-NLS-1$

    /**
     * id for action to rename a bookmark
     */
    public static final String RENAME_BOOKMARK_ACTION_ID = "bookmarks.actions.renamebookmarkaction"; //$NON-NLS-1$

    /**
     * id for action to save the bookmarks
     */
    public static final String SAVE_BOOKMARKS_ACTION_ID = "bookmarks.actions.savebookmarksaction"; //$NON-NLS-1$

    /**
     * id for action to restore the bookmarks
     */
    public static final String RESTORE_BOOKMARKS_ACTION_ID = "bookmarks.actions.restorebookmarksaction"; //$NON-NLS-1$

    private IViewPart view;
    private IStructuredSelection selection;
    private IBookmarkService bmManager;

    /**
     * Default Constructor
     */
    public BookmarkAction() {
        // nothing to do
    }

    // public void run() {
    // ViewportModel v =
    // (ViewportModel)(PlatformGIS.getActiveMap().getViewportModel());
    // IStructuredSelection selection =
    // (IStructuredSelection)this.view.viewer.getSelection();
    // Bookmark bookmark = (Bookmark)selection.getFirstElement();
    // v.setBounds(bookmark.getEnvelope());
    // }

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        if (targetPart != null && targetPart instanceof IViewPart) {
            view = (IViewPart) targetPart;
        }
    }

    public void run(IAction action) {
        try {
            if (REMOVE_ALL_ACTION_ID.equals(action.getId())) {
                if (MessageDialog
                        .openConfirm(
                                Display.getCurrent().getActiveShell(),
                                Messages.BookmarkAction_dialogtitle_removebookmarks,
                                Messages.BookmarkAction_dialogprompt_removeallbookmarks)) {
                    if (bmManager == null) {
                        bmManager = BookmarksPlugin.getBookmarkService();
                    }
                    bmManager.empty();
                    refreshView();
                }
            } else if (REMOVE_MAP_ACTION_ID.equals(action.getId())) {
                int size = selection.size();
                if (size > 0) {
                    if (size > 1) {
                        if (MessageDialog
                                .openConfirm(
                                        Display.getCurrent().getActiveShell(),
                                        Messages.BookmarkAction_dialogtitle_removebookmarks,
                                        Messages.BookmarkAction_dialogprompt_removemapbookmarks)) {
                            MapReference map = (MapReference) selection
                                    .getFirstElement();
                            if (bmManager == null) {
                                bmManager = BookmarksPlugin
                                        .getBookmarkService();
                            }
                            bmManager.removeMap(map);
                        }
                    } else {
                        if (MessageDialog
                                .openConfirm(
                                        Display.getCurrent().getActiveShell(),
                                        Messages.BookmarkAction_dialogtitle_removebookmarks,
                                        Messages.BookmarkAction_dialogprompt_removeselectedmaps)) {
                            if (bmManager == null) {
                                bmManager = BookmarksPlugin
                                        .getBookmarkService();
                            }
                            List wrappedMaps = selection.toList();
                            Collection maps = MapWrapper.unwrap(wrappedMaps);
                            bmManager.removeMaps(maps);
                        }
                    }
                    refreshView();
                }
                // }else if(REMOVE_PROJECT_ACTION_ID.equals(action.getId())){
                // int size = selection.size();
                // if(size > 0){
                // if(size > 1){
                // if( MessageDialog.openConfirm(
                // Display.getCurrent().getActiveShell(),
                // Messages..BOOKMARK_ACTION_DIALOGTITLE_REMOVEBOOKMARKS,
                // //$NON-NLS-1$
                // Messages..BOOKMARK_ACTION_DIALOGPROMPT_REMOVEPROJECTBOOKMARKS)
                // ){ //$NON-NLS-1$
                // ProjectWrapper wrapper =
                // (ProjectWrapper)selection.getFirstElement();
                // if(bmManager == null){
                // bmManager = BookmarksPlugin.getBookmarkService();
                // }
                // // bmManager.removeProject(wrapper.getProject());
                // }
                // }else {
                // if( MessageDialog.openConfirm(
                // Display.getCurrent().getActiveShell(),
                // Messages..BOOKMARK_ACTION_DIALOGTITLE_REMOVEBOOKMARKS,
                // //$NON-NLS-1$
                // Messages..BOOKMARK_ACTION_DIALOGPROMPT_REMOVESELECTEDPROJECTS)
                // ){ //$NON-NLS-1$
                // if(bmManager == null){
                // bmManager = BookmarksPlugin.getBookmarkService();
                // }
                // List wrappedProjects = selection.toList();
                // Collection<IProject> projects =
                // ProjectWrapper.unwrap(wrappedProjects);
                // bmManager.removeProjects(projects);
                // }
                // }
                // refreshView();
                // }
            } else if (REMOVE_BOOKMARK_ACTION_ID.equals(action.getId())) {
                int size = selection.size();
                if (size > 0) {
                    if (size > 1) {
                        if (MessageDialog
                                .openConfirm(
                                        Display.getCurrent().getActiveShell(),
                                        Messages.BookmarkAction_dialogtitle_removebookmarks,
                                        Messages.BookmarkAction_dialogprompt_removeselectedbookmarks)) {
                            List bookmarks = selection.toList();
                            if (bmManager == null) {
                                bmManager = BookmarksPlugin
                                        .getBookmarkService();
                            }
                            bmManager.removeBookmarks(bookmarks);
                        }
                    } else {
                        if (MessageDialog
                                .openConfirm(
                                        Display.getCurrent().getActiveShell(),
                                        Messages.BookmarkAction_dialogtitle_removebookmark,
                                        Messages.BookmarkAction_dialogprompt_removebookmark)) {
                            Bookmark bookmark = (Bookmark) selection
                                    .getFirstElement();
                            if (bmManager == null) {
                                bmManager = BookmarksPlugin
                                        .getBookmarkService();
                            }
                            bmManager.removeBookmark(bookmark);
                        }
                    }
                    refreshView();
                }
            } else if (GOTO_BOOKMARK_ACTION_ID.equals(action.getId())) {
                Bookmark bookmark = (Bookmark) selection.getFirstElement();
                gotoBookmark(bookmark);
            } else if (ADD_BOOKMARK_ACTION_ID.equals(action.getId())) {
                IMap map = ApplicationGIS.getActiveMap();
                if (map != ApplicationGIS.NO_MAP) {
                    IViewportModel v = map.getViewportModel();
                    Envelope env = v.getBounds();
                    ReferencedEnvelope bounds;
                    if (env instanceof ReferencedEnvelope) {
                        bounds = (ReferencedEnvelope) env;
                    } else {
                        bounds = new ReferencedEnvelope(env, v.getCRS());
                    }
                    MapReference ref = bmManager.getMapReference(map);
                    Bookmark bookmark = new Bookmark(bounds, ref, null);
                    InputDialog dialog = new InputDialog(
                            Display.getCurrent().getActiveShell(),
                            Messages.BookmarkAction_dialogtitle_bookmarklocation,
                            Messages.BookmarkAction_dialogprompt_enterbookmarkname,
                            bookmark.getName(), null);
                    dialog.open();
                    if (dialog.getReturnCode() == Window.OK) {
                        String name = dialog.getValue();
                        bookmark.setName(name);
                        bmManager = BookmarksPlugin.getBookmarkService();
                        bmManager.addBookmark(bookmark);
                        refreshView();
                    }
                    ((BookmarksView) view)
                            .selectReveal(new StructuredSelection(bookmark));
                }
            } else if (RENAME_BOOKMARK_ACTION_ID.equals(action.getId())) {
                IBookmark bookmark = (IBookmark) selection.getFirstElement();
                InputDialog dialog = new InputDialog(Display.getCurrent()
                        .getActiveShell(),
                        Messages.BookmarkAction_dialogtitle_renamebookmark,
                        Messages.BookmarkAction_dialogprompt_enterbookmarkname,
                        bookmark.getName(), null);
                dialog.open();
                if (dialog.getReturnCode() == Window.OK) {
                    String name = dialog.getValue();
                    bookmark.setName(name);
                    refreshView();
                }
            } else if (SAVE_BOOKMARKS_ACTION_ID.equals(action.getId())) {
                BookmarksPlugin.getDefault().storeToPreferences();
            } else if (RESTORE_BOOKMARKS_ACTION_ID.equals(action.getId())) {
                BookmarksPlugin.getDefault().restoreFromPreferences();
                refreshView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshView() {
        if (view != null && view instanceof BookmarksView) {
            BookmarksView bView = ((BookmarksView) view);
            bView.refresh();
        }
    }

    /**
     * Go to the given bookmark
     * 
     * @param bookmark
     *            The bookmark to go to
     */
    private void gotoBookmark(Bookmark bookmark) {
        BookmarkCommandFactory factory = BookmarkCommandFactory.getInstance();
        MapCommand cmd = factory.createGotoBookmarkCommand(bookmark);
        ApplicationGIS.getActiveMap().sendCommandASync(cmd);
    }

    public void init(IViewPart viewPart) {
        if (viewPart != null) {
            this.view = viewPart;
        }
        if (bmManager == null) {
            bmManager = BookmarksPlugin.getBookmarkService();
        }
    }

    public void selectionChanged(IAction action, ISelection newSelection) {
        this.selection = (IStructuredSelection) newSelection;
    }

    public void doubleClick(DoubleClickEvent event) {
        final IStructuredSelection eventSelection = (IStructuredSelection) event
                .getSelection();
        if (eventSelection.size() > 0
                && eventSelection.getFirstElement() instanceof Bookmark) {
            Bookmark bookmark = (Bookmark) eventSelection.getFirstElement();
            gotoBookmark(bookmark);
        }
    }

}
