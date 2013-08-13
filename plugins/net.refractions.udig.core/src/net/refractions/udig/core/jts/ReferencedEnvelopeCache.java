/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.core.jts;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.BoundingPolygon;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class ReferencedEnvelopeCache {
    private static Cache<Object, Object> crsCache = CacheBuilder.newBuilder().build();

    public static ReferencedEnvelope getReferencedEnvelope(final CoordinateReferenceSystem crs) {
        if (crs == null) {
            return new ReferencedEnvelope();
        }
        try {
            ReferencedEnvelope envelope = (ReferencedEnvelope) crsCache.get( crs.getName(), new Callable<Object>(){
                public Object call() {
                    return getCRSBounds(crs);
                }
            });
            return envelope;
        } catch (Throwable e) {
            return new ReferencedEnvelope();
        }
    }

    private static ReferencedEnvelope getCRSBounds(CoordinateReferenceSystem crs) {
        if (crs == null)
            return new ReferencedEnvelope();
        try {
            Extent extent = crs.getDomainOfValidity();
            Collection<? extends GeographicExtent> elem = extent.getGeographicElements();
            double xmin = Double.MAX_VALUE, ymin = Double.MAX_VALUE;
            double xmax = Double.MIN_VALUE, ymax = Double.MIN_VALUE;
            for (GeographicExtent ext : elem) {
                if (ext instanceof BoundingPolygon) {
                    BoundingPolygon bp = (BoundingPolygon) ext;
                    Collection<? extends org.opengis.geometry.Geometry> geoms = bp.getPolygons();
                    for (org.opengis.geometry.Geometry geom : geoms) {
                        Envelope env = geom.getEnvelope();
                        if (env.getMinimum(0) < xmin)
                            xmin = env.getMinimum(0);
                        if (env.getMaximum(0) > xmax)
                            xmax = env.getMaximum(0);
                        if (env.getMinimum(1) < ymin)
                            ymin = env.getMinimum(1);
                        if (env.getMaximum(1) > ymax)
                            ymax = env.getMaximum(1);
                    }
                } else if (ext instanceof GeographicBoundingBox) {
                    GeographicBoundingBox gbb = (GeographicBoundingBox) ext;
                    ReferencedEnvelope env = new ReferencedEnvelope(DefaultGeographicCRS.WGS84);
                    env.expandToInclude(gbb.getWestBoundLongitude(), gbb.getNorthBoundLatitude());
                    env.expandToInclude(gbb.getEastBoundLongitude(), gbb.getSouthBoundLatitude());
                    env = env.transform(crs, true);
                    if (env.getMinX() < xmin)
                        xmin = env.getMinX();
                    if (env.getMaxX() > xmax)
                        xmax = env.getMaxX();
                    if (env.getMinY() < ymin)
                        ymin = env.getMinY();
                    if (env.getMaxY() > ymax)
                        ymax = env.getMaxY();
                }
            }
            if (xmin == Double.MAX_VALUE || ymin == Double.MAX_VALUE || xmax == Double.MIN_VALUE
                    || ymax == Double.MAX_VALUE) {
                return new ReferencedEnvelope(crs);
            }
            return new ReferencedEnvelope(xmin, xmax, ymin, ymax, crs);
        } catch (Throwable ex) {
            return new ReferencedEnvelope(crs);
        }
    }
}
