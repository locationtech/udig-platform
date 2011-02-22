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
package net.refractions.udig.tools.edit;

import net.refractions.udig.tools.edit.support.EditGeom;

/**
 * An extension interface allowing an EventBehaviour to Lock the tool handler.
 * <p>
 * Certain types of events need to be able to "lock" the tool handler
 * so that it is the only event getting events until it "unlocks" the tool handler.
 * </p><p>
 * Example:
 * </p><p>
 * The Polygon tool has a BoxSelection behaviour that accepts drag events and draws a
 * rectangle until the mouse is released (at that point it selects the vertices that are with
 * the box).
 * </p><p>
 * The Polygon tool also has a move geometry behaviour that moves the geometry when the mouse
 * is dragged over an {@link EditGeom}.
 * </p><p>
 * Given these two behaviours the following case can occur:  The mouse is pressed (not over a
 * geometry) and the BoxSelection behaviour starts drawing the selection box.  The mouse drags
 * over a Geometry and the MoveGeometry behaviour starts as well.
 * </p><p>
 * One solution is to put both behaviours in a {@link net.refractions.udig.tools.edit.MutualExclusiveEventBehavior} so that if the BoxSelection
 * behaviour is running then the MoveGeometryBehaviour won't.  However if the MoveGeometryBehaviour
 * starts then the BoxSelection behaviour may as well.
 * </p>
 * @author jones
 * @since 1.1.0
 */
public interface LockingBehaviour extends EventBehaviour {
    /**
     * If the object returned by getKey() is the same as the object that the {@link EditToolHandler}
     * has as its lock this object may unlock the EditToolHandler and is also permitted
     * to run.
     * @param handler handler that is calling getKey.
     *
     * @return the object that this behaviour uses as a key.
     */
    Object getKey(EditToolHandler handler);
}
