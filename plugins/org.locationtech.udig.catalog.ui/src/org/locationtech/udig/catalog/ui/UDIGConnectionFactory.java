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
package org.locationtech.udig.catalog.ui;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;

/**
 * Implementations of this class provide connection information based on context.
 * <p>
 * The connection information can be in the form of a map of connection Parameters, or a url, or
 * both.
 * </p>
 * <p>
 * Implementations of this class have two responsibilities. The first is to create a set of
 * connection parameters based on context. The second is to create a user interface capable of
 * capturing user connection parameters.
 * </p>
 * <p>
 * Implementations of this class are provided via the
 * org.locationtech.udig.catalog.ui.connectionFactory extension point.
 * </p>
 *
 * @author Justin Deoliveira,Refractions Research Inc.,jdeolive@refractions.net
 */
public abstract class UDIGConnectionFactory {

    /** extension point id **/
    public static final String XPID = "org.locationtech.udig.catalog.ui.connectionFactory"; //$NON-NLS-1$

    protected UDIGConnectionFactoryDescriptor descriptor;

    /**
     * Determines if the connection factory is capable of providing some connection information
     * based on the context object.
     * <p>
     * Default implementation simply checks if createConnectionParameters and createConnectionURL
     * are non null (since if either work chances are we "canProcess" the context).
     * <p>
     * Context is often a workbench selection, or a Drag and Drop content such as a URL or File.
     * Reviewing existing implementations the following are popular contents types to converter:
     * <ul>
     * <li>URL
     * <li>File - usually by file.toURI().toURL()
     * <li>IResource - usually by resource.toID().toURL()
     * </ul>
     * You may find it handy to refer to your ServiceExtention to see if it can use a URL produced
     * from the provided context.
     *
     * @param object The object to be "processed" or "adapted" into connection information.
     * @return True if the info can be returned based on the context, Default implementation checks
     *         if connection parameters or a URL can be produced.
     */
    public boolean canProcess(Object context) {
        Map<String, Serializable> params = createConnectionParameters(context);
        if (params != null) {
            return true; // if connection parameters can be produced then we "canProcess"
        }
        URL url = createConnectionURL(context);
        if (url != null) {
            return true; // if connection URL can be produced then we "canProcess"
        }
        return false; // we have no idea what context is
    }

    /**
     * Get the connection parameters based on the provided context.
     * <p>
     * Context is often data from a workbench selection, but does not have to be.
     * </p>
     *
     * @param object The object to be "processed" or "adapted" into a map of connection parameters.
     * @return Map of connection parameters, or null if no such parameters could be created.
     */
    public abstract Map<String, Serializable> createConnectionParameters(Object context);

    /**
     * Get a connection URL based on the provided context.
     * <p>
     * Context is often data from a workbench selection, but does not have to be.
     * </p>
     *
     * @param object The object to be "processed" or "adapted" into a URL.
     * @return An URL, or null if no such URL can be created.
     */
    public abstract URL createConnectionURL(Object context);

    /**
     * Sets the descriptor which describes the connection factory.
     */
    public void setDescriptor(UDIGConnectionFactoryDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * This method returns the wizard page used to capture connection parameters. Subclasses may
     * extend, but not override this method.
     *
     * @return A wizard connection page used to capture connection parameters.
     */
    public UDIGConnectionPage createConnectionPage(int pageIndex) {
        try {
            return descriptor.createConnectionPage(pageIndex);
        } catch (CoreException e) {
            CatalogUIPlugin.log(e.getLocalizedMessage(), e);
        }

        return null;
    }

}
