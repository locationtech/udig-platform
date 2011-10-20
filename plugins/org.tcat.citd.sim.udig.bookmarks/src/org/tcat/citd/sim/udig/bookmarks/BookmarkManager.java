/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2006, Refractions Research Inc.
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
package org.tcat.citd.sim.udig.bookmarks;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.Project;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.emf.common.util.URI;
import org.tcat.citd.sim.udig.bookmarks.internal.MapReference;
import org.tcat.citd.sim.udig.bookmarks.internal.Messages;

/**
 * Bookmark repository and associated management functions.
 * 
 * @author cole.markham
 * @version 1.3.0
 */
public class BookmarkManager {
    /*private HashMap<URI, Vector<MapReference>> projectsHash;
    private HashMap<URI, Vector<Bookmark>> mapsHash;
    private HashMap<URI, MapReference> mapReferences;
    private int count = 0;*/
    
    /**
     * 
     */
    public BookmarkManager() {
        /*projectsHash = new HashMap<URI, Vector<MapReference>>();
        mapsHash = new HashMap<URI, Vector<Bookmark>>();
        mapReferences = new HashMap<URI, MapReference>();*/
    }

    /**
     * Add the given bookmark.
     * 
     * @param bookmark
     */
    public void addBookmark( Bookmark bookmark ) {
        IBookmarkService bookmarkService = BookmarksPlugin.getBookmarkService();

        bookmarkService.addBookmark(bookmark);
    }

    /**
     * Empties the list of bookmarks
     */
    public void empty() {

        IBookmarkService bookmarkService = BookmarksPlugin.getBookmarkService();

        bookmarkService.empty();
    }

    /**
     * Returns whether the list is empty
     * 
     * @return whether this list is empty
     */
    public boolean isEmpty() {
        IBookmarkService bookmarkService = BookmarksPlugin.getBookmarkService();

        return bookmarkService.isEmpty();
    }

    /**
     * Returns the list of projects as an array of objects
     * 
     * @return array of IProject objects
     */
    public Collection<URI> getProjects() {
        IBookmarkService bookmarkService = BookmarksPlugin.getBookmarkService();

        return bookmarkService.getProjects();
    }

    /**
     * Returns the list of maps which are contained in the specified project
     * 
     * @param project The project for which the maps will be returned
     * @return array of MapReference objects
     */
    public Collection<MapReference> getMaps( URI project ) {
        IBookmarkService bookmarkService = BookmarksPlugin.getBookmarkService();

        return bookmarkService.getMaps(project);
    }

    /**
     * Return the list of bookmarks associated with the specified map
     * 
     * @param map The map for which the bookmarks will be returned
     * @return A vector of Bookmark objects
     */
    public Collection<Bookmark> getBookmarks( MapReference map ) {
        IBookmarkService bookmarkService = BookmarksPlugin.getBookmarkService();

        return bookmarkService.getBookmarks(map);
    }

    /**
     * Get the name of this bookmark manager for display It's just a static string for now
     * 
     * @return the name
     */
    public String getName() {
        IBookmarkService bookmarkService = BookmarksPlugin.getBookmarkService();

        return bookmarkService.getName();
    }

    @Override
    public String toString() {
        IBookmarkService bookmarkService = BookmarksPlugin.getBookmarkService();

        return bookmarkService.toString();
    }

    /**
     * Remove the given bookmark.
     * 
     * @param bookmark
     */
    public void removeBookmark( Bookmark bookmark ) {
        IBookmarkService bookmarkService = BookmarksPlugin.getBookmarkService();

        bookmarkService.removeBookmark(bookmark);
    }

    /**
     * Remove all of the bookmarks in the given list.
     * 
     * @param elements
     */
    public void removeBookmarks( Collection elements ) {
        IBookmarkService bookmarkService = BookmarksPlugin.getBookmarkService();

        bookmarkService.removeBookmarks(elements);
    }

    /**
     * Remove the map and all it's associated bookmarks
     * 
     * @param map
     */
    public void removeMap( MapReference map ) {
        IBookmarkService bookmarkService = BookmarksPlugin.getBookmarkService();

        bookmarkService.removeMap(map);
    }

    /**
     * Remove all of the maps in the given list and their associated bookmarks.
     * 
     * @param elements
     */
    public void removeMaps( Collection elements ) {
        IBookmarkService bookmarkService = BookmarksPlugin.getBookmarkService();

        bookmarkService.removeMaps(elements);
    }

    /**
     * Remove the project and all it's associated maps and bookmarks
     * 
     * @param project
     */
    public void removeProject( URI project ) {
        IBookmarkService bookmarkService = BookmarksPlugin.getBookmarkService();

        bookmarkService.removeProject(project);
    }

    /**
     * Remove all of the projects in the given list and their associated maps and bookmarks.
     * 
     * @param elements
     */
    public void removeProjects( Collection elements ) {
        IBookmarkService bookmarkService = BookmarksPlugin.getBookmarkService();

        bookmarkService.removeProjects(elements);
    }

    /**
     * @param map
     * @return the MapReference singleton for the given IMap
     */
    public MapReference getMapReference( IMap map ) {
        IBookmarkService bookmarkService = BookmarksPlugin.getBookmarkService();

        return bookmarkService.getMapReference(map);
    }
}
