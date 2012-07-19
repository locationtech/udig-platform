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

/**
 * Abstract implementation to help with implementation of anonymous inner classes.
 * <p>
 * Example:
 * 
 * <pre>
 * editFeature.addListener(new EditFeatureListenerAdaptor() {
 *     public void activate(EditFeature feature) {
 *         return true;
 *     }
 * });
 * </pre>
 * 
 * @author leviputna
 * 
 */
public abstract class EditFeatureAdaptor implements EditFeatureListener {

    @Override
    public void attributeValueBeforeChange(PropertyChangeEvent event) {
        // default behavior is to do nothing.
    }

    @Override
    public void attributeValueChange(PropertyChangeEvent event) {
        // default behavior is to do nothing.
    }

    @Override
    public void attributeStateChange(EditFeatureStateChangeEvent stateChangeEvent) {
        // default behavior is to do nothing.
    }

}
