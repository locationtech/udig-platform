package eu.udig.catalog.jgrass.core;

import com.vividsolutions.jts.geom.Coordinate;

public class JGTtmsProperties {

    /**
     * Possible schemas
     */
    public static enum TILESCHEMA {
        tms, google
    }

    public String HOST_NAME;
    public String PROTOCOL = "http"; //$NON-NLS-1$
    public int ZOOM_MIN = 0;
    public int ZOOM_MAX = 18;

    public String tilePart;
    public boolean isFile = true;
    public Coordinate centerPoint = null;
    public TILESCHEMA type = TILESCHEMA.google;

}
