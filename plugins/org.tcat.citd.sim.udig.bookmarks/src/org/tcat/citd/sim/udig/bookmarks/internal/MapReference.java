package org.tcat.citd.sim.udig.bookmarks.internal;

import org.eclipse.emf.common.util.URI;

/**
 * Container for a reference to a map
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
