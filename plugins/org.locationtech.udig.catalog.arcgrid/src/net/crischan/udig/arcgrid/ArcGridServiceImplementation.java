/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    
 *    (C) 2010, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package net.crischan.udig.arcgrid;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.catalog.rasterings.AbstractRasterGeoResource;
import org.locationtech.udig.catalog.rasterings.AbstractRasterService;
import org.locationtech.udig.catalog.rasterings.AbstractRasterServiceInfo;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

public class ArcGridServiceImplementation extends AbstractRasterService {
    public ArcGridServiceImplementation(URL id, org.geotools.coverage.grid.io.GridFormatFactorySpi factory) {
        super(id, ArcGridServiceExtension.TYPE, factory);
    }

    @Override
    protected AbstractRasterServiceInfo createInfo(IProgressMonitor monitor) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        monitor.beginTask("ArcGrid loading", 2);
        try {
            monitor.worked(1);
            return new AbstractRasterServiceInfo(this, ".asc", ".grd"); //$NON-NLS-1$//$NON-NLS-2$
        } finally {
            monitor.done();
        }
    }

    @Override
    public Map<String, Serializable> getConnectionParams() {
        return new ArcGridServiceExtension().createParams(getIdentifier());
    }

    @Override
    public List<AbstractRasterGeoResource> resources(IProgressMonitor monitor) throws IOException {
        if (monitor != null) {
            String msg = MessageFormat.format("Connecting to", new Object[] {});
            monitor.beginTask(msg, 5);
        }

        if (reader != null && monitor != null) {
            monitor.worked(3);
        }

        ArcGridGeoResourceImplementation res = new ArcGridGeoResourceImplementation(this, getHandle());

        List<AbstractRasterGeoResource> list = new ArrayList<AbstractRasterGeoResource>();
        list.add(res);

        if (monitor != null)
            monitor.done();

        return list;
    }
}
