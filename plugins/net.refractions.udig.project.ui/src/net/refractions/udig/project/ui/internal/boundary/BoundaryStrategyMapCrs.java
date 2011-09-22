package net.refractions.udig.project.ui.internal.boundary;

import java.lang.reflect.InvocationTargetException;

import net.refractions.udig.boundary.IBoundaryStrategy;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * Returns an empty ReferencedEnvelope so that zoom to extent goes to all
 * @author pfeiffp
 *
 */
public class BoundaryStrategyMapCrs implements IBoundaryStrategy {

	private static String name = "Map CRS";
	private static ReferencedEnvelope crsExtent = new ReferencedEnvelope();
	private static CoordinateReferenceSystem crs;
	
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
							crsExtent = crsExtent.transform(DefaultGeographicCRS.WGS84, true);
						} catch (Throwable t) {
							CatalogUIPlugin.log("Unable to get world bounds:"+t, t); //$NON-NLS-1$
						} finally {
							monitor.done();
						}
					}
				};
				PlatformGIS.runInProgressDialog( "Getting World Bounds", true, operation, false );
				return crsExtent;
			}
			return crsExtent;
		}
		return null;
	}

	@Override
	public Geometry getBoundary() {
		ReferencedEnvelope extent = this.getExtent();
		if (extent != null) {
			return new GeometryFactory().toGeometry(extent);
		}
		return null;
	}

	@Override
	public CoordinateReferenceSystem getCrs() {
		final IMap currentMap = ApplicationGIS.getActiveMap();
		if (!currentMap.equals(ApplicationGIS.NO_MAP)) {
			crs = currentMap.getViewportModel().getCRS();
			if (crs.getDomainOfValidity() == null) {
				// fall back to world extent which is converted to default CRS because it uses function getBounds
				return DefaultGeographicCRS.WGS84;
			}
			return crs;
		}
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Boolean enableZoomToExtent() {
		return true;
	}

	@Override
	public Boolean enableSearchCatalog() {
		return true;
	}
	
	

}
