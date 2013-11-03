/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.tool;

import org.eclipse.core.commands.IHandler;

/**
 * A custom command handler.
 * <p>
 * Must have a public default constructor so that the plugin frame work can instantiate the class.
 * </p>
 * 
 * @see org.locationtech.udig.project.ui.tool.AbstractToolCommandHandler
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
