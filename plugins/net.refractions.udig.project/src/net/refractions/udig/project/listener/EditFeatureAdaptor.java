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
package net.refractions.udig.project.listener;

import java.beans.PropertyChangeEvent;

import net.refractions.udig.project.EditFeature;

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
