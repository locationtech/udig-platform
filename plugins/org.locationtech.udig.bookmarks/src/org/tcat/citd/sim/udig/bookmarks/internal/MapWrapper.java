/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2006, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.bookmarks.internal;

import java.util.Collection;
import java.util.Vector;

import org.locationtech.udig.project.IMap;

/**
 * This class provides a wrapper for displaying <code>IMap</code> objects as folders in the
 * <code>BookmarksView</code>.<BR>
 * <BR>
 * This gives the advantage of more easily displaying custom menus and icons.
 * <p>
 * </p>
 * 
 * @author cole.markham
 * @since 1.0.0
 */
public class MapWrapper {
    private IMap map;

    /**
     * Default constructor
     * 
     * @param map The map that this object will wrap.
     */
    public MapWrapper( IMap map ) {
        this.map = map;
    }

    /**
     * @return Returns the map.
     */
    public IMap getMap() {
        return map;
    }

    /**
     * @param map The map to set.
     */
    public void setMap( IMap map ) {
        this.map = map;
    }

    /**
     * Get the name for the map
     * 
     * @return the name
     */
    public String getName() {
        return map.getName();
    }

    @Override
    public String toString() {
        return this.getName();
    }

    /**
     * Get a wrapper for the project that contains this map
     * 
     * @return the new ProjectWrapper
     */
    public ProjectWrapper getProjectWrapper() {
        ProjectWrapper wrapper = new ProjectWrapper(map.getProject());
        return wrapper;
    }

    /**
     * Unwrap all of the maps in the given list
     * 
     * @param wrappedMaps
     * @return a Collection of IMap objects
     */
    public static Collection unwrap( Collection wrappedMaps ) {
        Vector<IMap> maps = new Vector<IMap>(wrappedMaps.size());
        for( Object element : wrappedMaps ) {
            if (element instanceof MapWrapper) {
                MapWrapper wrapper = (MapWrapper) element;
                maps.add(wrapper.getMap());
            }
        }
        return maps;
    }

    /**
     * Wrap the maps in the given list
     * 
     * @param maps
     * @return a Collection of MapWrapper objects
     */
    public static Collection<MapWrapper> wrap( Collection<IMap> maps ) {
        Vector<MapWrapper> wrapped = new Vector<MapWrapper>(maps.size());
        for( IMap map : maps ) {
            wrapped.add(new MapWrapper(map));
        }
        return wrapped;
    }
}
