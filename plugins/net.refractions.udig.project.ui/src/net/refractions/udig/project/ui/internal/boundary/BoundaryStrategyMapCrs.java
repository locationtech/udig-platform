/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.project.ui.internal.boundary;

import net.refractions.udig.boundary.IBoundaryStrategy;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
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
	
	@Override
	public ReferencedEnvelope getExtent() {
		final IMap currentMap = ApplicationGIS.getActiveMap();
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
