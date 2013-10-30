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
