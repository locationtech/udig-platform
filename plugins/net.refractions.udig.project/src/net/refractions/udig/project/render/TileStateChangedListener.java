/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2008, Refractions Research Inc.
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
package net.refractions.udig.project.render;

/**
 * 
 * A listener for tile state change events.  See  net.refractions.uidg.project.render.Tile.
 * <p>
 *
 * </p>
 * @author Emily Gouge (Refractions Research)
 * @since 1.2.0
 */
public interface TileStateChangedListener {
    /**
     * Called when the screen state of a tile has changed.
     *
     * @param tile
     */
    public void screenStateChanged(Tile tile);
    /**
     * Called when the render state of a tile has changed. 
     *
     * @param tile
     */
    public void renderStateChanged(Tile tile);
    /**
     * Called when the context state of a tile has changed.
     *
     * @param tile
     */
    public void contextStateChanged(Tile tile);
    /**
     * Called when the validation state of a tile has changed.
     *
     * @param tile
     */
    public void validationStateChanged(Tile tile);
    
}
