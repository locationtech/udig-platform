/**
 * 
 */
package net.refractions.udig.internal.boundary;

import net.refractions.udig.boundary.IBoundaryStrategy;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Returns an empty ReferencedEnvelope so that zoom to extent goes to all
 * @author pfeiffp
 *
 */
public class BoundaryStrategyAll extends IBoundaryStrategy {

	private static String name = "All";
	
	@Override
	public ReferencedEnvelope getExtent() {
		return null;
	}

	@Override
	public Geometry getGeometry() {
		return null;
	}

	@Override
	public CoordinateReferenceSystem getCrs() {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

}
