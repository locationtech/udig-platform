/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.internal.aoi;

import org.locationtech.udig.aoi.IAOIStrategy;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Geometry;

/**
 * Returns an empty ReferencedEnvelope so that zoom to extent goes to all
 * @author pfeiffp
 *
 */
public class AOIStrategyAll extends IAOIStrategy {

	private static String name = "All";
	
	@Override
	public ReferencedEnvelope getExtent() {
		return null;
	}

	@Override
	public Geometry getGeometry() {
		return null;
	}

	@Override
	public CoordinateReferenceSystem getCrs() {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

}
