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
package org.tcat.citd.sim.udig.bookmarks.internal;

import org.eclipse.emf.common.util.URI;

/**
 * Reference to a map; can be used to lookup a Map at runtime.
 * <p>
 * </p>
 * 
 * @author cole.markham
 * @since 1.0.0
 */
public class MapReference {
    private URI mapID;
    private URI projectID;
    private String name;
    /**
     * @param mapid The URI of the map
     * @param projectid The URI of the project
     * @param name The name of the map
     */
    public MapReference( URI mapid, URI projectid, String name ) {
        mapID = mapid;
        projectID = projectid;
        this.name = name;
    }
    /**
     * @return Returns the mapID.
     */
    public URI getMapID() {
        return mapID;
    }
    /**
     * @param mapID The mapID to set.
     */
    public void setMapID( URI mapID ) {
        this.mapID = mapID;
    }
    /**
     * @return Returns the projectID.
     */
    public URI getProjectID() {
        return projectID;
    }
    /**
     * @param projectID The projectID to set.
     */
    public void setProjectID( URI projectID ) {
        this.projectID = projectID;
    }

    @Override
    public int hashCode() {
        int code;
        code = mapID.hashCode();
        return code;
    }
    /**
     * @return The name of the map
     */
    public String getName() {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public void setName( String name ) {
        this.name = name;
    }

}