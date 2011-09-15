package net.refractions.udig.project.ui.internal.limit;

import java.lang.reflect.InvocationTargetException;

import net.refractions.udig.limit.ILimitStrategy;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * Returns an empty ReferencedEnvelope so that zoom to extent goes to all
 * @author pfeiffp
 *
 */
public class LimitStrategyMapCrs implements ILimitStrategy {

	private static String name = "Map CRS";
	
	@Override
	public ReferencedEnvelope getExtent() {
		final IMap currentMap = ApplicationGIS.getActiveMap();
		if (!currentMap.equals(ApplicationGIS.NO_MAP)) {
			ReferencedEnvelope crsExtent = new ReferencedEnvelope();
			crsExtent = (ReferencedEnvelope)currentMap.getViewportModel().getCRS().getDomainOfValidity();
			System.out.println(crsExtent);
			if (crsExtent == null) {
				// fall back to world extent
				IRunnableWithProgress operation = new IRunnableWithProgress() {
					
					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException,
							InterruptedException {
						monitor.beginTask("Message!!!", 100);
						System.out.println(currentMap.getBounds(monitor));
						
					}
				};
				PlatformGIS.runInProgressDialog( "Getting World Bounds", true, operation, false );
				
				//IProgressMonitor monitor = new ProgressMonitorPart();
				//System.out.println(currentMap.getBounds(monitor));
			}
			return crsExtent;
		}
		return null;
	}

	@Override
	public Geometry getLimit() {
		ReferencedEnvelope extent = this.getExtent();
		if (extent != null) {
			return new GeometryFactory().toGeometry(extent);
		}
		return null;
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
