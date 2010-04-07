package org.tcat.citd.sim.udig.bookmarks;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.Project;

import org.eclipse.emf.common.util.URI;
import org.tcat.citd.sim.udig.bookmarks.internal.MapReference;
import org.tcat.citd.sim.udig.bookmarks.internal.Messages;

/**
 * This class provides a bookmark repository and associated management functions.
 * <p>
 * </p>
 * 
 * @author cole.markham
 * @since 1.0.0
 */
public class BookmarkManager {
    private HashMap<URI, Vector<MapReference>> projectsHash;
    private HashMap<URI, Vector<Bookmark>> mapsHash;
    private HashMap<URI, MapReference> mapReferences;
    private int count = 0;

    /**
     * 
     */
    public BookmarkManager() {
        projectsHash = new HashMap<URI, Vector<MapReference>>();
        mapsHash = new HashMap<URI, Vector<Bookmark>>();
        mapReferences = new HashMap<URI, MapReference>();
    }

    /**
     * Add the given bookmark.
     * 
     * @param bookmark
     */
    public void addBookmark( Bookmark bookmark ) {
        if (bookmark.getName() == null || bookmark.getName() == "") { //$NON-NLS-1$
            bookmark.setName(Messages.BookmarkManager_bookmarkdefaultname + (++count));
        }
        MapReference map = bookmark.getMap();
        URI project = map.getProjectID();
        if (!projectsHash.containsKey(project)) {
            Vector<MapReference> projectMaps = new Vector<MapReference>();
            projectMaps.add(map);
            projectsHash.put(project, projectMaps);
        } else {
            Vector<MapReference> projectMaps = projectsHash.get(project);
            if (!projectMaps.contains(map)) {
                projectMaps.add(map);
            }
        }
        if (!mapsHash.containsKey(map.getMapID())) {
            Vector<Bookmark> bookmarks = new Vector<Bookmark>();
            bookmarks.add(bookmark);
            mapsHash.put(map.getMapID(), bookmarks);
        } else {
            Vector<Bookmark> bmarks = mapsHash.get(map.getMapID());
            if (!bmarks.contains(bookmark)) {
                bmarks.add(bookmark);
            }
        }
    }

    /**
     * Empties the list of bookmarks
     */
    public void empty() {
        projectsHash.clear();
        mapsHash.clear();
    }

    /**
     * Returns whether the list is empty
     * 
     * @return whether this list is empty
     */
    public boolean isEmpty() {
        boolean isEmpty = mapsHash.isEmpty();
        return isEmpty;
    }

    /**
     * Returns the list of projects as an array of objects
     * 
     * @return array of IProject objects
     */
    public Collection<URI> getProjects() {
        Vector<URI> projects = new Vector<URI>(this.projectsHash.keySet());
        return projects;
    }

    /**
     * Returns the list of maps which are contained in the specified project
     * 
     * @param project The project for which the maps will be returned
     * @return array of MapReference objects
     */
    public Collection<MapReference> getMaps( URI project ) {
        Vector<MapReference> maps = projectsHash.get(project);
        return maps;
    }

    /**
     * Return the list of bookmarks associated with the specified map
     * 
     * @param map The map for which the bookmarks will be returned
     * @return A vector of Bookmark objects
     */
    public Collection<Bookmark> getBookmarks( MapReference map ) {
        if (!mapsHash.containsKey(map.getMapID())) {
            mapsHash.put(map.getMapID(), new Vector<Bookmark>());
        }
        Vector<Bookmark> bookmarks = mapsHash.get(map.getMapID());
        return bookmarks;
    }

    /**
     * Get the name of this bookmark manager for display It's just a static string for now
     * 
     * @return the name
     */
    public String getName() {
        return Messages.BookmarkManager_name_bookmarkmanager;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    /**
     * Remove the given bookmark.
     * 
     * @param bookmark
     */
    public void removeBookmark( Bookmark bookmark ) {
        MapReference map = bookmark.getMap();
        mapsHash.get(map.getMapID()).remove(bookmark);
        if (mapsHash.get(map.getMapID()).isEmpty()) {
            URI projectID = map.getProjectID();
            mapsHash.remove(map.getMapID());
            projectsHash.get(projectID).remove(map);
            if (projectsHash.get(projectID).isEmpty()) {
                projectsHash.remove(projectID);
            }
        }
    }

    /**
     * Remove all of the bookmarks in the given list.
     * 
     * @param elements
     */
    public void removeBookmarks( Collection elements ) {
        for( Object element : elements ) {
            if (element instanceof Bookmark) {
                Bookmark bmark = (Bookmark) element;
                this.removeBookmark(bmark);
            }
        }
    }

    /**
     * Remove the map and all it's associated bookmarks
     * 
     * @param map
     */
    public void removeMap( MapReference map ) {
        mapsHash.remove(map.getMapID());
        URI projectID = map.getProjectID();
        Vector<MapReference> maps = projectsHash.get(projectID);
        if (maps != null && maps.size() > 0) {
            maps.remove(map);
            if (maps.isEmpty()) {
                projectsHash.remove(projectID);
            }
        }
    }

    /**
     * Remove all of the maps in the given list and their associated bookmarks.
     * 
     * @param elements
     */
    public void removeMaps( Collection elements ) {
        for( Object element : elements ) {
            if (element instanceof MapReference) {
                MapReference map = (MapReference) element;
                this.removeMap(map);
            }
        }
    }

    /**
     * Remove the project and all it's associated maps and bookmarks
     * 
     * @param project
     */
    public void removeProject( URI project ) {
        Vector<MapReference> maps = projectsHash.get(project);
        projectsHash.remove(project);
        for( MapReference map : maps ) {
            maps.remove(map);
        }
    }

    /**
     * Remove all of the projects in the given list and their associated maps and bookmarks.
     * 
     * @param elements
     */
    public void removeProjects( Collection elements ) {
        for( Object element : elements ) {
            if (element instanceof URI) {
                URI project = (URI) element;
                this.removeProject(project);
            }
        }
    }

    /**
     * @param map
     * @return the MapReference singleton for the given IMap
     */
    public MapReference getMapReference( IMap map ) {
        MapReference ref = null;
        if (!mapReferences.containsKey(map.getID())) {
            // HACK: fix this when IProject has a getID() method
            Project project = (Project) map.getProject();
            URI projectURI = project.eResource().getURI();
            ref = new MapReference(map.getID(), projectURI, map.getName());
            mapReferences.put(map.getID(), ref);
        } else {
            ref = mapReferences.get(map.getID());
        }
        return ref;
    }
}
