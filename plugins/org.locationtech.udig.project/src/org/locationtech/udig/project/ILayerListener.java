/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project;

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
