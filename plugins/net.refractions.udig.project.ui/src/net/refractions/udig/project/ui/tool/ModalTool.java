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
 * A ModalTool is a tool which "takes control" of how the Map Editor operates.of operation.
 * <p>
 * An example consider the *Zoom* tool - If the zoom tool is "on" then other tools modal
 * tools must be off.
 * <p>
 * Must have a public default constructor so that the plugin frame work can instantiate the class.
 * </p>
 * Contains the following properties:
 * <ul>
 * <li><b>ToolConstants.DEFAULT_CURSOR_ID_KEY</b> - the cursor ID value from "toolCursorId"
 * attribute of "modalTool" extension</li>
 * 
 * </ul>
 * <p>
 * 
 * @see net.refractions.udig.project.ui.tool.AbstractModalTool
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public interface ModalTool extends Tool {

    /**
     * The default cursor type (gets set if no cursor is defined).
     */
    public static final String DEFAULT_CURSOR = "default"; //$NON-NLS-1$

    /**
     * The crosshair cursor type.
     */
    public static final String CROSSHAIR_CURSOR = "crosshair"; //$NON-NLS-1$

    /**
     * The text cursor type.
     */
    public static final String TEXT_CURSOR = "text"; //$NON-NLS-1$

    /**
     * The wait cursor type.
     */
    public static final String WAIT_CURSOR = "wait"; //$NON-NLS-1$

    /**
     * The south-west-resize cursor type.
     */
    public static final String SW_RESIZE_CURSOR = "sw_resize"; //$NON-NLS-1$

    /**
     * The south-east-resize cursor type.
     */
    public static final String SE_RESIZE_CURSOR = "se_resize"; //$NON-NLS-1$

    /**
     * The north-west-resize cursor type.
     */
    public static final String NW_RESIZE_CURSOR = "nw_resize"; //$NON-NLS-1$

    /**
     * The north-east-resize cursor type.
     */
    public static final String NE_RESIZE_CURSOR = "ne_resize"; //$NON-NLS-1$

    /**
     * The north-resize cursor type.
     */
    public static final String N_RESIZE_CURSOR = "n_resize"; //$NON-NLS-1$

    /**
     * The south-resize cursor type.
     */
    public static final String S_RESIZE_CURSOR = "s_resize"; //$NON-NLS-1$

    /**
     * The west-resize cursor type.
     */
    public static final String W_RESIZE_CURSOR = "w_resize"; //$NON-NLS-1$

    /**
     * The east-resize cursor type.
     */
    public static final String E_RESIZE_CURSOR = "e_resize"; //$NON-NLS-1$

    /**
     * The hand cursor type.
     */
    public static final String HAND_CURSOR = "hand"; //$NON-NLS-1$

    /**
     * The move cursor type.
     */
    public static final String MOVE_CURSOR = "move"; //$NON-NLS-1$
    
    public static final String NO_CURSOR = "no"; //$NON-NLS-1$

    /**
     * Called when tool button is pressed. If active is set to true the tool is Registered with the
     * source Component so that it receives events and will begin operating If active is set to
     * false the tool is set as inactive and deregistered with the component.
     * <p>
     * Here is a sample implementation:
     * <pre><code>    public void setActive( boolean active ) {
     *         this.active=active;
     *         setStatusBarMessage(active);
     *         if (!active) {
     *             deregisterMouseListeners();
     *         } else {
     *             if(isEnabled()){
     *                 registerMouseListeners();
     *             }
     *         }
     *     }</code></pre>
     * Use {@code getContext().getViewportPane() } to obtain the control to listen to.
     * 
     * @param active if true the tool is Registered with the source Component so that it receives
     *        events and will begin operating. if false the tool is set as inactive and deregistered
     *        with the component.
     */
    public void setActive( boolean active );
    
    /**
     * Returns true if the current tool is active.
     *
     * @return
     */
    public boolean isActive( );
    
    /**
     * Returns current cursor ID of the modal tool.
     * 
     * @return
     */
    public String getCursorID();
    
    /**
     * Sets the current cursor ID for the modal tool. If it is needed
     * the actual updating of the mouse cursor is performed automatically.
     * 
     * 
     * @param id the cursor ID from <code>net.refractions.udig.project.ui.tool.toolCursor</code>
     * extension or the constant from <code>ModalTool</code> interface.
     * 
     */
    public void setCursorID(String id);
    
    
    
    /**
     * Sets tool's selection provider.
     * <p>
     * Usually it is configured through extension point.
     * 
     * @param selectionProvider
     */
    public void setSelectionProvider(IMapEditorSelectionProvider selectionProvider);
    
    /**
     * Returns tool's selection provider.
     * <p>
     * Now the modal tool may have 0..1 of selection providers. 
     * 
     * 
     * @return the modal tool's selection provider.
     */
    public IMapEditorSelectionProvider getSelectionProvider();


}
