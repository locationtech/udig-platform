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
package org.locationtech.udig.project;

import org.eclipse.core.runtime.IExtension;

/**
 * Provides an object of type T. Used to populate the IBlackboard with default values.  
 * <p>This is used by the <em>org.locationtech.udig.project.provider</em> extension point</p>
 * 
 * @author Jesse
 * @since 1.0.0
 */
public abstract class IProvider<T> {

    /** Extension point id. * */
    public static final String XPID = "org.locationtech.udig.project.provider"; //$NON-NLS-1$

    /** the extension configuration element * */
    IExtension extension;

    /** key used to identify provider * */
    String key;

    /**
     * Sets the extension that provier originated from. This method should not be called by client
     * code.
     * 
     * @param extension The extension in which the provider was instantiated.
     * @uml.property name="extension"
     */
    public void setExtension( IExtension extension ) {
        this.extension = extension;
    }

    /**
     * @return the extension the provider originated from.
     * @uml.property name="extension"
     */
    public IExtension getExtension() {
        return extension;
    }

    /**
     * @return the key that is used to identify the object being provided.
     * @uml.property name="key"
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key that is used to identify the object being provided.
     * @uml.property name="key"
     */
    public void setKey( String key ) {
        this.key = key;
    }

    /**
     * Returns the class of the object being provided. How this class relates to the class of
     * objects being provided (via inheritance) is up to the client of the provider.
     * 
     * @return The type of the object being provider (the providee).
     */
    public abstract Class<T> getProvidee();

    /**
     * Signals the provider to provide an object of the specified class. If the object can not be
     * provided.
     * 
     * @return The object being provided, otherwise null.
     */
    public abstract T provide();

}
