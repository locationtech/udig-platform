/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.aoi;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.udig.aoi.IAOIStrategy;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * Returns an empty ReferencedEnvelope so that zoom to extent goes to all
 * @author pfeiffp
 *
 */
public class MapCrsAOIStrategy extends IAOIStrategy {

	private static String name = "Map CRS";
	
	@Override
	public ReferencedEnvelope getExtent() {
		final IMap currentMap = ApplicationGIS.getActiveMap();
		return calcualteExtent(currentMap);
	}

    /**
     * Returns the extent of a map
     * @param currentMap
     * @return 
     * @throws MismatchedDimensionException
     */
	public static ReferencedEnvelope calcualteExtent( final IMap currentMap )
            throws MismatchedDimensionException {
        if (!currentMap.equals(ApplicationGIS.NO_MAP)) {
			CoordinateReferenceSystem worldCRS = currentMap.getViewportModel().getCRS();
			try {
                if( worldCRS.getIdentifiers() == null || worldCRS.getIdentifiers().isEmpty()){
                    // sanity check! look up the good definition in the EPSG database!!
                    String code = CRS.lookupIdentifier(worldCRS, true);
                    if( code != null ){
                        worldCRS = CRS.decode(code);
                    }
                }
            } catch (FactoryException e1) {
                // could not find a match
            }
			ReferencedEnvelope extent = null;

	        Envelope envelope = CRS.getEnvelope(worldCRS);
			if( envelope instanceof BoundingBox){
			    extent = ReferencedEnvelope.reference( (BoundingBox) envelope);
			    if( extent.getCoordinateReferenceSystem() != worldCRS ){
			        // CRS.getEnvelope(worldCRS) is inconsistent as it returned an envelope in a different CRS
			        return null; // give up!
			    }
			}
            if (extent == null || extent.isEmpty() || extent.isNull()) {
				// fall back to WGS84 bounds and transform to world CRS
                GeographicBoundingBox worldBounds = CRS.getGeographicBoundingBox(worldCRS);
                if( worldBounds != null ){
                    ReferencedEnvelope testGeo = new ReferencedEnvelope(worldBounds.getWestBoundLongitude(), worldBounds.getEastBoundLongitude(), worldBounds.getNorthBoundLatitude(), worldBounds.getSouthBoundLatitude(), DefaultGeographicCRS.WGS84);
                    try {
                        extent = testGeo.transform(worldCRS,  true );
                    } catch (TransformException e) {
                        extent = null; // off the map!
                    } catch (FactoryException e) {
                        extent = null; // off the map!
                    }
                }
			}
            
            if (extent == null || extent.isEmpty() || extent.isNull()) {
                return null; // go fish
            }
            return extent;
		}
		return null;
    }

	@Override
	public Geometry getGeometry() {
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
		    return currentMap.getViewportModel().getCRS();
		}
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

}
