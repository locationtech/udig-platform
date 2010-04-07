/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.command;

import net.refractions.udig.project.internal.render.ViewportModel;

/**
 * All implementations of NavCommand are used to manipulate the viewport model of the map.  
 * In addition they are send to the Navigation Command Stack rather than the normal command stack
 * for execution.
 * 
 * @author Jesse
 * @since 0.5
 */
public interface NavCommand extends UndoableMapCommand, MapCommand {

    /**
     * Set the viewport model that the command operates on.
     * 
     * @param model
     * @see ViewportModel
     */
    public void setViewportModel( ViewportModel model );
}
