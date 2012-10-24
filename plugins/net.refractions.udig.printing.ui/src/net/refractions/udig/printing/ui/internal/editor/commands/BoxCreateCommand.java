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

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.Page;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

public class BoxCreateCommand extends Command {
	private Box newBox;
	private Page parent; //page to add box to
	private Rectangle bounds;
	public BoxCreateCommand(Box box, Page parent, Rectangle bounds) {
		super();
		this.bounds = bounds;
		newBox = box;
		this.parent = parent;
		setLabel("box creation");
	}
	
	public boolean canExecute() {
		return newBox != null && parent != null && bounds != null;
	}
	
	public void execute() {
		newBox.setLocation(bounds.getLocation());
		Dimension size = bounds.getSize();
		if (size.width > 0 && size.height > 0)
			newBox.setSize(size);
		redo();
	}
	
	public void redo() {
		parent.getBoxes().add(newBox);
	}
	
	public void undo() {
		parent.getBoxes().remove(newBox);
	}
}
