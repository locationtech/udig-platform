/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.ServiceExtension2;

/**
 * Adds some generic checks to attempt to process URLs and Map context objects. Essentially queries
 * the ServiceExtension for the Service in order to determine whether it can handle the context
 * objects.
 *
 * <p>
 * The context objects this will process are:
 * <ul>
 * <li>URLs</li>
 * <li>Map</li>
 * <li>String - it will try to convert the string to a URL if other processing needs to be done to a
 * string then the subclass will have to do it</li>
 *
 * @author Jesse
 * @since 1.1.0
 */
public abstract class AbstractUDIGConnectionFactory extends UDIGConnectionFactory {

    /**
     * Will use the service extension to try and determine if the context is useable.
     *
     * <p>
     * Will try to process URLs, Maps and Strings. String processing is limited to trying to create
     * a URL from the string and then processing it with the ServiceExtension
     * </p>
     * <p>
     * {@link CatalogPlugin#locateURL(Object)} is used to attempt to create a URL from the context
     * object
     * <p>
     * {@link #doOtherChecks(Object)} will be called if the context is not a URL or a Map or if the
     * String cannot be processed as a URL.
     *
     * @param context The object to be "processed" or "adapted" into connection information.
     * @return True if the info can be returned based on the conext, otherwise false.
     */
    @SuppressWarnings("unchecked")
    @Override
    public final boolean canProcess(Object context) {
        ServiceExtension2 serviceExtension = getServiceExtension();
        if (context instanceof ID) {
            ID id = (ID) context;
            return canProcess(serviceExtension, id);
        }
        if (context instanceof URL) {
            URL url = (URL) context;
            return canProcess(serviceExtension, url);
        }
        if (context instanceof String) {

            // if the string cannot be processed we want to fall through so
            // that doOtherChecks() can be called.
            String string = (String) context;
            try {
                URL url = new URL(string);
                if (canProcess(serviceExtension, url))
                    return true;
            } catch (MalformedURLException e) {
                // continue.
            }
        }
        if (context instanceof Map) {
            Map map = (Map) context;
            return canProcess(serviceExtension, map);
        }
        if (context instanceof IResolve) {
            ID id = ((IResolve) context).getID();
            return canProcess(serviceExtension, id);
        }
        URL locateURL = ID.cast(context).toURL();
        if (locateURL != null) {
            return canProcess(serviceExtension, locateURL);
        }
        return doOtherChecks(context);
    }

    /**
     * Called by canProcess. By default the method simply calls reasonForFailure on the service
     * extension
     *
     * @param serviceExtension
     * @param map parameters from {@link AbstractUDIGConnectionFactory#canProcess(Object)}
     * @return true if can process parameters
     */
    protected boolean canProcess(ServiceExtension2 serviceExtension,
            Map<String, Serializable> map) {
        return serviceExtension.reasonForFailure(map) == null;
    }

    /**
     * Called by canProcess. By default the method simply calls reasonForFailure on the service
     * extension
     *
     * @param serviceExtension
     * @param url URL created from {@link AbstractUDIGConnectionFactory#canProcess(Object)}
     * @return true if can process parameters
     */
    protected boolean canProcess(ServiceExtension2 serviceExtension, URL url) {
        return serviceExtension.reasonForFailure(url) == null;
    }

    /**
     * Called by canProcess. By default the method simply calls reasonForFailure on the id's url on
     * the service extension
     *
     * @param serviceExtension
     * @param id ID from {@link AbstractUDIGConnectionFactory#canProcess(Object)}
     * @return true if can process parameters
     */
    protected boolean canProcess(ServiceExtension2 serviceExtension, ID id) {
        return serviceExtension.reasonForFailure(id.toURL()) == null;
    }

    /**
     * If contexts other than URLs, Maps, and Strings (or if Strings need to be otherwise processed)
     * then subclass must perform those checks here.
     *
     * @param context The object to be "processed" or "adapted" into connection information.
     * @return True if the info can be returned based on the conext, otherwise false.
     */
    protected abstract boolean doOtherChecks(Object context);

    /**
     * Returns the Service extension for the extension in question.
     *
     * @return the Service extension for the extension in question.
     */
    protected abstract ServiceExtension2 getServiceExtension();

    /**
     * Returns the parameters using the Service extension if the context is a URL or String
     * (provided that Service extension claims to be able to process them). If the context is a map
     * it will be returned (again only if Service Extension claims to be able to consume it).
     * Otherwise {@link #doCreateConnectionParameters(Object)} will be called.
     */
    @SuppressWarnings("unchecked")
    @Override
    public final Map<String, Serializable> createConnectionParameters(Object context) {
        ServiceExtension2 serviceExtension = getServiceExtension();
        if (context instanceof URL) {
            URL url = (URL) context;
            if (canProcess(serviceExtension, url))
                return serviceExtension.createParams(url);
        }
        if (context instanceof String) {

            // if the string cannot be processed we want to fall through so
            // that doOtherChecks() can be called.
            String string = (String) context;
            try {
                URL url = new URL(string);
                if (canProcess(serviceExtension, url))
                    return serviceExtension.createParams(url);
                ;
            } catch (MalformedURLException e) {
                // continue.
            }
        }

        if (context instanceof Map) {
            Map params = (Map) context;
            if (canProcess(serviceExtension, params))
                return params;

        }
        URL locateURL = ID.cast(context).toURL();
        if (locateURL != null) {
            if (canProcess(serviceExtension, locateURL))
                return serviceExtension.createParams(locateURL);
        }
        return doCreateConnectionParameters(context);
    }

    /**
     * Called if {@link #createConnectionParameters(Object)} fails to return a value
     */
    protected abstract Map<String, Serializable> doCreateConnectionParameters(Object context);

    /**
     * Returns the URL if the context is a URL or can be made a URL from a String(provided that
     * Service extension claims to be able to process them). If the context anything else then
     * {@link #doCreateConnectionURL(Object)} will be called.
     */
    @Override
    public final URL createConnectionURL(Object context) {
        ServiceExtension2 serviceExtension = getServiceExtension();
        if (context instanceof URL) {
            URL url = (URL) context;
            if (canProcess(serviceExtension, url))
                return url;
        }
        if (context instanceof String) {

            // if the string cannot be processed we want to fall through so
            // that doOtherChecks() can be called.
            String string = (String) context;
            try {
                URL url = new URL(string);
                if (canProcess(serviceExtension, url))
                    return url;
            } catch (MalformedURLException e) {
                // continue.
            }
        }
        URL locateURL = ID.cast(context).toURL();
        if (locateURL != null) {
            if (canProcess(serviceExtension, locateURL))
                return locateURL;
        }
        return doCreateConnectionURL(context);
    }

    /**
     * Called if {@link #createConnectionURL(Object)} fails to return a value
     */
    protected abstract URL doCreateConnectionURL(Object context);

}
