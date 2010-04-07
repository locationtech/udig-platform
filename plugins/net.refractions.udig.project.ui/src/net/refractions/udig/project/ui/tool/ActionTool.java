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

/**
 * A tool that performs an action when the tool button is pressed.
 * <p>
 * An example is a zoom to extent button. An action button is not modal. Like other tools an action
 * tool should not be instantiated.
 * </p>
 * <p>
 * Must have a public default constructor.
 * </p>
 * 
 * @see net.refractions.udig.project.ui.tool.AbstractTool
 * @author jeichar
 * @since 0.3
 * @see Tool
 */
public interface ActionTool extends Tool {

    /**
     * Called when tool button is pressed. If false the tool is set as inactive and deregistered
     * with the component.
     */
    public void run();

}
