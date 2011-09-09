/**
 * 
 */
package net.refractions.udig.internal.limit;

import net.refractions.udig.limit.ILimitService;
import net.refractions.udig.limit.ILimitStrategy;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * This is the default implemtnation of LimitService; it delegates to the
 * internal strategy object.
 * 
 * @author pfeiffp
 */
public class LimitServiceImpl implements ILimitService {

	private ILimitStrategy limitStrategy;
	
	public LimitServiceImpl() {
		this.limitStrategy = new LimitStrategyAll();
	}
	
	@Override
	public ReferencedEnvelope getExtent() {
		return this.limitStrategy.getExtent();
	}

	@Override
	public void setStrategy(ILimitStrategy limitStrategy) {
		this.limitStrategy = limitStrategy;

	}
	
	@Override
	public Geometry getLimit() {
		return this.limitStrategy.getLimit();
	}

	@Override
	public CoordinateReferenceSystem getCrs() {
		return this.limitStrategy.getCrs();
	}
}
