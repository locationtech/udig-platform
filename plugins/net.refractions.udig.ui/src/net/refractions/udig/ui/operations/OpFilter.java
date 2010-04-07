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
 * Represents the filter element in the operation extension point.
 * @author jones
 * @since 1.1.0
 */
public interface OpFilter {
    
    OpFilter TRUE = new OpFilter(){

        public boolean accept( Object object ) {
            return true;
        }

        public void addListener( IOpFilterListener listener ) {
        }

        public boolean canCacheResult() {
            return true;
        }

        public boolean isBlocking() {
            return false;
        }

        public void removeListener( IOpFilterListener listener ) {
        }
        
        
        
    };
    OpFilter FALSE = new OpFilter(){

        public boolean accept( Object object ) {
            return false;
        }

        public void addListener( IOpFilterListener listener ) {
        }

        public boolean canCacheResult() {
            return true;
        }

        public boolean isBlocking() {
            return false;
        }

        public void removeListener( IOpFilterListener listener ) {
        }
        
    };

    /**
     * Returns true if the object is accepted by the filter
     *
     * @param object object to test
     * @return true if the object is accepted by the filter
     */
    boolean accept(Object object);
    /**
     * Returns true if the results can be cached.
     * 
     * <p>A result can be cached if
     * <ol>
     * <li>The value will never change</li>
     * <li>It is possible for the filter to listen for changes and update listeners 
     * added via the {@link #addListener(IOpFilterListener)} method</li>
     * </ol>
     * </p><p>
     * Therefore this method only returns false if it must be calculated each time because there is 
     * no way to listen for state changes.  If it is non-blocking that is fine, if it is blocking
     * then try to do this rarely.
     * </p>
     * <p>WARNING:  If this returns true then the listeners must be notified for the new value to be 
     * recognized</p>
     * @return true if the results can be cached.  
     */
    boolean canCacheResult();
    
    /**
     * Returns true if processing this filter may block when {@link #accept(Object)} is called or takes a large amount
     * of time to execute.
     *
     * @return true if processing this filter may block when {@link #accept(Object)} is called.
     */
    boolean isBlocking();
    /**
     * Adds a listener to listen for events indicating the value has changed.  Listeners should only be added
     * if {@link #canCacheResult()} returns true.
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
