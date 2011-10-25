package org.tcat.citd.sim.udig.bookmarks.internal;

import java.util.Collection;
import java.util.HashMap;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.MapEditor;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.tcat.citd.sim.udig.bookmarks.Bookmark;
import org.tcat.citd.sim.udig.bookmarks.BookmarksPlugin;
import org.tcat.citd.sim.udig.bookmarks.IBookmarkService;
import org.tcat.citd.sim.udig.bookmarks.internal.ui.BookmarksView;

/**
 * The content provider class is responsible for providing objects to the view. It can wrap existing
 * objects in adapters or simply return objects as-is. These objects may be sensitive to the current
 * input of the view, or ignore it and always show the same content (like Task List, for example).
 * 
 * @author cole.markham
 * @since 1.0.0
 */
public class BookmarksContentProvider
        implements
            IStructuredContentProvider,
            ITreeContentProvider,
            IPartListener {
    private HashMap<Viewer, Object> viewers;

    private IWorkbenchPart currentPart;

    private IBookmarkService bManager;

    private MapReference currentMap;

    /**
     * Default constructor
     */
    public BookmarksContentProvider() {
        viewers = new HashMap<Viewer, Object>();
        bManager = BookmarksPlugin.getBookmarkService();
        currentPart = null;
        currentMap = null;
        
    }

    public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
        if (newInput == null)
            viewers.remove(viewer);
        else
            viewers.put(viewer, newInput);
    }

    /**
     * returns current mapping of registered viewers and input objects Usually this involves a Viewe
     * object
     * 
     * @return a map of the viewers to input objects
     */
    protected HashMap getViewers() {
        return viewers;
    }

    public void dispose() {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().removePartListener(
                this);
        viewers.clear();
        bManager = null;
    }

    /**
     * Get the root element -- a single IMap object corresonding to the current map
     * 
     * @return array of Objects
     */
    public Object[] getElements( Object parent ) {
        Object[] elements = new Object[1];
        if (currentMap == null) {
            elements[0] = Messages.BookmarksContentProvider_emptybookmarkslist;
        } else {
            elements[0] = currentMap;
        }
        return elements;
    }

    public Object[] getChildren( Object parentElement ) {
        Object[] children = null;
        if (parentElement instanceof MapReference) {
            MapReference map = (MapReference) parentElement;
            children = bManager.getBookmarks(map).toArray();
        }
        return children;
    }

    public Object getParent( Object element ) {
        Object parent = null;
        if (element instanceof Bookmark) {
            Bookmark bookmark = (Bookmark) element;
            parent = bookmark.getMap();
        }
        return parent;
    }

    public boolean hasChildren( Object element ) {
        boolean hasChildren = false;
        if (element instanceof MapReference) {
            hasChildren = true;
        }
        return hasChildren;
    }

    /**
     * refreshes the given viewer in the UI thread
     * 
     * @param asynch set to true for asynchronous refresh, false for synchronous
     * @param v viewer to refresh
     */
    public void refresh( final Viewer v, boolean asynch ) {
        if (v.getControl().isDisposed())
            return;
        Display display = v.getControl().getDisplay();
        if (display != null && !display.isDisposed()) {
            Runnable r = new Runnable(){
                public void run() {
                    if (v != null && !v.getControl().isDisposed()) {
                        v.refresh();
                        if (v instanceof TreeViewer) {
                            ((TreeViewer) v).expandToLevel(currentMap, 1);
                        }
                    }
                }
            };
            if (asynch)
                display.asyncExec(r);
            else
                PlatformGIS.syncInDisplayThread(r);
        }
    }

    /**
     * refreshes the given collection of viewers in the UI thread
     * 
     * @param c
     * @param asynch set to true for asynchronous refresh, false for synchronous
     */
    public void refresh( final Collection<Viewer> c, boolean asynch ) {
        for( Viewer v : c ) {
            refresh(v, asynch);
        }
    }

    /**
     * @return Returns the currentMap.
     */
    public MapReference getCurrentMap() {
        return currentMap;
    }

    /**
     * @param currentMap The currentMap to set.
     */
    public void setCurrentMap( MapReference currentMap ) {
        this.currentMap = currentMap;
    }

    public void partActivated( IWorkbenchPart part ) {
        if (part == currentPart)
            return;
        
        IMap map = null;
        if (part instanceof BookmarksView){
            //get the current active map
            map = ApplicationGIS.getActiveMap();
        }
        if (map == null && part instanceof IAdaptable){
            map = (IMap)((IAdaptable)part).getAdapter(Map.class);
        }
        
        if (map != null){
            currentPart = part;   
            MapReference ref = bManager.getMapReference(map);
            setCurrentMap(ref);
            refresh(viewers.keySet(), true);
        }
    }

    public void partBroughtToTop( IWorkbenchPart part ) {
        // nothing to do
    }

    public void partClosed( IWorkbenchPart part ) {
        if (part == this) {
            dispose();
            return;
        }
        if (part != currentPart)
            return;
        currentPart = null;
        if (part instanceof MapEditor) {
            setCurrentMap(null);
        }
        refresh(viewers.keySet(), true);
    }

    public void partDeactivated( IWorkbenchPart part ) {
        // nothing to do
    }

    public void partOpened( IWorkbenchPart part ) {
        // nothing to do
    }
}
