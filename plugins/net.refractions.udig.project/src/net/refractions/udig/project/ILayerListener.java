/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project;

/**
 * Listens to changes on this layer.
 * <p>
 * This method is similar, but not identical to adding an "adapter" the the layer. The only reason
 * this may not be identical is the presense of LayerDecorator, when working with a wrapped layer
 * changes that take place only on the decorator will not be available via EMF adapaters.
 * </p>
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Provide callbacks when layer modifications occur
 * <li>The LayerEvent can be used to communicate the nature of the change
 * </ul>
 * </p>
 * 
 * @author Jody Garnett
 * @since 0.9.0
 */
public interface ILayerListener {
    /**
     * Called after a layer modification takes place.
     * <p>
     * The provided LayerEvent contains some details on the modification.
     * <ul>
     * <li>
     * </ul>
     * </p>
     * 
     * @param event
     */
    void refresh( LayerEvent event );
}
