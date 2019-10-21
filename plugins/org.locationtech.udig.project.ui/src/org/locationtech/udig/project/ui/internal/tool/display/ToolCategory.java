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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.tool.IToolManager;

/**
 * Representation of a category, this is a UI construct.
 * 
 * @author jeichar
 * @since 0.9.0
 */
public abstract class ToolCategory implements Iterable<ModalItem> {
    /** The list of items in the category */
    public List<ModalItem> items = new LinkedList<ModalItem>();

    /** The tool manager instance */
    protected IToolManager manager;
    /** id of the category */
    protected String id;
    /** name of the category */
    protected String name;
    private String commandId;
    private ImageDescriptor icon;

    private boolean handlersSet = false;

    protected IConfigurationElement element;

    /**
     * Construct <code>ToolCategory</code>.
     * 
     * @param element the configurationelement that declares the category
     * @param manager the containing manager
     */
    protected ToolCategory( IConfigurationElement element, IToolManager manager ) {
        id = element.getAttribute("id"); //$NON-NLS-1$
        commandId = element.getAttribute("commandId"); //$NON-NLS-1$
        name = element.getAttribute("name"); //$NON-NLS-1$
        String iconPath = element.getAttribute("icon"); //$NON-NLS-1$
        if (iconPath != null)
            icon = AbstractUIPlugin.imageDescriptorFromPlugin(element.getNamespace(), iconPath);
        this.element=element;
        this.manager = manager;
    }

    /**
     * Construct <code>ToolCategory2</code>.
     * 
     * @param manager
     */
    public ToolCategory( IToolManager manager ) {
        this.manager = manager;
        id = Messages.ToolCategory_other; 
        name = Messages.ToolCategory_other_menu; 
        icon = null;
        commandId = null;
    }

    public void dispose( IActionBars bars ) {
        for( Iterator<ModalItem> iter = items.iterator(); iter.hasNext(); ) {
            ModalItem item = iter.next();
            List<CurrentContributionItem> contributions = item.getContributions();
            for( CurrentContributionItem item2 : contributions ) {
                bars.getMenuManager().remove(item2);
                bars.getToolBarManager().remove(item2);
                bars.getStatusLineManager().remove(item2);
            }
            item.clearContributions();
        }
    }

    /**
     * Add an item to the category
     * 
     * @param item the new item
     */
    public void add( ModalItem item ) {
        items.add(item);
    }

    /**
     * @return an iterator that iterates through the items in the category
     */
    public Iterator<ModalItem> iterator() {
        return items.iterator();
    }

    /**
     * Sets the commandHandler for this category.
     * @param ids 
     */
    public void setCommandHandlers( ICommandService service ) {

        if (!handlersSet) {
            if (commandId != null) {
                Command command = service.getCommand(commandId);
                if (command != null && getHandler() != null)
                    command.setHandler(getHandler());
            }
            handlersSet = true;
        }
    }

    /**
     * Gets the command handler for the category.
     * 
     * @return the command handler for the category.
     */
    protected abstract IHandler getHandler();

    /**
     * Gets the icon for the category.
     * 
     * @return the icon for the category.
     */
    public ImageDescriptor getIcon() {
        return icon;
    }
    /**
     * Returns the id of the category
     * 
     * @return the id of the category
     */
    public String getId() {
        return id;
    }
    /**
     * Returns the name of the category
     * 
     * @return the name of the category
     */
    public String getName() {
        return name;
    }
}
