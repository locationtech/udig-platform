/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.edit.support;

/**
 * Indicates the different ways that snapping can behave.
 * @author Jesse
 * @since 1.1.0
 */
public enum SnapBehaviour {
    /**
     * No snapping
     */
    OFF,
    /**
     * Snap to features selected (on the edit blackboard)
     */
    SELECTED,
    /**
     * Search the current layer and the selected features
     */
    CURRENT_LAYER,
    /**
     * Search all layers
     */
    ALL_LAYERS,
    /**
     * Snap to a grid defined in the preferences
     */
    GRID
}
