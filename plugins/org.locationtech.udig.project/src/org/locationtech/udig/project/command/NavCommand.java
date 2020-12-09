/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.command;

import org.locationtech.udig.project.internal.render.ViewportModel;

/**
 * All implementations of NavCommand are used to manipulate the viewport model of the map.
 * In addition they are send to the Navigation Command Stack rather than the normal command stack
 * for execution.
 *
 * @author Jesse
 * @since 0.5
 */
public interface NavCommand extends MapCommand {

    /**
     * Set the viewport model that the command operates on.
     *
     * @param model
     * @see ViewportModel
     */
    public void setViewportModel( ViewportModel model );
}
