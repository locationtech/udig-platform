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

import net.refractions.udig.project.IAbstractContext;
import net.refractions.udig.project.command.Command;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.command.factory.BasicCommandFactory;
import net.refractions.udig.project.command.factory.EditCommandFactory;
import net.refractions.udig.project.command.factory.NavigationCommandFactory;
import net.refractions.udig.project.command.factory.SelectionCommandFactory;
import net.refractions.udig.project.ui.commands.DrawCommandFactory;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IActionBars2;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * A set of tools that is provided to Tool extensions.
 * <p>
 * In addition to the references available in the Toolkit class, ToolContext provides access to
 * command factories and and to sendCommand methods.
 * </p>
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Provide access to the objects that an extension can use for its operations.</li>
 * <li>Provide convenience methods for extension developers to use.</li>
 * <li>Provide a consistent interface for extensions which will not easily change in future
 * versions</li>
 * </ul>
 * </p>
 * 
 * @author Jesse
 * @since 0.5
 */
public interface IToolContext extends IAbstractContext {
    /**
     * Casts getDisplay to ViewportPane;
     * 
     * @return getDisplay cast to ViewportPane
     * @see ViewportPane
     */
    public ViewportPane getViewportPane();

    /**
     * Returns a DrawCommandFactory. Used to create commands that draw on the display.
     * 
     * @return a DrawCommandFactory
     * @see DrawCommandFactory
     */
    public DrawCommandFactory getDrawFactory();

    /**
     * Returns a EditCommandFactory. Used to create commands that edit the data model.
     * 
     * @return a EditCommandFactory
     * @see EditCommandFactory
     */
    public EditCommandFactory getEditFactory();

    /**
     * Returns a NavigationCommandFactory. Used to create commands that change the current view of
     * the map.
     * 
     * @return a NavigationCommandFactory
     * @see NavigationCommandFactory
     * @deprecated Please use navigation commands directly
     */
    public NavigationCommandFactory getNavigationFactory();

    /**
     * Returns a SelectionCommandFactory. Used to create commands that changes the current
     * selection.
     * 
     * @return a SelectionCommandFactory
     */
    public SelectionCommandFactory getSelectionFactory();
    /**
     * Returns a BasicCommandFactory. 
     * 
     * @return a BasicCommandFactory
     */
    public BasicCommandFactory getBasicCommandFactory();

    /**
     * Dispatches a command.  If the command is a IDrawCommand the command will
     * be added to the ViewportPane and the ViewportPane will be refreshed.
     * 
     * @param command The command to execute.
     * @see MapCommand
     */
    public void sendASyncCommand( Command command );

    /**
     * Dispatches a command and blocks until the command has executed.
     * 
     * @param command The command to execute.
     * @see MapCommand
     */
    public void sendSyncCommand( Command command );
    
    /**
     * Gets an instance of the status bar from the current editor or null if there is no editor
     * open.
     * 
     * @return an instance of the status bar from the current editor or null if there is no editor
     *         open.
     * @deprecated use getActionBars().getStatusLineManager()
     */
    IStatusLineManager getStatusBar();

    /**
     * Gets an instance of the ActionsBars from the current editor or null if there is no editor
     * open.
     * 
     * @return an instance of the ActionsBars from the current editor or null if there is no editor
     *         open.
     */
    IActionBars2 getActionBars();
    
    /**
     * Run a code block in the UI thread. This method should always be used when modifying the ui.
     * 
     * @param runnable the code block to execute in the ui thread.
     */
    public void updateUI( Runnable runnable );

    public IToolContext copy();

    /**
     * Calculates the best zoom level based on the Preferred Zoom levels  in ViewportModel.  
     * As recommended in the API the preferred zoom levels are used only if they are not defaults.
     * 
     * That behaviour can be overridden by setting alwayUsePreferredZoomLevels to be true 
     * 
     * @param previousZoom the value of the previousZoom level.  1 is no zoom.  This is required for incremental zooming
     * @param zoomChange the difference between the previousZoom level and the new desiredZoom
     * @param fixedPoint the zoom center
     * @param alwayUsePreferredZoomLevels true to always used preferred zoom levels even if they are the defaults
     * @param alwaysChangeZoom make sure the zoom always changes useful for zoom in buttons
     * 
     * @return the best zoom level to use
     */
	public double calculateZoomLevel(double previousZoom, double zoom, Coordinate fixedPoint, boolean alwayUsePreferredZoomLevels, boolean alwaysChangeZoom);
}