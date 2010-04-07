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
