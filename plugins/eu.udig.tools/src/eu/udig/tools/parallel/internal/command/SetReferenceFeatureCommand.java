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

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.core.runtime.IProgressMonitor;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;

//import es.axios.geotools.util.GeoToolsUtils;
import eu.udig.tools.feature.util.GeoToolsUtils;
//import es.axios.udig.ui.commons.util.MapUtil;
import eu.udig.tools.internal.ui.util.MapUtil;
//import es.axios.udig.ui.editingtools.internal.i18n.Messages;
import eu.udig.tools.internal.i18n.Messages;
//import es.axios.udig.ui.editingtools.precisionparallels.internal.ParallelContext;
import eu.udig.tools.parallel.internal.FeatureHighLight;
import eu.udig.tools.parallel.internal.ParallelContext;
import eu.udig.tools.parallel.internal.PrecisionToolsMode;
import eu.udig.tools.parallel.internal.PrecisionToolsUtil;
//import es.axios.udig.ui.editingtools.precisiontools.commons.internal.FeatureHighLight;
//import es.axios.udig.ui.editingtools.precisiontools.commons.internal.PrecisionToolsMode;
//import es.axios.udig.ui.editingtools.precisiontools.commons.internal.PrecisionToolsUtil;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.AnimationUpdater;
import net.refractions.udig.project.ui.commands.IDrawCommand;
import net.refractions.udig.project.ui.internal.commands.draw.DrawFeatureCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.Point;

/**
 * <p>
 * 
 * <pre>
 * Get the feature that is under the cursor are. Store it on
 * {@link ParallelContext} and run the animation that highlight this feature.
 * </pre>
 * 
 * </p>
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 */
public class SetReferenceFeatureCommand extends AbstractCommand implements UndoableMapCommand {

	private ParallelContext	parallelContext	= null;
	private EditToolHandler	handler			= null;
	private MapMouseEvent	event			= null;

	public SetReferenceFeatureCommand(ParallelContext paralleContext, EditToolHandler handler, MapMouseEvent event) {

		this.parallelContext = paralleContext;
		this.handler = handler;
		this.event = event;
	}

	public String getName() {

		return Messages.PrecisionParallelReferenceFeature;
	}

	public void run(IProgressMonitor monitor) throws Exception {

		SimpleFeature feature = PrecisionToolsUtil.getFeatureUnderCursor(handler, event);
		// at this time, before running this commands it has checked there is a
		// feature under the cursor, so
		// this will return a feature.
		assert feature != null;

		// set map units.
		IMap map = handler.getContext().getMap();
		assert map != null;

		CoordinateReferenceSystem crs = MapUtil.getCRS(map);
		Unit<?> mapUnits = GeoToolsUtils.getDefaultCRSUnit(crs);

		parallelContext.setUnits(mapUnits);

		EditBlackboard bb = handler.getEditBlackboard(handler.getEditLayer());
		Point currPoint = Point.valueOf(event.x, event.y);
		Coordinate coor = bb.toCoord(currPoint);

		// previous line exist, if we change the reference line also need to
		// reset the initial point.
		// needs to store the line before setting the initial point.
		if (this.parallelContext.getReferenceFeature() != null) {

			this.parallelContext.setMode(PrecisionToolsMode.BUSY);
			this.parallelContext.setReferenceFeature(feature, coor);
			this.parallelContext.setInitialCoordinate(null);
		} else {
			this.parallelContext.setReferenceFeature(feature, coor);
		}

		this.parallelContext.setMode(PrecisionToolsMode.WAITING);

		List<IDrawCommand> commands = new ArrayList<IDrawCommand>();
		DrawFeatureCommand drawCmd = new DrawFeatureCommand(feature);
		commands.add(drawCmd);
		FeatureHighLight animation = new FeatureHighLight(commands, new Rectangle());
		AnimationUpdater.runTimer(handler.getContext().getMapDisplay(), animation);
	}

	public void rollback(IProgressMonitor monitor) throws Exception {
		// TODO Auto-generated method stub

	}

}
