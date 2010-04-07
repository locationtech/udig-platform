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
package net.refractions.udig.printing.ui.internal.editor.policies;

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.Page;
import net.refractions.udig.printing.ui.internal.editor.commands.DeleteCommand;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

/**
 * Provides ...TODO summary sentence
 * <p>
 * TODO Description
 * </p><p>
 * Responsibilities:
 * <ul>
 * <li>
 * <li>
 * </ul>
 * </p><p>
 * Example Use:<pre><code>
 * PageElementEditPolicy x = new PageElementEditPolicy( ... );
 * TODO code example
 * </code></pre>
 * </p>
 * @author Richard Gould
 * @since 0.3
 */
public class PageElementEditPolicy extends ComponentEditPolicy {

    protected Command createDeleteCommand( GroupRequest deleteRequest ) {
    	Object parent = getHost().getParent().getModel();
    	DeleteCommand deleteCmd = new DeleteCommand();
    	deleteCmd.setParent((Page)parent);
    	deleteCmd.setChild((Box)getHost().getModel());
    	return deleteCmd;
    }
}
