/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui.operations;

/**
 * Determines whether a property value is true or false given an object.
 * <p>
 * This class is must be implemented by objectProperty extensions to teach the system new
 * properties for objects.
 * </p>
 * You can compare this to the idea of "core expressions" introduced to the RCP
 * platform in eclipse. This is something like a core expression that can only
 * be a Boolean.
 * 
 * @author jones
 * @since 1.1.0
 */
public interface PropertyValue<T> {

    /**
     * Returns true if the value provided is a legal value for the object.  The subclass determines what
     * are legal values.
     *
     * @param object the object that will be used to determine if the value is legal
     * @param value an arbitrary string. 
     * @return Returns true if the value provided is a legal value for the object.  
     */
    boolean isTrue( T object, String value );
    /**
     * Returns true if the results can be cached.  A result can be cached if
     * <ol>
     * <li>The value will never change</li>
     * <li>It is possible for the filter to listen for changes and update listeners 
     * added via the {@link #addListener(IOpFilterListener)} method</li>
     * </ol>
     * 
     * Therefore this method only returns false if it must be calculated each time because there is 
     * no way to listen for state changes.  If it is non-blocking that is fine, if it is blocking
     * then try to do this rarely.
     *
     * @return true if the results can be cached.  
     */
    boolean canCacheResult();
    
    /**
     * Returns true if processing this filter may block when {@link #accept(Object)} is called.
     *
     * @return true if processing this filter may block when {@link #accept(Object)} is called.
     */
    boolean isBlocking();
    /**
     * Adds a listener to listen for events indicating the value has changed. 
     *
     * @param listener listener to add
     */
    void addListener(IOpFilterListener listener);
    /**
     * Removes a listeners
     *
     * @param listener listener to remove
     */
    void removeListener(IOpFilterListener listener);
}
