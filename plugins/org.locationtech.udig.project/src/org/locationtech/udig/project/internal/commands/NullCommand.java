/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2021, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.commands;

import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.command.UndoableCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This command does nothing it simply.  If undone it will undo the previous command so it it
 * effectively invisible on the stack
 * @author jeichar
 *
 */
public class NullCommand extends AbstractCommand implements MapCommand, UndoableMapCommand, UndoableCommand {

	public void run(IProgressMonitor monitor) throws Exception {
		// do nothing
	}

	public String getName() {
		return null;
	}

	public void rollback(IProgressMonitor monitor) throws Exception {
		getMap().undo();
	}

}
