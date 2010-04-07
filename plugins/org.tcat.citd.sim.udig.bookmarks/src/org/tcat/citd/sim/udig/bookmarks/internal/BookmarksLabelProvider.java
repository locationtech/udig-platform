package org.tcat.citd.sim.udig.bookmarks.internal;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.tcat.citd.sim.udig.bookmarks.Bookmark;
import org.tcat.citd.sim.udig.bookmarks.BookmarkManager;
import org.tcat.citd.sim.udig.bookmarks.BookmarksPlugin;

/**
 * Provide labels with images to the bookmarks view
 * <p>
 * </p>
 * 
 * @author cole.markham
 * @since 1.0.0
 */
public class BookmarksLabelProvider extends LabelProvider {
    private HashMap<Class, Image> table;

    /**
     * Default constructor
     */
    public BookmarksLabelProvider() {
        try {
            table = new HashMap<Class, Image>(4);
            Image image = BookmarksPlugin.getDefault().getImageDescriptor(
                    "icons/obj16/bookmark_obj.gif").createImage(); //$NON-NLS-1$
            table.put(Bookmark.class, image);
            image = BookmarksPlugin.getDefault()
                    .getImageDescriptor("icons/obj16/fldr_obj.gif").createImage(); //$NON-NLS-1$
            table.put(MapReference.class, image);
            image = BookmarksPlugin.getDefault()
                    .getImageDescriptor("icons/obj16/fldr_obj.gif").createImage(); //$NON-NLS-1$
            table.put(ProjectWrapper.class, image);
            image = BookmarksPlugin.getDefault().getImageDescriptor(
                    "icons/obj16/bookmarkmanager_obj.gif").createImage(); //$NON-NLS-1$
            table.put(BookmarkManager.class, image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        Collection<Image> images = table.values();
        table.clear();
        for( Image img : images ) {
            img.dispose();
        }
    }

    @Override
    public String getText( Object obj ) {
        String name;
        if (obj instanceof BookmarkManager) {
            BookmarkManager bManager = (BookmarkManager) obj;
            name = bManager.getName();
        } else if (obj instanceof Bookmark) {
            Bookmark bookmark = (Bookmark) obj;
            name = bookmark.getName();
        } else if (obj instanceof ProjectWrapper) {
            ProjectWrapper project = (ProjectWrapper) obj;
            name = project.getName();
        } else if (obj instanceof MapReference) {
            MapReference map = (MapReference) obj;
            name = map.getName();
        } else {
            name = obj.toString();
        }
        return name;
    }

    @Override
    public Image getImage( Object obj ) {
        Class theClass = obj.getClass();
        Image image = table.get(theClass);
        return image;
    }

}