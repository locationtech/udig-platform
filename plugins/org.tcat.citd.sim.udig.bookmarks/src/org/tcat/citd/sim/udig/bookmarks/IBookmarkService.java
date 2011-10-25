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
package org.tcat.citd.sim.udig.bookmarks;

import java.util.Collection;

import net.refractions.udig.project.IMap;

import org.eclipse.emf.common.util.URI;
import org.tcat.citd.sim.udig.bookmarks.internal.MapReference;

/**
 * This interface provides a bookmark repository and associated management functions.
 * <p>
 * </p>
 * 
 * @author paul.pfeiffer
 * @version 1.3.0
 */
public interface IBookmarkService {

    /**
     * Used to load a bookmark into the list; does not trigger a
     * save to preferences.
     * @param bookmark
     */
    public void load( Bookmark bookmark ); 
    /**
     * Add the given bookmark.
     * 
     * @param bookmark
     */
    public void addBookmark( Bookmark bookmark ); 

    /**
     * Empties the list of bookmarks
     */
    public void empty(); 

    /**
     * Returns whether the list is empty
     * 
     * @return whether this list is empty
     */
    public boolean isEmpty();

    /**
     * Returns the list of projects as an array of objects
     * 
     * @return array of IProject objects
     */
    public Collection<URI> getProjects(); 

    /**
     * Returns the list of maps which are contained in the specified project
     * 
     * @param project The project for which the maps will be returned
     * @return array of MapReference objects
     */
    public Collection<MapReference> getMaps( URI project ); 

    /**
     * Return the list of bookmarks associated with the specified map
     * 
     * @param map The map for which the bookmarks will be returned
     * @return A vector of Bookmark objects
     */
    public Collection<Bookmark> getBookmarks( MapReference map );
    
    /**
     * Returns a list of all bookmarks
     * 
     * @return A collection of bookmarks
     */
    public Collection<IBookmark> getBookmarks();

    /**
     * Get the name of this bookmark manager for display It's just a static string for now
     * 
     * @return the name
     */
    public String getName(); 

    /**
     * Remove the given bookmark.
     * 
     * @param bookmark
     */
    public void removeBookmark( Bookmark bookmark ); 

    /**
     * Remove all of the bookmarks in the given list.
     * 
     * @param elements
     */
    public void removeBookmarks( Collection elements ); 

    /**
     * Remove the map and all it's associated bookmarks
     * 
     * @param map
     */
    public void removeMap( MapReference map ); 

    /**
     * Remove all of the maps in the given list and their associated bookmarks.
     * 
     * @param elements
     */
    public void removeMaps( Collection elements ); 
    
    /**
     * Remove the project and all it's associated maps and bookmarks
     * 
     * @param project
     */
    public void removeProject( URI project ); 

    /**
     * Remove all of the projects in the given list and their associated maps and bookmarks.
     * 
     * @param elements
     */
    public void removeProjects( Collection elements ); 

    /**
     * @param map
     * @return the MapReference singleton for the given IMap
     */
    public MapReference getMapReference( IMap map ); 

}
