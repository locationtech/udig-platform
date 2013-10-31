/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.teradata;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceInfo;

public class TeradataServiceHolder extends IService {

    private URL id;
    private Map<String, Serializable> params;

    public TeradataServiceHolder(URL finalID, Map<String, Serializable> params2) {
        this.id = finalID;
        this.params = params2;
    }

    @Override
    public Status getStatus() {
        return Status.NOTCONNECTED;
    }

    @Override
    public Throwable getMessage() {
        return null;
    }

    @Override
    public URL getIdentifier() {
        return id;
    }

    @Override
    public List<? extends IGeoResource> resources(IProgressMonitor monitor) throws IOException {
        return Collections.emptyList();
    }

    @Override
    protected IServiceInfo createInfo(IProgressMonitor monitor) throws IOException {
        return new IServiceInfo("Driverless Teradata connection", "Teradata plugin needs jdbc driver", "", null, null,
                null, null, null);
    }

    @Override
    public Map<String, Serializable> getConnectionParams() {
        return params;
    }

}
