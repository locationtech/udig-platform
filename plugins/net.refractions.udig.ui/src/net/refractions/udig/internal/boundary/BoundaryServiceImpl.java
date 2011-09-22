/**
 * 
 */
package net.refractions.udig.internal.boundary;

import net.refractions.udig.boundary.IBoundaryService;
import net.refractions.udig.boundary.IBoundaryStrategy;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

/**
 * This is the default implementation of BoundaryService; it delegates to the
 * internal strategy object.
 * 
 * @author pfeiffp
 */
public class BoundaryServiceImpl implements IBoundaryService {

	private IBoundaryStrategy boundaryStrategy;
	
	public BoundaryServiceImpl() {
		this.boundaryStrategy = new BoundaryStrategyAll();
	}
	
	@Override
	public ReferencedEnvelope getExtent() {
		return this.boundaryStrategy.getExtent();
	}

	@Override
	public void setStrategy(IBoundaryStrategy boundaryStrategy) {
		this.boundaryStrategy = boundaryStrategy;

	}
	
	@Override
	public Geometry getBoundary() {
		return this.boundaryStrategy.getBoundary();
	}

	@Override
	public CoordinateReferenceSystem getCrs() {
		return this.boundaryStrategy.getCrs();
	}

	@Override
	public IBoundaryStrategy currentStrategy() {
		return this.boundaryStrategy;
	}

}
