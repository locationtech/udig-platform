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
package net.refractions.udig.project.ui.internal.tool.display;

import net.refractions.udig.project.ui.internal.ProjectUIPlugin;
import net.refractions.udig.project.ui.tool.IToolHandler;
import net.refractions.udig.project.ui.tool.Tool;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Proxy to allow lazy loading of ToolCommandHandlers
 * 
 * @author jeichar
 * @since 0.6.0
 */
public class HandlerProxy extends AbstractHandler {

    /**
     * If a handler cannot be created for the Tool then this class will
     * 
     * @author jeichar
     * @since 0.6.0
     */
    public static class EmptyHandler extends AbstractHandler implements IToolHandler {

        /**
         * @see net.refractions.udig.project.tool.IToolHandler#setTool(net.refractions.udig.project.tool.Tool)
         */
        public void setTool( Tool tool ) {
            // do nothing
        }

        /**
         * @see net.refractions.udig.project.tool.IToolHandler#setCurrentCommandId(java.lang.String)
         */
        public void setCurrentCommandId( String currentCommandId ) {
            // do nothing.
        }

        /**
         * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
         */
        public Object execute( ExecutionEvent event ) {
            return null;
        }

    }
    private volatile IToolHandler instance;
    private IConfigurationElement toolElement;
    private ToolProxy tool;
    /** the id of the extension attribute */
    public static final String ID = "commandHandler"; //$NON-NLS-1$
    private final String commandId;

    /**
     * Construct <code>HandlerProxy</code>.
     * 
     * @param toolElement
     * @param tool
     */
    public HandlerProxy( IConfigurationElement toolElement, ToolProxy tool, String commandId ) {
        this.toolElement = toolElement;
        this.tool = tool;
        this.commandId = commandId;
    }

    private IToolHandler getToolHandler() {
        synchronized (this) {
            if (instance == null) {
                try {
                    instance = (IToolHandler) toolElement.createExecutableExtension(ID);
                    instance.setTool(tool.getTool());
                } catch (CoreException e) {
                    ProjectUIPlugin.log(null, e);
                    instance = new EmptyHandler();
                }
            }
            instance.setCurrentCommandId(commandId);
        }
        return instance;
    }
    // /**
    // * @see
    // org.eclipse.ui.commands.IHandler#addHandlerListener(org.eclipse.ui.commands.IHandlerListener)
    // */
    // public void addHandlerListener( IHandlerListener handlerListener ) {
    // getToolHandler().addHandlerListener(handlerListener);
    // }

    /**
     * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    public Object execute( ExecutionEvent event ) throws ExecutionException {
        return getToolHandler().execute(event);
    }

}
