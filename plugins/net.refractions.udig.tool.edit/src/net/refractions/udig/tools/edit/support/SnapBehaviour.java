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
