/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.geotools.data;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.geotools.data.DataUtilities;
import org.geotools.util.URLs;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.geotools.Activator;
import org.locationtech.udig.catalog.ui.UDIGConnectionFactory;

/**
 * Try and connect to any GeoTools DataStore
 * 
 * @author jody
 * @since 1.1.0
 */
public class DataStoreConnectionFactory extends UDIGConnectionFactory {

        @Override
    public Map<String, Serializable> createConnectionParameters( Object context ) {
        try {
            if (context instanceof URL) {
                URL url = (URL) context;
                Map<String, Serializable> params = DataStoreServiceExtension
                        .createDataAcessParameters(url);
                return params;
            }
            if (context instanceof File) {
                File file = (File) context;
                URL url = URLs.fileToUrl(file);

                Map<String, Serializable> params = DataStoreServiceExtension
                        .createDataAcessParameters(url);
                return params;
            }
            if (context instanceof IResolve) {
                IResolve resolve = (IResolve) context;
                ID id = resolve.getID();
                URL url = id.toURL();

                Map<String, Serializable> params = DataStoreServiceExtension
                        .createDataAcessParameters(url);
                return params;
            }
        } catch (Throwable t) {
            // any problem at this point kind of indicates that the context cannot
            // be processed
            if (Activator.getDefault().isDebugging()) {
                IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID,
                        "DataStoreConnection cannot process: " + context, t);
                Activator.getDefault().getLog().log(status);
            }
        }
        return null;
    }

    @Override
    public URL createConnectionURL( Object context ) {
        try {
            if (context instanceof URL) {
                URL url = (URL) context;
                Map<String, Serializable> params = DataStoreServiceExtension
                        .createDataAcessParameters(url);
                return params != null ? url : null;
            }
            if (context instanceof File) {
                File file = (File) context;
                URL url = URLs.fileToUrl(file);

                Map<String, Serializable> params = DataStoreServiceExtension
                        .createDataAcessParameters(url);
                return params != null ? url : null;
            }
            if (context instanceof IResolve) {
                IResolve resolve = (IResolve) context;
                ID id = resolve.getID();
                URL url = id.toURL();

                Map<String, Serializable> params = DataStoreServiceExtension
                        .createDataAcessParameters(url);

                return params != null ? url : null;
            }
        } catch (Throwable t) {
            // any problem at this point kind of indicates that the context cannot
            // be processed
            if (Activator.getDefault().isDebugging()) {
                IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID,
                        "DataStoreConnection cannot produce a url: " + context, t);
                Activator.getDefault().getLog().log(status);
            }
        }
        return null;
    }
}
