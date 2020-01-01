/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 * (C) 2006, Axios Engineering S.L. (Axios)
 * (C) 2006, County Council of Gipuzkoa, Department of Environment and Planning
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.udig.tools.parallel.internal.command;

import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;

import org.eclipse.core.runtime.IProgressMonitor;

import org.locationtech.jts.geom.Coordinate;

//import es.axios.udig.ui.editingtools.internal.i18n.Messages;
import org.locationtech.udig.tools.internal.i18n.Messages;
//import es.axios.udig.ui.editingtools.precisiontools.commons.internal.PrecisionToolsContext;
//import es.axios.udig.ui.editingtools.precisiontools.commons.internal.PrecisionToolsMode;
import org.locationtech.udig.tools.parallel.internal.PrecisionToolsContext;
import org.locationtech.udig.tools.parallel.internal.PrecisionToolsMode;

/**
 * Sets the initial point into the {@link PrecisionToolsContext}
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * 
 */
public class SetInitialPointCommand extends AbstractCommand implements UndoableMapCommand {

	private PrecisionToolsContext	toolContext	= null;
	private Coordinate				coordinate	= null;

	public SetInitialPointCommand(PrecisionToolsContext toolContext, Coordinate coor) {

		this.toolContext = toolContext;
		this.coordinate = coor;
	}

	public String getName() {

		return Messages.SetInitialPointCommand;
	}

	public void run(IProgressMonitor monitor) throws Exception {

		// set the state before setting the initial point because, when point is
		// set it will automatically update.
		this.toolContext.setMode(PrecisionToolsMode.READY);
		this.toolContext.setInitialCoordinate(this.coordinate);
	}

	public void rollback(IProgressMonitor monitor) throws Exception {
		//TODO
	}

}
