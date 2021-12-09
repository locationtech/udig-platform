/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;
import org.locationtech.udig.internal.ui.UDIGDropHandler;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.ui.tool.IMapEditorSelectionProvider;

/**
 * WorkbenchPart that supports map editing (for an example a IViewpart or IEditorPart that has a map
 * in it).
 * <p>
 * This is responsible for allowing collaboration with the Map. for example:
 * <ul>
 * <li>The palette will need access to the current tool;</li>
 * <li>Perhaps get a list of what tools are good for that context.</li>
 * <li>Allow tools to take control of the map display (ie set font and provide a context menu and
 * status line)</li>
 * <li>
 * </ul>
 *
 * @author Jesse, GDavis
 * @since 1.1.0
 * @version 1.3.0
 */
public interface MapPart extends IWorkbenchPart {

    /**
     * Returns the map that this editor edits
     *
     * @return Returns the map that this editor edits
     */
    public abstract Map getMap();

    /**
     * Opens the map's context menu.
     */
    public void openContextMenu();

    /**
     * Used to set the font for the map display
     *
     * @param textArea
     */
    public void setFont(Control textArea);

    /**
     * This is used by tools to advertise their "selection". For example the feature selection tool
     * will often provide a Filter or FeatureCollection.
     *
     * @param selectionProvider
     */
    public void setSelectionProvider(IMapEditorSelectionProvider selectionProvider);

    /**
     * Access to status line manager; used to display messages and provide tool feedback.
     *
     * @return
     */
    IStatusLineManager getStatusLineManager();

    /**
     * Returns the MapSite of this MapPart.
     *
     * @return Returns the MapSite of this MapPart.
     */
    MapSite getMapSite();

    /**
     * Returns the DropHandler from this editor.
     *
     * @return Returns the DropHandler from this editor.
     */
    public UDIGDropHandler getDropHandler();

    /**
     * Returns true if drag and drop is enabled for the MapPart.
     *
     * @return Returns true if drag and drop is enabled for the MapPart.
     */
    boolean isDragging();

    /**
     * Enables or disables dragging (drag and drop) from the map editor.
     *
     * @param isDragging True if drag and drop should be enabled, false otherwise.
     */
    void setDragging(boolean isDragging);

    /**
     * Sets MapPart to dirty mode.
     *
     * @param isDirty True if MapPart is dirty, false otherwise.
     */
    void setDirty(boolean isDirty);
}
