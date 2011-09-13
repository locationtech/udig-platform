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

	private ILimitStrategy defaultStrategy;
	private ILimitStrategy currentStrategy;
	
	public LimitServiceImpl() {
		this.defaultStrategy = new LimitStrategyAll();
		this.currentStrategy = new LimitStrategyAll();
	}
	
	@Override
	public ReferencedEnvelope getExtent() {
		return this.currentStrategy.getExtent();
	}

	@Override
	public void setStrategy(ILimitStrategy limitStrategy) {
		this.currentStrategy = limitStrategy;

	}
	
	@Override
	public Geometry getLimit() {
		return this.currentStrategy.getLimit();
	}

	@Override
	public CoordinateReferenceSystem getCrs() {
		return this.currentStrategy.getCrs();
	}

	@Override
	public ILimitStrategy currentStrategy() {
		return this.currentStrategy;
	}

	@Override
	public ILimitStrategy defaultStrategy() {
		return this.defaultStrategy;
	}
}
