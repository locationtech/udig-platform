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

/**
 * The general interface for tools interacting with the Map Editor.
 * <p>
 * Must have a public default constructor so that the plug-in frame work can instantiate the class.
 * </p>
 *
 * @see org.locationtech.udig.project.ui.tool.AbstractTool
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public interface Tool {

    /** Tool category for tools that do not modify. Examples are Pan and Zoom */
    public static final String VIEW = "view"; //$NON-NLS-1$
    /** Tool category for tools that modify. Examples are Add Vertex and Add SimpleFeature */
    public static final String EDIT = "edit"; //$NON-NLS-1$
    /** The extension point id for tools */
    public static final String EXTENSION_ID = "org.locationtech.udig.project.ui.tool"; //$NON-NLS-1$

    /**
     * Releases resource, Cursor and image resources possibly.
     */
    public void dispose();

    /**
     * Called each time an eclipse editor is activated. The RenderManager and ViewportPane are those
     * that are associated with the newly actived Eclipse view. Intended to be used if something
     * other just changing the current state happens. if false the tool is set as inactive and
     * deregistered with the component.
     *
     * @param tools The tools that the tool can use in its operations
     * @see IToolContext
     */
    public void setContext( IToolContext tools );

    /**
     * Returns the AbstractContext that a tool can use in its operations.
     *
     * @return the AbstractContext that a tool can use in its operations.
     * @see IToolContext
     */
    public IToolContext getContext();


    /**
     * Returns the property of the particular tool implementation.
     * <p>
     *
     * @param key the property key.
     * @return
     */
    public Object getProperty(String key);


    /**
     * Sets the tool's property value by key.
     *
     * @param key
     * @param value
     */
    public void setProperty(String key, Object value);


    /**
     * Returns enablement statement of the tool.
     *
     * @return
     */
    public boolean isEnabled();

    /**
     * Sets enablement of the tool.
     *
     * @param enable
     */
    public void setEnabled(boolean enable);

    /**
     * Adds listener of tool's lifecycle.
     *
     * @param listener
     */
    public void addListener(ToolLifecycleListener listener);

    /**
     * Removes a listener of tool's lifecycle.
     *
     * @param listener
     */
    public void removeListener(ToolLifecycleListener listener);

    /**
     * Indicates if the button has toggling behaviour.
     *
     * @return toggling behaviour indicator of button.
     */
    public boolean isToggleButton();

    /**
     * Indicates if the button is toggled or not.
     * If the button does not have toggling behaviour, it should always return false.
     *
     * @return current button state.
     */
    public boolean isTogglingButtonEnabled();
}
