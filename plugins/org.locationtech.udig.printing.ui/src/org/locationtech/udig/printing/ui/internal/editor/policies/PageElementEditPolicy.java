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
package org.locationtech.udig.printing.ui.internal.editor.policies;

import org.locationtech.udig.printing.model.Box;
import org.locationtech.udig.printing.model.Page;
import org.locationtech.udig.printing.ui.internal.editor.commands.DeleteCommand;

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
