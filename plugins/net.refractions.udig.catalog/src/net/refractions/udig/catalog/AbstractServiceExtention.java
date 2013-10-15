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
package net.refractions.udig.catalog;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import net.refractions.udig.core.internal.CorePlugin;

/**
 * Abstract class used to provide methods for common 
 * service extension activities such (mostly processing connection
 * parameters).
 * <p>
 * This class is a good base class when implementing your
 * own ServiceExtention.
 * <p>
 * You can provide implementations for createParams( url ) and
 * resonForFailure( url ) if you wish to support drag and drop.
 * 
 * @author Jody Garnett
 */
public abstract class AbstractServiceExtention implements ServiceExtension2 {

    /**
     * Do our best to to unpack the a url from the provided string.
     * <p>
     * This method makes use of CorePlugin.RELAXED_HANDLER in order
     * to be very forgiving about URL format.
     * @return url based on the provided id
     * @throws MalformedURLException
     */
    protected URL toURL( String id ) throws MalformedURLException {
        return new URL(null, id, CorePlugin.RELAXED_HANDLER);
    }
    
    /** 
     * Create default connection parameters from the provided url (is possible).
     */
    public Map<String, Serializable> createParams( URL url ) {
        return null;
    }

    public String reasonForFailure( URL url ) {
        return null;
    }

}
