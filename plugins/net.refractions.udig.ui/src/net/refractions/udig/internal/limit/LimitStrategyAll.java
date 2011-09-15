/**
 * 
 */
package net.refractions.udig.internal.limit;

import net.refractions.udig.limit.ILimitStrategy;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Returns an empty ReferencedEnvelope so that zoom to extent goes to all
 * @author pfeiffp
 *
 */
public class LimitStrategyAll implements ILimitStrategy {

	private static String name = "All";
	
	@Override
	public ReferencedEnvelope getExtent() {
//		ReferencedEnvelope bbox = new ReferencedEnvelope();
//		return bbox;
		return null;
	}

	@Override
	public Geometry getLimit() {
		return null;
//		GeometryBuilder geometryBuilder = GeometryBuilder.create();
//		return geometryBuilder.safeCreateGeometry(Geometry.class, );
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
