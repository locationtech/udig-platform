/*******************************************************************************
 * Copyright (c) 2006,2012,2013 County Council of Gipuzkoa, Department of Environment
 *                              and Planning and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Aritz Davila (Axios) - initial API, implementation, and documentation
 *    Mauricio Pazos (Axios) - initial API, implementation, and documentation
 *******************************************************************************/
package org.locationtech.udig.tools.parallel.internal.command;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.core.runtime.IProgressMonitor;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;

//import es.axios.geotools.util.GeoToolsUtils;
import org.locationtech.udig.tools.feature.util.GeoToolsUtils;
//import es.axios.udig.ui.commons.util.MapUtil;
import org.locationtech.udig.tools.internal.ui.util.MapUtil;
//import es.axios.udig.ui.editingtools.internal.i18n.Messages;
import org.locationtech.udig.tools.internal.i18n.Messages;
//import es.axios.udig.ui.editingtools.precisionparallels.internal.ParallelContext;
import org.locationtech.udig.tools.parallel.internal.FeatureHighLight;
import org.locationtech.udig.tools.parallel.internal.ParallelContext;
import org.locationtech.udig.tools.parallel.internal.PrecisionToolsMode;
import org.locationtech.udig.tools.parallel.internal.PrecisionToolsUtil;
//import es.axios.udig.ui.editingtools.precisiontools.commons.internal.FeatureHighLight;
//import es.axios.udig.ui.editingtools.precisiontools.commons.internal.PrecisionToolsMode;
//import es.axios.udig.ui.editingtools.precisiontools.commons.internal.PrecisionToolsUtil;

import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.ui.AnimationUpdater;
import org.locationtech.udig.project.ui.commands.IDrawCommand;
import org.locationtech.udig.project.ui.internal.commands.draw.DrawFeatureCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.Point;

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
