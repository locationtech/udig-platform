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
package eu.udig.tools.parallel.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import net.refractions.udig.project.command.UndoableComposite;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.commands.DeselectEditGeomCommand;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.EditUtils;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.ShapeType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

//import es.axios.udig.ui.editingtools.precisiontools.commons.internal.PrecisionToolsContext;
//import es.axios.udig.ui.editingtools.precisiontools.commons.internal.PrecisionToolsMode;
//import es.axios.udig.ui.editingtools.precisiontools.commons.internal.command.AddCustomVertexCommand;
import eu.udig.tools.parallel.internal.command.AddCustomVertexCommand;

/**
 * 
 * This class will draw the parallel on the blackboard.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * 
 */
public class ParallelPreview implements Observer {

	private ParallelContext					parallelContext	= null;
	private IToolContext					context			= null;
	private EditToolHandler					handler			= null;

	private final static ParallelPreview	THIS			= new ParallelPreview();

	/**
	 * use {@link #getInstance()}
	 */
	private ParallelPreview() {
		// singleton
	}

	/**
	 * 
	 * @return The instance of this class.
	 */
	public static ParallelPreview getInstance() {
		return THIS;
	}

	/**
	 * Set the parameters needed for working with class.
	 * 
	 * @param toolContext
	 * @param editToolHandler
	 * @param parallelContext
	 */
	public void setParameters(IToolContext toolContext, EditToolHandler editToolHandler, ParallelContext parallelContext) {

		this.context = toolContext;
		this.handler = editToolHandler;
		this.parallelContext = parallelContext;
	}

	public void update(Observable o, Object arg) {

		if (PrecisionToolsContext.UPDATE_LAYER.equals(arg) || PrecisionToolsContext.UPDATE_ERROR.equals(arg)) {

			redraw();
		}
	}

	/**
	 * Deletes the previous parallel preview and draws a new one.
	 */
	private void redraw() {

		UndoableComposite composite = new UndoableComposite();

		delete(composite);
		draw(composite);

		composite.setMap(handler.getContext().getMap());
		context.sendASyncCommand(composite);

		handler.getContext().getViewportPane().repaint();
	}

	/**
	 * Deletes the parallel preview or any selected feature.
	 * 
	 * @param composite
	 */
	private void delete(UndoableComposite composite) {

		List<EditGeom> list = new LinkedList<EditGeom>();
		EditBlackboard bb = handler.getCurrentEditBlackboard();
		list.addAll(bb.getGeoms());

		// Deselects the selected painted geometry
		composite.addCommand(new DeselectEditGeomCommand(handler, list));
	}

	/**
	 * Draw the parallel preview. The context have a state which is used to know
	 * when the parallel tool is ready for drawing the preview.
	 * 
	 * @param composite
	 * 
	 */
	public void draw(UndoableComposite composite) {

		// check if it's ready.
		if (!(parallelContext.mode == PrecisionToolsMode.READY)) {
			return;
		}

		EditBlackboard bb = handler.getEditBlackboard(handler.getEditLayer());

		List<Geometry> resultList = parallelContext.getOutputCoordinates();
		for (Geometry result : resultList) {

			Coordinate[] coordinates = result.getCoordinates();
			List<Coordinate> array = new ArrayList<Coordinate>();
			for (Coordinate coord : coordinates) {
				array.add(coord);
			}
			Iterator<Coordinate> coorIt = array.iterator();

			EditGeom newEditGeom = bb.newGeom("", ShapeType.LINE); //$NON-NLS-1$
			PrimitiveShape shape = newEditGeom.getShell();
			handler.setCurrentShape(shape);
			handler.setCurrentState(EditState.MODIFYING);

			Coordinate coor = null;
			while (coorIt.hasNext()) {

				coor = coorIt.next();
				composite.addFinalizerCommand(new AddCustomVertexCommand(handler, bb,
							new EditUtils.EditToolHandlerShapeProvider(handler), coor, shape));
			}
		}
	}

}
