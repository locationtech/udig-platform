/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.moved;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import org.locationtech.udig.catalog.AbstractServiceExtention;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IService;

/**
 * Create a MovedService handle recording where a service was moved to in the catalog.
 *
 * @author Jody Garnett (Refractions Research Inc)
 */
public class MovedServiceExtention extends AbstractServiceExtention {
    /**
     * Key used to look up in the connection parameters the identifier of the this MovedService.
     * <p>
     * This is the same as the identifier of the service before it was moved.
     */
    public static String ID_KEY = "id"; //$NON-NLS-1$

    /**
     * Key used to look up the connection parameter for the identifier of the service where it
     * exists now in the catalog.
     */
    public static String FORWARD_KEY = "forward"; //$NON-NLS-1$

    /**
     * Create the MovedService handle based on the provided parameters.
     */
    @Override
    public IService createService(URL id, Map<String, Serializable> params) {
        if (id != null) {
            CatalogPlugin.trace("Ignoring requested id=" + id + " for moved service", null); //$NON-NLS-1$ //$NON-NLS-2$
        }
        URL identifier = (URL) params.get(ID_KEY);
        URL forward = (URL) params.get(FORWARD_KEY);
        if (identifier == null || forward == null) {
            return null;
        }
        return new MovedService(new ID(id), new ID(forward));
    }

    @Override
    public String reasonForFailure(Map<String, Serializable> params) {
        if (!params.containsKey(ID_KEY) || !params.containsKey(FORWARD_KEY)) {
            return null; // not interested
        }
        try {
            URL identifier = (URL) params.get(ID_KEY);
            URL forward = (URL) params.get(FORWARD_KEY);
            if (identifier == null || forward == null) {
                return null; // not interested
            }
        } catch (ClassCastException huh) {
            return "ID and FORWARD are required to be URLs"; //$NON-NLS-1$
        }
        return null;
    }

}
