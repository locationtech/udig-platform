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

import java.util.List;

import org.locationtech.udig.project.command.Command;
import org.locationtech.udig.project.command.MapCommand;

import org.eclipse.emf.common.util.URI;

/**
 * A Project contains Maps and Pages.
 * <p>
 * Provides event notification when something changes. The ProjectRegistry is used to obtain
 * references to Projects.
 * </p>
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Maintain a list of Maps and Pages</li>
 * </ul>
 * </p>
 * <p>
 * Example Use:
 * 
 * <pre><code>
 * Project project = registry.getProject(new URL(&quot;file://home/user/project.udig&quot;));
 * project.getElements();
 * </code></pre>
 * 
 * </p>
 * 
 * @author Jesse
 * @since 0.1
 */
public interface IProject {
    /**
     * Returns an unmodifiable list of the type requested.
     * <p>
     * Some currently valid options are IMap and Page
     */
    public <E> List<E> getElements( Class<E> type );

    /**
     * Returns a List with all elements in the project
     * <p>
     * This is an immutable list
     * </p>
     * 
     * @return a list with all in the project
     */
    public List<IProjectElement> getElements();

    /**
     * @return the name of the project
     */
    public String getName();

    /**
     * Executes the command asynchronously. The commands are not placed in a commandstack so they
     * can not be undone. This allows developers to execute commands such as map creation.
     * <b>NOTICE: this should only be used if {@link IMap#sendCommand(MapCommand)}<b>
     * 
     * @param command
     */
    public void sendASync( Command command );

    /**
     * Executes the command synchronously and blocks. The commands are not placed in a commandstack
     * so they can not be undone. This allows developers to execute commands such as map creation.
     * <b>NOTICE: this should only be used if {@link IMap#sendCommand(MapCommand)}<b>
     * 
     * @param command
     */
    public void sendSync( Command command );
    
    /**
     * The id of the Project.
     *
     */
    URI getID();

}
