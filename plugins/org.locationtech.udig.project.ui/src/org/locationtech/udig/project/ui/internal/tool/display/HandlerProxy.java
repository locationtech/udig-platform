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
package org.locationtech.udig.project.ui.internal.tool.display;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.project.ui.tool.IToolHandler;
import org.locationtech.udig.project.ui.tool.Tool;

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
         * @see org.locationtech.udig.project.tool.IToolHandler#setTool(org.locationtech.udig.project.tool.Tool)
         */
        public void setTool( Tool tool ) {
            // do nothing
        }

        /**
         * @see org.locationtech.udig.project.tool.IToolHandler#setCurrentCommandId(java.lang.String)
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
