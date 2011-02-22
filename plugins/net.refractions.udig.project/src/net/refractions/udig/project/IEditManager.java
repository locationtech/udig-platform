/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project;

import org.geotools.feature.Feature;

/**
 * Defines and implements controllers a map. The EditManager is the central piece and has the
 * following functionality:
 * <ul>
 * <li>Controlling transactions</li>
 * <li>Modifying ContextModel</li>
 * <li>Creating Layer Objects</li>
 * </ul>
 * <p>
 * Transactions and locks are also part of map core.
 * </p>
 *
 * @author jeichar
 * @since 0.1
 */
public interface IEditManager {

    /**
     * Returns the map this EditManager is associated with
     *
     * @return the map this EditManager is associated with
     */
    public IMap getMap();

    /**
     * Gets the Feature that that is currently being edited.
     * <p>
     * Returns null if there is currently no edit feature. This is different from the current
     * selection. Each layer has a selection that may contain many features but there is only only
     * feature that can be editted at one time.
     * </p>
     *
     * @return the Feature that that is currently being edited.
     */
    public Feature getEditFeature();

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
