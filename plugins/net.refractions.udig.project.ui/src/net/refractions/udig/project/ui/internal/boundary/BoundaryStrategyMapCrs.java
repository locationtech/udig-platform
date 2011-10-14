package net.refractions.udig.project.ui.internal.boundary;

import java.lang.reflect.InvocationTargetException;

import net.refractions.udig.boundary.IBoundaryStrategy;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.resources.CRSUtilities;
import org.geotools.util.logging.Logging;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.BoundingPolygon;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * Returns an empty ReferencedEnvelope so that zoom to extent goes to all
 * @author pfeiffp
 *
 */
public class BoundaryStrategyMapCrs extends IBoundaryStrategy {

	private static String name = "Map CRS";
	private static ReferencedEnvelope crsExtent = new ReferencedEnvelope();
	private static CoordinateReferenceSystem crs;
	
	@Override
	public ReferencedEnvelope getExtent() {
		final IMap currentMap = ApplicationGIS.getActiveMap();
		if (!currentMap.equals(ApplicationGIS.NO_MAP)) {
			CoordinateReferenceSystem worldCRS = currentMap.getViewportModel().getCRS();
//			Extent valid = worldCRS.getDomainOfValidity();
//			for( GeographicExtent geographic : valid.getGeographicElements() ){
//			   // this is all handled by getEnvelope
//			}
			Envelope envelope = CRS.getEnvelope(worldCRS);
			
			if( envelope instanceof BoundingBox){
			    crsExtent = ReferencedEnvelope.reference( (BoundingBox) envelope);
			    if( crsExtent.getCoordinateReferenceSystem() != worldCRS ){
			        try {
                        crsExtent = crsExtent.transform(worldCRS, true);
                    } catch (Exception e) {
                        // cannot compute!
                        crsExtent = null;
                    }
			    }
			}
            if (crsExtent == null || crsExtent.isEmpty()) {
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
            
            // debugging code to be removed
            /*GeneralEnvelope merged = null;
            
            Extent domainOfValidity = worldCRS.getDomainOfValidity();
            if (domainOfValidity != null) {
                for (final GeographicExtent extent : domainOfValidity.getGeographicElements()) {
                    if (Boolean.FALSE.equals(extent.getInclusion())) {
                        continue;
                    }
                    if (extent instanceof BoundingPolygon) {
                        for (final org.opengis.geometry.Geometry geometry : ((BoundingPolygon) extent).getPolygons()) {
                            final Envelope candidate = geometry.getEnvelope();
                            if (candidate != null) {
                                final CoordinateReferenceSystem sourceCRS =
                                        candidate.getCoordinateReferenceSystem();
                                if (sourceCRS == null || CRS.equalsIgnoreMetadata(sourceCRS, crs)) {
                                    if (envelope == null) {
                                        envelope = candidate;
                                    } else {
                                        if (merged == null) {
                                            envelope = merged = new GeneralEnvelope(envelope);
                                        }
                                        merged.add(envelope);
                                    }
                                }
                            }
                        }
                    } else if (extent instanceof GeographicBoundingBox) {
                        //System.out.println("failed to use this: " + extent);
                        //System.out.println("class: " + extent.getClass());
                        GeographicBoundingBox test = (GeographicBoundingBox)extent;
                        ReferencedEnvelope testGeo = new ReferencedEnvelope(test.getWestBoundLongitude(), test.getEastBoundLongitude(), test.getNorthBoundLatitude(), test.getSouthBoundLatitude(), DefaultGeographicCRS.WGS84);
                        //System.out.println(testGeo);
                        try {
                            crsExtent = testGeo.transform(worldCRS, true);
                            //System.out.println(crsExtent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } /*else if (extent instanceof GeographicBoundingBox) {
                        merged = new GeneralEnvelope((GeographicBoundingBox)extent);
                        try {
                            envelope = transform(merged, crs);
                        } catch (TransformException exception) {
                            envelope = null;
                            unexpectedException("getEnvelope", exception);
                        }
                    }*//*
                }
            }
            if (worldCRS.getDomainOfValidity() != null) {
                Extent test = worldCRS.getDomainOfValidity();
                System.out.println(test.getGeographicElements());
            }

            /*if (envelope.getMaximum(0) == testEnvelope.getMaximum(0)
                    && envelope.getMaximum(1) == testEnvelope.getMaximum(1)
                    && envelope.getMinimum(0) == testEnvelope.getMinimum(0)
                    && envelope.getMinimum(1) == testEnvelope.getMinimum(1)
                    ) {
                System.out.println("default envelope: " + testEnvelope);
            }*/
            
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

}
