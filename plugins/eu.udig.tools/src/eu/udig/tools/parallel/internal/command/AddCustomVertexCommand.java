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

import net.refractions.udig.core.IBlockingProvider;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.AnimationUpdater;
import net.refractions.udig.project.ui.IAnimation;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.animation.AddVertexAnimation;
import net.refractions.udig.tools.edit.animation.DeleteVertexAnimation;
import net.refractions.udig.tools.edit.commands.AddVertexCommand;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;

import org.eclipse.core.runtime.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Add a vertex to the current {@link EditGeom} with the characteristic that
 * never will had a vertex if ones exist at the same coordinate. Collaboration:
 * {@link AddVertexCommand}.
 * 
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 */
public class AddCustomVertexCommand extends AddVertexCommand implements UndoableMapCommand {

	/** Coordinate as generated with toCoordinate( point, useSnapping) */
	private Coordinate							toAdd			= null;
	private Point								point;
	private Coordinate							addedCoord;
	private final EditBlackboard				board;
	private IBlockingProvider<PrimitiveShape>	shapeProvider;
	private EditToolHandler						handler;
	private int									index;
	private boolean								showAnimation	= true;
	private PrimitiveShape						shape			= null;

	public AddCustomVertexCommand(	EditToolHandler handler2,
									EditBlackboard bb,
									IBlockingProvider<PrimitiveShape> provider,
									Coordinate coordinate,
									PrimitiveShape shape) {
		super(handler2, bb, provider, null, false);

		this.handler = handler2;
		board = bb;
		shapeProvider = provider;
		this.toAdd = coordinate;
		this.point = board.toPoint(toAdd);
		this.shape = shape;
	}

	@Override
	public String getName() {
		return Messages.AddVertexCommand_name + toAdd;
	}

	@Override
	public void run(IProgressMonitor monitor) throws Exception {
		PrimitiveShape shape = this.shape;
		handler.setCurrentShape(shape);
		boolean collapseVertices = board.isCollapseVertices();
		try {
			board.setCollapseVertices(false);
			board.addCoordinate(toAdd, shape);
			addedCoord = toAdd;
			index = shape.getNumPoints() - 1;
		} finally {
			board.setCollapseVertices(collapseVertices);
		}
		if (handler.getContext().getMapDisplay() != null && showAnimation) {
			IAnimation animation = new AddVertexAnimation(point.getX(), point.getY());
			AnimationUpdater.runTimer(handler.getContext().getMapDisplay(), animation);
		}
	}

	@Override
	public void rollback(IProgressMonitor monitor) throws Exception {
		if (addedCoord == null) {
			return;
		}
		if (handler.getContext().getMapDisplay() != null && showAnimation) {
			IAnimation animation = new DeleteVertexAnimation(point);
			AnimationUpdater.runTimer(handler.getContext().getMapDisplay(), animation);
		}
		board.removeCoordinate(index, addedCoord, shape);
		addedCoord = null;
	}
}
