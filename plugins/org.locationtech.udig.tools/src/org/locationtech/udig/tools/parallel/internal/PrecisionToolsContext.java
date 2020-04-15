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
package org.locationtech.udig.tools.parallel.internal;

import java.util.Observable;

import javax.measure.Unit;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.support.EditBlackboard;

/**
 * Abstract class for store the data of the precision tools.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 */
public abstract class PrecisionToolsContext extends Observable {

	public static final String	UPDATE_LAYER		= "UPDATE_LAYER";				//$NON-NLS-1$
	public static final String	UPDATE_VIEW			= "UPDATE_VIEW";				//$NON-NLS-1$
	public static final String	UPDATE_ERROR		= "UPDATE_ERROR";				//$NON-NLS-1$

	public PrecisionToolsMode	mode				= null;
	public PrecisionToolsMode	previousMode		= PrecisionToolsMode.WAITING;
	protected Coordinate		initialCoordinate	= null;

	protected Unit<?>			units				= null;
	protected Double			length				= null;
	protected EditBlackboard	bb					= null;
	protected double			distanceCoorX		= 0;
	protected double			distanceCoorY		= 0;
	protected Coordinate		referenceCoor		= null;
	protected boolean			reverse				= false;

	protected EditToolHandler	handler				= null;

	/**
	 * Set the edit tool handler used by the tool.
	 * 
	 * @param handler
	 */
	public void setHandler(EditToolHandler handler) {

		this.handler = handler;
	}

	public abstract void initContext();

	/**
	 * When context is changed, call its observer for updating.
	 * 
	 * @param update
	 */
	public void update(String update) {

		setChanged();
		notifyObservers(update);
	}

	/**
	 * Set the mode and the previous mode of the tool.
	 * 
	 * @param mode
	 */
	public void setMode(PrecisionToolsMode mode) {

		assert mode != null;

		this.previousMode = this.mode;
		this.mode = mode;
	}

	/**
	 * Get the map units.
	 * 
	 * @return
	 */
	public synchronized Unit<?> getUnits() {

		return units;
	}

	/**
	 * Set the map units.
	 * 
	 * @param units
	 */
	public synchronized void setUnits(Unit<?> units) {

		this.units = units;
	}

	/**
	 * Get the length of the new parallel line.
	 * 
	 * @return
	 */
	public synchronized Double getLength() {

		return length;
	}

	/**
	 * Set the length of the new parallel line.
	 * 
	 * @param length
	 */
	public synchronized void setLength(Double length) {

		this.length = length;
	}

	/**
	 * Set the initial coordinate.
	 * 
	 * @param coordinate
	 */
	public synchronized void setInitialCoordinate(Coordinate coordinate) {

		this.initialCoordinate = coordinate;
		update(UPDATE_LAYER);
	}

	/**
	 * Get the initial coordinate.
	 */
	public synchronized Coordinate getInitialCoordinate() {

		return this.initialCoordinate;
	}

	/**
	 * Set the edit blackboard. Is needed for transforming points to
	 * coordinates.
	 * 
	 * @param editBlackboard
	 */
	public synchronized void setEditBlackBoard(EditBlackboard editBlackboard) {

		this.bb = editBlackboard;
	}

	/**
	 * Get the distance for the coordinate X
	 * 
	 * @return
	 */
	public synchronized double getDistanceCoorX() {

		return distanceCoorX;
	}

	/**
	 * Set the distance for the coordinate X
	 * 
	 * @param dist
	 */
	public synchronized void setDistanceCoorX(double dist) {

		this.distanceCoorX = dist;
	}

	/**
	 * Set the distance for the coordinate Y
	 * 
	 * @param dist
	 */
	public synchronized void setDistanceCoorY(double dist) {

		this.distanceCoorY = dist;
	}

	/**
	 * Get the distance for the coordinate Y
	 * 
	 * @return
	 */
	public synchronized double getDistanceCoorY() {

		return distanceCoorY;
	}

	/**
	 * Get the reference coordinate.
	 * 
	 * @return
	 */
	public Coordinate getReferenceCoordinate() {

		return this.referenceCoor;
	}

	/**
	 * Get the actual mode.
	 * 
	 * @return
	 */
	public PrecisionToolsMode getMode() {

		return this.mode;
	}

	/**
	 * Get the previous mode.
	 * 
	 * @return
	 */
	public PrecisionToolsMode getPreviousMode() {

		return this.previousMode;
	}

	/**
	 * Get the edit tool handler.
	 * 
	 * @return
	 */
	public EditToolHandler getHandler() {

		return handler;
	}
}
