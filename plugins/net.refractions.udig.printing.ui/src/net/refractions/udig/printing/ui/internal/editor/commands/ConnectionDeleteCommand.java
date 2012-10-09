/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
