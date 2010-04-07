package net.refractions.udig.printing.ui.internal.editor.commands;

import java.util.Iterator;

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.Connection;
import net.refractions.udig.printing.ui.internal.Messages;

import org.eclipse.gef.commands.Command;

public class ConnectionReconnectCommand extends Command {

	private Box oldTarget;
	private Box oldSource;
	private Box newTarget;
	private Box newSource;
	private Connection connection;

	public ConnectionReconnectCommand(Connection connection) {
		super();
		if (connection == null) {
			throw new IllegalArgumentException(Messages.ConnectionReconnectCommand_error_nullConnection); 
		}
		this.connection = connection;
		this.oldSource = connection.getSource();
		this.oldTarget = connection.getTarget();
	}
	public boolean canExecute() {
		if (newSource != null) {
			return checkSourceReconnection();
		} else if (newTarget != null) {
			return checkTargetReconnection();
		}
		return false;
	}

	private boolean checkSourceReconnection() {
		// connection endpoints must be different Shapes
		if (newSource.equals(oldTarget)) {
			return false;
		}
		// return false, if the connection exists already
		for (Iterator iter = newSource.getSourceConnections().iterator(); iter.hasNext();) {
			Connection conn = (Connection) iter.next();
			// return false if a newSource -> oldTarget connection exists already
			// and it is a different instance than the connection-field
			if (conn.getTarget().equals(oldTarget) &&  !conn.equals(connection)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean checkTargetReconnection() {
		// connection endpoints must be different Shapes
		if (newTarget.equals(oldSource)) {
			return false;
		}
		// return false, if the connection exists already
		for (Iterator iter = newTarget.getTargetConnections().iterator(); iter.hasNext();) {
			Connection conn = (Connection) iter.next();
			// return false if a oldSource -> newTarget connection exists already
			// and it is a differenct instance that the connection-field
			if (conn.getSource().equals(oldSource) && !conn.equals(connection)) {
				return false;
			}
		}
		return true;
	}
	
	public void execute() {
		if (newSource != null) {
			connection.reconnect(newSource, oldTarget);
		}
		else if (newTarget != null) {
			connection.reconnect(oldSource, newTarget);
		}
		else {
			throw new IllegalStateException(Messages.ConnectionReconnectCommand_error_unreacheable); 
		}
	}
	
	public void setNewSource(Box connectionSource) {
		if (connectionSource == null) {
			throw new IllegalArgumentException();
		}
		setLabel(Messages.ConnectionReconnectCommand_label_startpoint); 
		newSource = connectionSource;
		newTarget = null;
	}
	
	public void setNewTarget(Box connectionTarget) {
		if (connectionTarget == null) {
			throw new IllegalArgumentException();
		}
		setLabel(Messages.ConnectionReconnectCommand_label_endpoint); 
		newSource = null;
		newTarget = connectionTarget;
	}
	
	public void undo() {
		connection.reconnect(oldSource, oldTarget);
	}
}
