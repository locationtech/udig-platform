/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.command;

import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.Map;

/**
 * A command specific to modifying the state of a map.
 * <p>
 * This is the only way to get write access to a Map. You need to submit your command to the Map
 * (and the framework will carefully schedule the update in between screen redraws etc...).
 * </p>
 * Example:
 *
 * <pre>
 * </code>
 * map.sendCommandSync(new AbstractCommand() {
 *  public void run(IProgressMonitor monitor) throws Exception {
 *      // us getMap()
 *      getMap().getContextModel().lowerLayer(layer);
 *  }
 * });
 * </code>
 * </pre>
 *
 * @author Jesse
 * @since 1.0.0
 */
public interface MapCommand extends Command {

    /**
     * Called when before the command is executed. API mutable? Throw UnsupportedException?
     *
     * @param map The map executing the command.
     * @uml.property name="map"
     */
    public void setMap(IMap map);

    /**
     * Returns the map if called during execute (or undo command is an undoable command)
     *
     * API mutable?
     *
     * @return the map if called during execute (or undo command is an undoable command)
     */
    public Map getMap();
}
