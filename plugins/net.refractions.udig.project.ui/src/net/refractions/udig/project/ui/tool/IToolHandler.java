/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.project.ui.tool;

import org.eclipse.core.commands.IHandler;

/**
 * A custom command handler.
 * <p>
 * Must have a public default constructor so that the plugin frame work can instantiate the class.
 * </p>
 * 
 * @see net.refractions.udig.project.ui.tool.AbstractToolCommandHandler
 * @author jeichar
 * @since 0.6.0
 * @see IHandler
 */
public interface IToolHandler extends IHandler {

    /**
     * Called before any of the IHandler methods are called.
     * 
     * @param tool
     */
    void setTool( Tool tool );

    /**
     * Called before any of the IHandler methods are called.
     * 
     * @param currentCommandId
     */
    void setCurrentCommandId( String currentCommandId );
}
