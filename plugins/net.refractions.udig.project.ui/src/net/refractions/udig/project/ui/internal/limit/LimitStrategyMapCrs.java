package net.refractions.udig.project.ui.internal.limit;

import java.lang.reflect.InvocationTargetException;

import net.refractions.udig.catalog.ui.CatalogUIPlugin;
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
	private static ReferencedEnvelope crsExtent = new ReferencedEnvelope();
	
	@Override
	public ReferencedEnvelope getExtent() {
		final IMap currentMap = ApplicationGIS.getActiveMap();
		if (!currentMap.equals(ApplicationGIS.NO_MAP)) {
			crsExtent = (ReferencedEnvelope)currentMap.getViewportModel().getCRS().getDomainOfValidity();
			if (crsExtent == null) {
				// fall back to world extent
				IRunnableWithProgress operation = new IRunnableWithProgress() {
					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException,
							InterruptedException {
						try {
							monitor.beginTask("Message!!!", 100);
							crsExtent = currentMap.getBounds(monitor);
						} catch (Throwable t) {
							CatalogUIPlugin.log("Unable to get world bounds:"+t, t); //$NON-NLS-1$
						} finally {
							monitor.done();
						}
					}
				};
				PlatformGIS.runInProgressDialog( "Getting World Bounds", true, operation, false );
				System.out.println(crsExtent);
				return crsExtent;
			}
			System.out.println(crsExtent);
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
			return currentMap.getViewportModel().getCRS();
		}
		return null;
	}

	@Override
	public String getName() {
		return name;
	}
	
	

}
