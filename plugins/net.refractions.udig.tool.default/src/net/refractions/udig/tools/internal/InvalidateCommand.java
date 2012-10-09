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
