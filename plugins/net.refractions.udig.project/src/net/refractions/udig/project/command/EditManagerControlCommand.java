/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.command;

import net.refractions.udig.project.internal.EditManager;

/**
 * API comments please ... Provides ...TODO summary sentence
 * <p>
 * TODO Description
 * </p>
 * <p>
 * Responsibilities:
 * <ul>
 * <li>
 * <li>
 * </ul>
 * </p>
 * <p>
 * Example Use:
 * 
 * <pre><code>
 *    
 *     LayerManagerControlCommand x = new LayerManagerControlCommand( ... );
 *     TODO code example
 *     
 * </code></pre>
 * 
 * </p>
 * 
 * @author jeichar
 * @deprecated
 * @since 0.3
 */
public interface EditManagerControlCommand extends MapCommand {

    /**
     * This is old and shouldn't be used anymore
     * 
     * @param layerManager
     * @deprecated
     */
    public void setLayerManager( EditManager layerManager );

}
