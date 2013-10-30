/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
