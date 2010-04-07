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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;

/**
 * The handler for tool commands.
 * 
 * @author jeichar
 * @since 0.9.0
 */
public class ToolCommandHandler extends AbstractHandler {

    ModalToolCategory category;

    /**
     * Construct <code>ToolCommandHandler</code>.
     */
    public ToolCommandHandler( ModalToolCategory category ) {
        this.category = category;
    }

    /**
     * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    public Object execute( ExecutionEvent event ) {
        if (category.getContribution().isChecked()) {
            category.getContribution().incrementSelection();
        }
        category.getContribution().runCurrentTool();
        return null;
    }

}
