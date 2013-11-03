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
        
        public String toString() {
            return "TRUE";
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
        public String toString() {
            return "FALSE";
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
