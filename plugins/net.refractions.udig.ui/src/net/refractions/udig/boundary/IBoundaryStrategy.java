package net.refractions.udig.boundary;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Defines the changing functionality of the Boundary service.
 * 
 * @author pfeiffp
 */
public abstract class IBoundaryStrategy {

    /**
     * Returns the extent Should return an empty envelope for an "All" extent
     * 
     * @return ReferencedEnvelope
     */
    public abstract ReferencedEnvelope getExtent();

    /**
     * Returns a geometry of the current boundary selected
     * 
     * @return Geometry
     */
    public abstract Geometry getBoundary();

    /**
     * Returns the CRS of the current Boundary selected
     * 
     * @return
     */
    public abstract CoordinateReferenceSystem getCrs();

    /**
     * Returns the name of the Boundary strategy. This is used when adding to the combo to select
     * from.
     * 
     * @return String
     */
    public abstract String getName();

}
