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
public class BoundaryStrategyAll implements IBoundaryStrategy {

	private static String name = "All";
	
	@Override
	public ReferencedEnvelope getExtent() {
		return null;
	}

	@Override
	public Geometry getBoundary() {
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

	@Override
	public Boolean enableTool(String toolId) {
		if (toolId.equalsIgnoreCase("Search")) {
			return false;
		}
		return true;
	}

}
