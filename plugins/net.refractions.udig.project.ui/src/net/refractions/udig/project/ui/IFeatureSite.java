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
package net.refractions.udig.project.ui;

import net.refractions.udig.project.IAbstractContext;
import net.refractions.udig.project.command.Command;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.command.factory.BasicCommandFactory;
import net.refractions.udig.project.command.factory.EditCommandFactory;
import net.refractions.udig.project.command.factory.NavigationCommandFactory;
import net.refractions.udig.project.command.factory.SelectionCommandFactory;
import net.refractions.udig.project.ui.commands.DrawCommandFactory;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars2;
import org.eclipse.ui.IWorkbench;

/**
 * Allows access to the site hosting the IFeaturePanel.
 * 
 * @author Myles
 * @since 1.2
 */
public interface IFeatureSite extends IAbstractContext {
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

    /**
     * Returns the current workbench.
     * <p>
     * Convenience for PlatformUI.getWorkbench()
     * 
     * @return the current workbench.
     */
    IWorkbench getWorkbench();
    /**
     * Returns the default display.
     * <p>
     * Convenience for Display.getDefault()
     * </p>
     * 
     * @return the default display.
     */
    Display getDisplay();
    /**
     * Logs an exception to the current plugin.
     * 
     * @param currentPlugin the plugin that the exception will be logged in.
     * @param message the message to log
     * @param severity the severity of the exception. IF null ERROR will be assumed.
     *        {@linkplain org.eclipse.core.runtime.IStatus#ERROR},
     *        {@linkplain org.eclipse.core.runtime.IStatus#INFO},
     *        {@linkplain org.eclipse.core.runtime.IStatus#WARNING}
     * @param exception the exception to log. Can be null.
     */
    void log( Plugin currentPlugin, String message, int severity, Throwable exception );
    
    public IFeatureSite copy();
}