/*
 *    Parkinfo
 *    http://qpws/parkinfo
 *
 *    (C) 2011, Department of Environment Resource Management
 *
 *    This code is provided for department use.
 */
package net.refractions.udig.project.listener;

import java.beans.PropertyChangeEvent;

import org.eclipse.core.runtime.ListenerList;

/**
 * Holds onto all the {@link EditFeatureListener} for an EditFeature, provides additional utility
 * methods and is backed by a normal {@link ListenerList}.
 * 
 * @author levi.putna
 * 
 */
public class EditFeatureListenerList {

    private ListenerList listeners = new ListenerList();

    /**
     * Adds a listener to this list. This method has no effect if the <a
     * href="ListenerList.html#same">same</a> listener is already registered.
     * 
     * @param listener the non-<code>null</code> listener to add
     */
    public void add(EditFeatureListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes a listener from this list. Has no effect if the <a
     * href="ListenerList.html#same">same</a> listener was not already registered.
     * 
     * @param listener the non-<code>null</code> listener to remove
     */
    public void remove(EditFeatureListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Utility method to fire notify all value change listeners
     */
    public void doValueChange(PropertyChangeEvent event) {
        Object[] listenerArray = getListeners();

        for (int i = 0; i < listenerArray.length; i++) {
            EditFeatureListener listener = (EditFeatureListener) listenerArray[i];
            listener.attributeValueChange(event);

        }
    }

    /**
     * Utility method to notify all value change listeners.
     * 
     * @param stateChangeEvent
     * @param attributeStatus
     */
    public void doStateChange(EditFeatureStateChangeEvent stateChangeEvent) {
        Object[] listenerArray = getListeners();

        for (int i = 0; i < listenerArray.length; i++) {
            EditFeatureListener listener = (EditFeatureListener) listenerArray[i];
            listener.attributeStateChange(stateChangeEvent);

        }
    }

    /**
     * Returns an array containing all the registered listeners. The resulting array is unaffected
     * by subsequent adds or removes. If there are no listeners registered, the result is an empty
     * array. Use this method when notifying listeners, so that any modifications to the listener
     * list during the notification will have no effect on the notification itself.
     * <p>
     * Note: Callers of this method <b>must not</b> modify the returned array.
     * 
     * @return the list of registered listeners
     */
    public Object[] getListeners() {
        return listeners.getListeners();
    }

    /**
     * Returns whether this listener list is empty.
     * 
     * @return <code>true</code> if there are no registered listeners, and <code>false</code>
     *         otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns the number of registered listeners.
     * 
     * @return the number of registered listeners
     */
    public int size() {
        return getListeners().length;
    }

    /**
     * Removes all listeners from this list.
     */
    public synchronized void clear() {
        listeners.clear();
    }

}
