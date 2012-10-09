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

import java.util.Iterator;

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.Connection;
import net.refractions.udig.printing.model.ModelFactory;
import net.refractions.udig.printing.ui.internal.Messages;

import org.eclipse.gef.commands.Command;

public class ConnectionCreateCommand extends Command {

	private Box source;
	private Box target;
	private Connection connection;

	public ConnectionCreateCommand(Box source) {
		super();
		if (source == null) {
			throw new IllegalArgumentException(Messages.ConnectionCreateCommand_error_sourceNull); 
		}
		this.source = source;
	}
	
	public boolean canExecute() {
		if (source.equals(target)) {
			return false;
		}
		Iterator iter = source.getSourceConnections().iterator();
		while(iter.hasNext()) {
			Connection connection = (Connection) iter.next();
			
			if (connection.getTarget().equals(target)) {
				return false;
			}
		}
		return true;
	}
	public void execute() {
		connection = ModelFactory.eINSTANCE.createConnection();
		connection.setSource(source);
		connection.setTarget(target);
	}
	public void redo() {
		connection.reconnect();
	}
	public void setTarget(Box target) {
		if (target == null) {
			throw new IllegalArgumentException(Messages.ConnectionCreateCommand_error_targetNull); 
		}
		this.target = target;
	}
		
	public void undo() {
		connection.disconnect();
	}
}
