/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.ui.operations;

/**
 * Determines whether a property value is true or false given an object.
 * <p>
 * This class is must be implemented by objectProperty extensions to teach the system new properties for
 * objects.
 * </p>
 *
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
