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
 * You can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software 
 * Foundation; version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package eu.udig.tools.parallel.internal;

import java.util.Observable;

import javax.measure.unit.Unit;

import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.support.EditBlackboard;

import com.vividsolutions.jts.geom.Coordinate;

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
