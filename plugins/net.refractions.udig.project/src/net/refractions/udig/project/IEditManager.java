/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project;

import org.opengis.feature.simple.SimpleFeature;

/**
 * Defines and implements controllers a map. The EditManager is the central piece and has the
 * following functionality:
 * <ul>
 * <li>Controlling transactions (commit and rollback) used to support data editing</li>
 * <li>Modifying the layer list safely</li>
 *     <ul>
 *     <li>Creating Layer Objects</li>
 *     </ul>
 *     </li>
 * <li>EditFeature support (apply and cancel) on a feature by feature basis used to support forms</li>
 * <li>Recycling FeatureSource instances to be shared between layers</li>
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
