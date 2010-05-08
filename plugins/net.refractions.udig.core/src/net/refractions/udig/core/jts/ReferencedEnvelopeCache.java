package net.refractions.udig.core.jts;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.BoundingPolygon;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.google.common.collect.ReferenceMap;

public class ReferencedEnvelopeCache {
	private static Map<CoordinateReferenceSystem, ReferencedEnvelope> crsCache;
	
	private static synchronized Map<CoordinateReferenceSystem, ReferencedEnvelope> getCache() {
		if(crsCache == null) {
			crsCache = new HashMap<CoordinateReferenceSystem, ReferencedEnvelope>();
		}
		return crsCache;
	}
	
	public static ReferencedEnvelope getReferencedEnvelope(CoordinateReferenceSystem crs) {
		if(crs == null)
			return new ReferencedEnvelope();
		try {
			Map<CoordinateReferenceSystem, ReferencedEnvelope> map = getCache();
			ReferencedEnvelope envelope = map.get(crs);
			if(envelope == null) {
				envelope = getCRSBounds(crs);
			}
			return envelope;
		} catch(Throwable e) {
			e.printStackTrace(System.out);
			return new ReferencedEnvelope();
		}
	}
	
	private static ReferencedEnvelope getCRSBounds(CoordinateReferenceSystem crs) {
		if(crs == null)
			return new ReferencedEnvelope();
		try {
			Extent extent = crs.getDomainOfValidity();
			Collection<? extends GeographicExtent> elem = extent.getGeographicElements();
			double xmin = Double.MAX_VALUE, ymin = Double.MAX_VALUE;
			double xmax = Double.MIN_VALUE, ymax = Double.MIN_VALUE;
			for(GeographicExtent ext : elem) {
				if(ext instanceof BoundingPolygon) {
					BoundingPolygon bp = (BoundingPolygon)ext;
					Collection<? extends org.opengis.geometry.Geometry> geoms = bp.getPolygons();
					for(org.opengis.geometry.Geometry geom : geoms) {
						Envelope env = geom.getEnvelope();
						if(env.getMinimum(0) < xmin)
							xmin = env.getMinimum(0);
						if(env.getMaximum(0) > xmax)
							xmax = env.getMaximum(0);
						if(env.getMinimum(1) < ymin)
							ymin = env.getMinimum(1);
						if(env.getMaximum(1) > ymax)
							ymax = env.getMaximum(1);
					}
				} else if(ext instanceof GeographicBoundingBox) {
					GeographicBoundingBox gbb = (GeographicBoundingBox)ext;
					ReferencedEnvelope env = new ReferencedEnvelope(DefaultGeographicCRS.WGS84);
					env.expandToInclude(gbb.getWestBoundLongitude(), gbb.getNorthBoundLatitude());
					env.expandToInclude(gbb.getEastBoundLongitude(), gbb.getSouthBoundLatitude());
					env = env.transform(crs, true);
					if(env.getMinX() < xmin)
						xmin = env.getMinX();
					if(env.getMaxX() > xmax)
						xmax = env.getMaxX();
					if(env.getMinY() < ymin)
						ymin = env.getMinY();
					if(env.getMaxY() > ymax)
						ymax = env.getMaxY();
				}
			}
			if(xmin == Double.MAX_VALUE || 
					ymin == Double.MAX_VALUE ||
					xmax == Double.MIN_NORMAL ||
					ymax == Double.MAX_VALUE) {
				System.out.println("No sensible extents generated.");
				return new ReferencedEnvelope(crs);
			}
			return new ReferencedEnvelope(xmin, xmax, ymin, ymax, crs);
		} catch(Throwable ex) {
			ex.printStackTrace(System.out);
			return new ReferencedEnvelope(crs);
		}
	}
}
