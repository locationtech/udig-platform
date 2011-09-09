/**
 * 
 */
package net.refractions.udig.internal.limit;

import net.refractions.udig.core.internal.GeometryBuilder;
import net.refractions.udig.limit.ILimitStrategy;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Returns an empty ReferencedEnvelope so that zoom to extent goes to all
 * @author pfeiffp
 *
 */
public class LimitStrategyAll implements ILimitStrategy {

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
	
	

}
