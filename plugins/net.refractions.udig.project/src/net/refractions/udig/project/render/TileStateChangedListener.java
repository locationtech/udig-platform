/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
