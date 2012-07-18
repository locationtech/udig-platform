/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project;

import org.opengis.feature.simple.SimpleFeature;

/**
 * EditManager acts as the Controler for an open Map responsible for managing the current
 * session.
 * <p>
 * EditManager is responsible for the following functionality:
 *  (both Transaction and any EditFeatures), assiting edit tools (hosting the inter
 * active ediDefines and implements controllers a map. The EditManager is the central piece and has the
 * following functionality:
 * <ul>
 * <li>Managing the current session used for editing:
 *   <ul>
 *   <li>Current transactions (used for commit and rollback)</li>
 *   <li>EditFeature support (apply and cancel) on a feature by feature basis used to support forms</li>
 *   <li>Recycling FeatureSource instances to be shared between layers and operations</li>
 *   <li>Pending: Any support for Security or FeatureLocking will be handled here</li>
 *   </ul>
 * </li>
 * <li>Concurrent modification: Holds a queue of commands to support the safe modification of the Map when the
 * data structure is not in use. As an example you can use a command to modify the layer list, when the map is not being used
 * for rendering.</li>
 * </ul>
 * </p>
 * 
 * @author jeichar
 * @since 0.1
 * @version 3.2
 */
public interface IEditManager {

    /**
     * Returns the map this EditManager is associated with
     * 
     * @return the map this EditManager is associated with
     */
    public IMap getMap();

    /**
     * Gets the SimpleFeature that that is currently being edited.
     * <p>
     * Returns null if there is currently no edit feature. This is different from the current
     * selection. Each layer has a selection that may contain many features but there is only only
     * feature that can be editted at one time.
     * </p>
     * 
     * @return the SimpleFeature that that is currently being edited.
     */
    public SimpleFeature getEditFeature();

    /**
     * The layer that contains the edit features. Often feature edit commands will require the layer
     * that contains the Edit feature so that the feature may be editted.
     * 
     * @return the layer that contains the edit feature in its feature store.
     */
    public ILayer getEditLayer();
    
    /**
     * Indicates whether the editlayer can be changed.
     * 
     * @return true if the current editlayer is locked and cannot be changed.
     * @model 
     */
    public boolean isEditLayerLocked();

    /**
     * Returns the layer that is "currently" selected. The workbench is monitored for changes to
     * selection and this will reflect that layer.
     * 
     * @return the layer that is "currently" selected
     */
    public ILayer getSelectedLayer();

    /**
     * Returns true if there is a currently active transaction (editing has occured and not been
     * committed). false is returned if there is no current transaction. If false is returned it is
     * understood that no editing is occurring.
     * 
     * @return Returns true if there is a currently active transaction. false is returned if there
     *         is no current transaction.
     */
    public boolean isEditing();
    /**
     * Adds a EditManager Listener
     * 
     * @param listener the new listener.
     */
    public void addListener( IEditManagerListener listener );
    /**
     * Removes a EditManager Listener
     * 
     * @param listener the listener to remove
     */
    public void removeListener( IEditManagerListener listener );
    
    
    /**
     * Checks containment of the spesified EditManager listener in the
     * list of already existing listeners.
     * 
     * @param listener
     * @return
     */
    public boolean containsListener( IEditManagerListener listener);
}
