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
package org.locationtech.udig.project.ui.internal.commands.draw;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;
import org.locationtech.udig.project.ui.commands.AbstractDrawCommand;
import org.locationtech.udig.project.ui.commands.IDrawCommand;
import org.locationtech.udig.ui.graphics.ViewportGraphics;

/**
 * Composite drawing command.
 * <p>
 * Contains several internal drawing commands. Only the composite command
 * is sent to the ViewportPainter.
 * <p>
 * The composite command is responsible for the running all internal
 * commands.
 * 
 * @author Vitalus
 * @since 1.1.0
 * 
 */
public class CompositeDrawCommand extends AbstractDrawCommand {
	
	private List<IDrawCommand> internalCommands = null;

	public CompositeDrawCommand(IDrawCommand[] commandsArray) {

		this.internalCommands = new ArrayList<IDrawCommand>();
		for (int i = 0; i < commandsArray.length; i++) {
			internalCommands.add(commandsArray[i]);
		}
	}
	
	/**
	 * 
	 * @param commandsList list of <code>IDrawCommand</code>s.
	 */
	public CompositeDrawCommand(List<? extends IDrawCommand> commandsList) {
		this.internalCommands = new ArrayList<IDrawCommand>(commandsList);
	}

	public Rectangle getValidArea() {
		return null;
	}
	
	

	@Override
	public void setGraphics(ViewportGraphics graphics, IMapDisplay display) {

		super.setGraphics(graphics, display);
		if(internalCommands != null){
			for (IDrawCommand command : internalCommands) {
				command.setGraphics(graphics, display);
			}
		}
	}

	@Override
	public void setValid(boolean valid) {
		
		super.setValid(valid);
		if(internalCommands != null){
			for (IDrawCommand command : internalCommands) {
				command.setValid(valid);
			}
		}
	}

	/**
	 * 
	 */
	@Override
	public void setMap(IMap map) {
		super.setMap(map);
		if(internalCommands != null){
			for (IDrawCommand command : internalCommands) {
				command.setMap(map);
			}
		}
	}


	/**
	 * 
	 */
	public void run(IProgressMonitor monitor) throws Exception {
		
		for  (IDrawCommand command : internalCommands) {
			try {
				if(command.isValid())
					command.run(monitor);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
