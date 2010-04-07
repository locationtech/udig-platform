package net.refractions.udig.tools.internal;

import java.awt.Rectangle;

import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.ui.commands.AbstractDrawCommand;
import net.refractions.udig.project.ui.commands.IDrawCommand;
import net.refractions.udig.project.ui.commands.TransformDrawCommand;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author jesse
 */
public class InvalidateCommand extends AbstractDrawCommand implements MapCommand, IDrawCommand {

	private TransformDrawCommand m_toInvalidate;

	public InvalidateCommand(TransformDrawCommand command) {
		m_toInvalidate = command;
	}

	public void run(IProgressMonitor monitor) throws Exception {
		m_toInvalidate.setValid(false);
	}

	public Rectangle getValidArea() {
		return null;
	}

}
