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
package org.tcat.citd.sim.udig.bookmarks.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.Project;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.osgi.service.prefs.BackingStoreException;
import org.tcat.citd.sim.udig.bookmarks.Bookmark;
import org.tcat.citd.sim.udig.bookmarks.BookmarksPlugin;
import org.tcat.citd.sim.udig.bookmarks.IBookmark;
import org.tcat.citd.sim.udig.bookmarks.IBookmarkService;

/**
 * This class provides a bookmark repository and associated management functions.
 * <p>
 * </p>
 * 
 * @author paul.pfeiffer
 * @version 1.3.0
 */
public class BookmarkServiceImpl implements IBookmarkService {
    private HashMap<URI, Vector<MapReference>> projectsHash;
    private HashMap<URI, Vector<Bookmark>> mapsHash;
    private HashMap<URI, MapReference> mapReferences;
    private int count = 0;

    /**
     * 
     */
    public BookmarkServiceImpl() {
        projectsHash = new HashMap<URI, Vector<MapReference>>();
        mapsHash = new HashMap<URI, Vector<Bookmark>>();
        mapReferences = new HashMap<URI, MapReference>();
    }
    
    @Override
    public void addBookmark( Bookmark bookmark ) {
        load( bookmark );
        try {
            BookmarksPlugin.getDefault().storeToPreferences();
        } catch (BackingStoreException e) {
            ILog log = BookmarksPlugin.getDefault().getLog();
            IStatus status = new Status( IStatus.WARNING,BookmarksPlugin.ID,"Unable to save to BookmarksPlugin");
            log.log(status);
        }
    }
    
    @Override
    public void load( Bookmark bookmark ) {
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

    @Override
    public void empty() {
        projectsHash.clear();
        mapsHash.clear();
    }

    @Override
    public boolean isEmpty() {
        boolean isEmpty = mapsHash.isEmpty();
        return isEmpty;
    }

    @Override
    public Collection<URI> getProjects() {
        Vector<URI> projects = new Vector<URI>(this.projectsHash.keySet());
        return projects;
    }

    @Override
    public Collection<MapReference> getMaps( URI project ) {
        Vector<MapReference> maps = projectsHash.get(project);
        return maps;
    }

    @Override
    public Collection<Bookmark> getBookmarks( MapReference map ) {
        if (!mapsHash.containsKey(map.getMapID())) {
            mapsHash.put(map.getMapID(), new Vector<Bookmark>());
        }
        Vector<Bookmark> bookmarks = mapsHash.get(map.getMapID());
        return bookmarks;
    }

    @Override
    public Collection<IBookmark> getBookmarks() {
        Collection<IBookmark> bookmarks = new Vector<IBookmark>();
        for (URI project : getProjects()) {
            for (MapReference map : getMaps(project)) {
                for (IBookmark  bookmark : getBookmarks(map)) {
                    bookmarks.add(bookmark);
                }
            }
        }
        return bookmarks;
    }

    @Override
    public String getName() {
        return Messages.BookmarkManager_name_bookmarkmanager;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
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

    @Override
    public void removeBookmarks( Collection elements ) {
        for( Object element : elements ) {
            if (element instanceof Bookmark) {
                Bookmark bmark = (Bookmark) element;
                this.removeBookmark(bmark);
            }
        }
    }

    @Override
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

    @Override
    public void removeMaps( Collection elements ) {
        for( Object element : elements ) {
            if (element instanceof MapReference) {
                MapReference map = (MapReference) element;
                this.removeMap(map);
            }
        }
    }

    @Override
    public void removeProject( URI project ) {
        Vector<MapReference> maps = projectsHash.get(project);
        projectsHash.remove(project);
        for( MapReference map : maps ) {
            maps.remove(map);
        }
    }

    @Override
    public void removeProjects( Collection elements ) {
        for( Object element : elements ) {
            if (element instanceof URI) {
                URI project = (URI) element;
                this.removeProject(project);
            }
        }
    }

    @Override
    public MapReference getMapReference( IMap map ) {
        MapReference ref = null;
        if (!mapReferences.containsKey(map.getID())) {
            // HACK: fix this when IProject has a getID() method
            Project project = (Project) map.getProject();
            if (project != null) {
                URI projectURI = project.eResource().getURI();
                ref = new MapReference(map.getID(), projectURI, map.getName());
                mapReferences.put(map.getID(), ref);
            }
        } else {
            ref = mapReferences.get(map.getID());
        }
        return ref;
    }

}
