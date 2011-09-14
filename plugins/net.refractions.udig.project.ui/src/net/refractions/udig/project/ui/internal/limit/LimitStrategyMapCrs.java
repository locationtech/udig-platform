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
public class LimitStrategyMapCrs implements ILimitStrategy {

	private static String name = "Map CRS";
	
	@Override
	public ReferencedEnvelope getExtent() {
		IMap currentMap = ApplicationGIS.getActiveMap();
		if (!currentMap.equals(ApplicationGIS.NO_MAP)) {
			System.out.println(currentMap.getViewportModel().getCRS().getDomainOfValidity());
			return (ReferencedEnvelope)currentMap.getViewportModel().getCRS().getDomainOfValidity();
		}
		return null;
	}

	@Override
	public Geometry getLimit() {
		return null;
		//return (Geometry)this.getExtent();
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
