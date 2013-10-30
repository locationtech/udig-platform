/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package net.refractions.udig.project.internal.commands;

import net.refractions.udig.project.command.Command;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.Messages;
import net.refractions.udig.project.internal.command.navigation.AbstractNavCommand;

import org.eclipse.core.runtime.IProgressMonitor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Change the CRS of a map.
 * 
 * @author Jesse
 * @since 1.0.0
 */
public class ChangeCRSCommand extends AbstractNavCommand implements
		UndoableMapCommand {
	private static final String NAME = Messages.ChangeCRSCommand_name;
	private CoordinateReferenceSystem crs;

	/**
	 * @deprecated
	 */
	public ChangeCRSCommand(Map map, CoordinateReferenceSystem crs) {
		this.crs = crs;
	}

	public ChangeCRSCommand( CoordinateReferenceSystem crs) {
		this.crs = crs;
	}

	/**
	 * Each command has a name that is displayed with the undo/redo buttons.
	 * 
	 * @see net.refractions.udig.project.command.MapCommand#getName()
	 */
	public String getName() {
		return NAME;
	}

	@Override
	protected void runImpl(IProgressMonitor monitor) throws Exception {
		monitor.beginTask(NAME, 1);
		model.setCRS(crs);
		monitor.done();
	}

	public Command copy() {
		return new ChangeCRSCommand(null,crs);
	}

}