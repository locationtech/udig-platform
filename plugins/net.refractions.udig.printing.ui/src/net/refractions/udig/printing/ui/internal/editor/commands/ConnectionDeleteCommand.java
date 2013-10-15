/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.printing.ui.internal.editor.commands;

import net.refractions.udig.printing.model.Connection;
import net.refractions.udig.printing.ui.internal.Messages;

import org.eclipse.gef.commands.Command;

public class ConnectionDeleteCommand extends Command {

	private Connection connection;

	public ConnectionDeleteCommand(Connection connection) {
		super();
		setLabel(Messages.ConnectionDeleteCommand_label); 
		this.connection = connection;
	}
	public void execute() {
		connection.disconnect();
	}
	public void undo() {
		connection.reconnect();
	}
}
