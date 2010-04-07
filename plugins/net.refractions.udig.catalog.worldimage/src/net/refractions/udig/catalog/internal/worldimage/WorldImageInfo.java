/**
 * 
 */
package net.refractions.udig.catalog.internal.worldimage;

import net.refractions.udig.catalog.rasterings.AbstractRasterGeoResource;
import net.refractions.udig.catalog.rasterings.AbstractRasterGeoResourceInfo;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

final class WorldImageInfo extends
		AbstractRasterGeoResourceInfo {
	private final CoordinateReferenceSystem crs;

	WorldImageInfo(
			AbstractRasterGeoResource resource, CoordinateReferenceSystem crs) {
		super(resource, "WorldImage", "world image", ".gif", ".jpg",
				".jpeg", ".tif", ".tiff", ".png");
		this.crs = crs;
	}

	@Override
	public CoordinateReferenceSystem getCRS() {
		return crs;
	}
	
	@Override
	public synchronized ReferencedEnvelope getBounds() {
		ReferencedEnvelope b = super.getBounds();
		// the bounds gets the projection information from the coverage which defaults to WGS84 if it doesn't know
		// the projection.  So we get the bounds and set the CRS read from the prj file
		return new ReferencedEnvelope(b, crs);
	}
}