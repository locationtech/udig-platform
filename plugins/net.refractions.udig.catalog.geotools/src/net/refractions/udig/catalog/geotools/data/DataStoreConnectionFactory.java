package net.refractions.udig.catalog.geotools.data;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.geotools.Activator;
import net.refractions.udig.catalog.ui.UDIGConnectionFactory;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.geotools.data.DataUtilities;

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
                URL url = DataUtilities.fileToURL(file);

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
                URL url = DataUtilities.fileToURL(file);

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