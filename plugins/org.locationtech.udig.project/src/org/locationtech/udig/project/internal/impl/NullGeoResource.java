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
package org.locationtech.udig.project.internal.impl;

import java.net.MalformedURLException;
import java.net.URL;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Envelope;

/**
 * Placeholder indicating that GeoResources was found.
 * <p>
 * This is often used by a Layer when there was no resource found for the layer.
 * 
 * @author Jesse
 */
public class NullGeoResource extends IGeoResource {

    @Override
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) {
        return null;
    }
    public <T> boolean canResolve( Class<T> adaptee ) {
        return false;
    }

    public Status getStatus() {
        return Status.BROKEN;
    }

    public Throwable getMessage() {
        return new Exception(Messages.NullGeoResource_0); 
    }

    public URL getIdentifier() {
        try {
            return new URL("http://NULL"); //$NON-NLS-1$
        } catch (MalformedURLException e) {
            // Can't happen
            e.printStackTrace();
            return null;
        }
    }

    @Override
	protected IGeoResourceInfo createInfo( IProgressMonitor monitor ) {
        // TODO Auto-generated method stub
        return new IGeoResourceInfo(){
            @Override
            public ReferencedEnvelope getBounds() {
                // TODO Auto-generated method stub
                return new ReferencedEnvelope(new Envelope(), null);
            }
            @Override
            public CoordinateReferenceSystem getCRS() {
                // TODO Auto-generated method stub
                return null;
            }

        };
    }
}
