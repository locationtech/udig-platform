/**
 * 
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
	
	/* (non-Javadoc)
	 * @see org.locationtech.udig.project.command.Command#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor) throws Exception {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see org.locationtech.udig.project.command.Command#getName()
	 */
	public String getName() {
		return null;
	}

	public void rollback(IProgressMonitor monitor) throws Exception {
		getMap().undo();
	}

}
