/**
 * 
 */
package net.refractions.udig.project.ui.internal.limit;

import net.refractions.udig.limit.ILimitStrategy;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Returns an empty ReferencedEnvelope so that zoom to extent goes to all
 * @author pfeiffp
 *
 */
public class LimitStrategyScreen implements ILimitStrategy {

	private static String name = "Screen";
	
	@Override
	public ReferencedEnvelope getExtent() {
		IMap currentMap = ApplicationGIS.getActiveMap();
		if (!currentMap.equals(ApplicationGIS.NO_MAP)) {
			System.out.println(currentMap.getViewportModel().getBounds());
			return currentMap.getViewportModel().getBounds();
		}
		return null;
	}

	@Override
	public Geometry getLimit() {
		return null;
		//return new Geometry(this.getExtent());
	}

	@Override
	public CoordinateReferenceSystem getCrs() {
		IMap currentMap = ApplicationGIS.getActiveMap();
		if (!currentMap.equals(ApplicationGIS.NO_MAP)) {
			System.out.println(currentMap.getViewportModel().getCRS());
			return currentMap.getViewportModel().getCRS();
		}
		return null;
	}

	@Override
	public String getName() {
		return name;
	}
	
	

}
