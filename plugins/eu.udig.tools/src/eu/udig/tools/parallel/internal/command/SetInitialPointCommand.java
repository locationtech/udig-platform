/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 *      Diputación Foral de Gipuzkoa, Ordenación Territorial 
 *
 *      http://b5m.gipuzkoa.net
 *      http://www.axios.es 
 *
 * (C) 2006, Diputación Foral de Gipuzkoa, Ordenación Territorial (DFG-OT). 
 * DFG-OT agrees to licence under Lesser General Public License (LGPL).
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package eu.udig.tools.parallel.internal.command;

import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;

import org.eclipse.core.runtime.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;

//import es.axios.udig.ui.editingtools.internal.i18n.Messages;
import eu.udig.tools.internal.i18n.Messages;
//import es.axios.udig.ui.editingtools.precisiontools.commons.internal.PrecisionToolsContext;
//import es.axios.udig.ui.editingtools.precisiontools.commons.internal.PrecisionToolsMode;
import eu.udig.tools.parallel.internal.PrecisionToolsContext;
import eu.udig.tools.parallel.internal.PrecisionToolsMode;

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
