/* Image Georeferencing
 * 
 * Axios Engineering 
 *      http://www.axios.es 
 *
 * (C) 2011, Axios Engineering S.L. (Axios)
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.udig.image.georeferencing.internal.ui;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;

import org.eclipse.swt.graphics.ImageData;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Coordinate;

import org.locationtech.udig.image.georeferencing.internal.i18n.Messages;
import org.locationtech.udig.image.georeferencing.internal.process.GeoReferencingProcess;
import org.locationtech.udig.image.georeferencing.internal.process.MarkModel;
import org.locationtech.udig.image.georeferencing.internal.process.MarkModelFactory;
import org.locationtech.udig.image.georeferencing.internal.ui.GeoreferencingCommandEventChange.ChangeEvent;
import org.locationtech.udig.image.georeferencing.internal.ui.message.InfoMessage;
import org.locationtech.udig.image.georeferencing.internal.ui.message.InfoMessage.Type;

/**
 * Command used to store all the valuable data used by the composites and the
 * georeferencing process.
 * 
 * It's an {@link Observable} class, and all the composites are {@link Observer}
 * of him.
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.3
 * 
 */
public final class GeoReferencingCommand extends Observable {

	private static final String			DEFAULT_MESSAGE	= Messages.GeoReferencingCommand_defaultMessage;
	private static final String			INITIAL_MESSAGE	= Messages.GeoReferencingCommand_initialMessage;
	private InfoMessage					message			= new InfoMessage(INITIAL_MESSAGE, Type.INFORMATION);
	private String						imagePath		= null;
	private CoordinateReferenceSystem	crsTarget		= null;

	private Map<String, MarkModel>		markList		= new LinkedHashMap<String, MarkModel>();
	private boolean						readyToExecute	= false;
	private ImageData					imageData		= null;
	private String						outputFileName;
	private IMap						originalMap		= null;
	private IMap						currentMap;

	/**
	 * Adds a new mark model to the mark model list and notify the observers
	 * about this event.
	 * 
	 * @param newMark A created mark.
	 */
	public void addMark(MarkModel newMark) {

		markList.put(newMark.getID(), newMark);

		// TODO the command must know who called this. we need 2 methods for
		// adding marks. FUTURE
		this.setMessage(Messages.GeoReferencingCommand_addGrounControlPoint);

		setChanged();
		notifyObservers(new GeoreferencingCommandEventChange(newMark, ChangeEvent.MARK_ADDED));
	}

	/**
	 * Delete the given mark and notify the observers about this event.
	 * 
	 * @param mark
	 *            Mark to be deleted.
	 */
	public void deleteMark(MarkModel mark) {

		mark.delete();
		Object value = markList.remove(mark.getID());
		assert value != null;

		setChanged();
		notifyObservers(new GeoreferencingCommandEventChange(mark, ChangeEvent.MARK_DELETED));
	}

	/**
	 * Delete all marks contained in the list and notify the observers about
	 * this event.
	 */
	public void deleteAllMarks() {

		Collection<MarkModel> collection = markList.values();
		for (MarkModel mark : collection) {
			mark.delete();
		}
		markList.clear();

		MarkModelFactory.resetIdSecuence();
		this.readyToExecute = false;
		this.setMessage(Messages.GeoReferencingCommand_insertMarks);

		setChanged();
		notifyObservers(new GeoreferencingCommandEventChange(ChangeEvent.ALL_MARKS_DELETED));
	}

	/**
	 * Set the path of the image to be georeferenced and notify the observers
	 * about this event.
	 * 
	 * @param imagePath
	 *            The image to be georeferenced.
	 */
	public void setImagePath(String imagePath) {

		assert imagePath != null;
		this.imagePath = imagePath;

		setChanged();
		notifyObservers(new GeoreferencingCommandEventChange(ChangeEvent.IMAGE_LOADED));
	}

	/**
	 * 
	 * @return True if the process can be executed.
	 */
	public boolean canExectue() {

		return this.readyToExecute;
	}

	/**
	 * Evaluate the requirements to be executed the image georeferencing
	 * process.
	 */
	public void evalPrecondition() {

		this.readyToExecute = true;

		// checks there are X mark in the command
		if (markList.size() < 6 || !validateCoordinateData()) {
			this.readyToExecute = false;
		} else if (this.crsTarget == null) {
			this.readyToExecute = false;
		} else if (this.imagePath == null || this.imagePath.equals("")) { //$NON-NLS-1$
			this.readyToExecute = false;
		} else if (this.outputFileName == null || this.outputFileName.equals("")) { //$NON-NLS-1$
			this.readyToExecute = false;
			// if it has already 6 points advice the user that he needs an
			// output file.
			if (markList.size() >= 6) {
				this.setMessage(Messages.GeoReferencingCommand_needOutputFile);
			}
		} else if (this.originalMap == null) {
			this.readyToExecute = false;
		}

		if (this.readyToExecute) {
			this.setMessage(Messages.GeoReferencingCommand_executeOperation);
		}

		setChanged();
		notifyObservers(new GeoreferencingCommandEventChange(ChangeEvent.CAN_EXECUTE));
	}

	/**
	 * Validates that coordinates aren't {@link Double#NaN}.
	 * 
	 * @return True if there isn't any NaN.
	 */
	private boolean validateCoordinateData() {

		Collection<MarkModel> collection = markList.values();
		for (MarkModel mark : collection) {

			if (mark.getXCoord().equals(Double.NaN)) {
				return false;
			}
			if (mark.getYCoord().equals(Double.NaN)) {
				return false;
			}
		}

		return true;
	}

	public void setCRS(CoordinateReferenceSystem currentMapCrs) {

		this.crsTarget = currentMapCrs;
	}

	/**
	 * Set the map used by the process. It also will control when there is set a
	 * different map and notify the observers about this event.
	 * 
	 * @param map
	 */
	public void setMap(IMap map) {

		this.currentMap = map;

		// see if the map has changed.
		if (this.originalMap != null && !this.originalMap.equals(currentMap)) {
			// set enable = false on all the composite, we can't work with a
			// different map.
			this.setMessage(Messages.GeoReferencingCommand_mapChange);
			setChanged();
			notifyObservers(new GeoreferencingCommandEventChange(ChangeEvent.MAP_CHANGE));
		}
		if (this.originalMap != null && this.originalMap.equals(currentMap)) {
			// working again with the original map.
			this.setMessage(DEFAULT_MESSAGE);
			setChanged();
			notifyObservers(new GeoreferencingCommandEventChange(ChangeEvent.MAP_CHANGE_TO_ORIGINAL));
		}

		if (this.originalMap == null) {
			// first time only
			this.setMessage(DEFAULT_MESSAGE);
			this.originalMap = map;
		}
	}

	/**
	 * Launch the georeferencing process.
	 * @throws IOException 
	 */
	public void execute() throws IOException {

		if (!this.readyToExecute) {
			throw new IllegalStateException(Messages.GeoReferencingCommand_cmdNotReady);
		}

		Point2D[] dstCoords = getImagePoints();
		Point2D[] srcCoords = getBasemapPoints();

		// TODO TEST IF THIS STATMENT MAY OR MAY NOT BE WRONG.
		// the best warping results seem to occur when the points are ordered
		// from
		// left to right and up to down, so order them in that fashion as best
		// as possible first.
		int[] order = getSortOrder(dstCoords);
		dstCoords = sortArray(dstCoords, order);
		srcCoords = sortArray(srcCoords, order);

		GeoReferencingProcess process = new GeoReferencingProcess(this.crsTarget, srcCoords, dstCoords, this.imagePath,
					this.outputFileName, this.originalMap);
		
		process.run();

		// FIXME test it, i want to see the newly added layer rendered in uDig.
		for (ILayer layer : originalMap.getMapLayers()) {

			layer.refresh(null);
		}
	}

	/**
	 * Converts the image points into an array of {@link Point2D}.
	 * 
	 * @return The points converted.
	 */
	private Point2D[] getImagePoints() {

		Point2D[] points = new Point2D[markList.size()];
		int index = 0;
		Collection<MarkModel> collection = markList.values();
		for (MarkModel mark : collection) {

			Point fullPoint = new Point(mark.getXImage(), this.imageData.height - mark.getYImage());
			points[index] = fullPoint;
			index++;
		}
		return points;
	}

	/**
	 * Converts the map coordinates into an array of {@link Point2D}.
	 * 
	 * @return The coordinates converted.
	 */
	private Point2D[] getBasemapPoints() {
		Point2D[] points = new Point2D[markList.size()];
		int index = 0;
		Collection<MarkModel> collection = markList.values();
		for (MarkModel mark : collection) {

			Coordinate coord = new Coordinate(mark.getXCoord(), mark.getYCoord());
			points[index] = new Point2D.Double(coord.x, coord.y);
			index++;
		}
		return points;
	}

	/**
	 * Sort the given point array in the order of the provided index array.
	 * 
	 * @param ar
	 *            Points to be short.
	 * @param order
	 *            array
	 */
	private Point2D[] sortArray(Point2D[] ar, int[] order) {
		Point2D[] ordered = new Point2D[ar.length];
		for (int i = 0; i < ar.length; i++) {
			ordered[i] = ar[order[i]];
		}
		return ordered;
	}

	/**
	 * Determine the order the array should be sorted in.
	 * 
	 * @param ar
	 * @return new order array
	 */
	private int[] getSortOrder(Point2D[] ar) {
		Point2D[] sorted = sortPointsX(ar);
		sorted = sortPointsY(sorted);

		// determine the new sort order
		int[] order = new int[ar.length];
		for (int i = 0; i < sorted.length; i++) {
			Point2D point = sorted[i];
			for (int y = 0; y < ar.length; y++) {
				if (point.equals(ar[y])) {
					order[i] = y;
					break;
				}
			}
		}
		return order;
	}

	/**
	 * Sort the point array from "left" to "right" as that seems to give better
	 * results.
	 * 
	 * @param ar
	 * @return sorted array
	 */
	private Point2D[] sortPointsX(Point2D[] ar) {
		Point2D[] sorted = new Point2D[ar.length];
		for (int i = 0; i < ar.length; i++) {
			Point2D point = ar[i];
			for (int y = 0; y < sorted.length; y++) {
				Point2D point2 = sorted[y];
				if (point2 == null) {
					sorted[y] = point;
					break;
				} else {
					if (point2.getX() < point.getX()) {
						for (int z = sorted.length - 1; z > y; z--) {
							sorted[z] = sorted[z - 1];
						}
						sorted[y] = point;
						break;
					}
				}
			}
		}
		return sorted;
	}

	/**
	 * Sort the point array from "top" to "bottom" as that seems to give better
	 * results.
	 * 
	 * @param ar
	 * @return sorted array
	 */
	private Point2D[] sortPointsY(Point2D[] ar) {
		Point2D[] sorted = new Point2D[ar.length];
		for (int i = 0; i < ar.length; i++) {
			Point2D point = ar[i];
			for (int y = 0; y < sorted.length; y++) {
				Point2D point2 = sorted[y];
				if (point2 == null) {
					sorted[y] = point;
					break;
				} else {
					if (point2.getY() < point.getY()) {
						for (int z = sorted.length - 1; z > y; z--) {
							sorted[z] = sorted[z - 1];
						}
						sorted[y] = point;
						break;
					}
				}
			}
		}
		return sorted;
	}

	public void setImageData(ImageData imageData) {

		this.imageData = imageData;
	}

	private void setMessage(String msg) {

		this.message.setText(msg);
	}

	public InfoMessage getMessage() {

		return this.message;
	}

	public void setOutputFileName(String filename) {

		this.outputFileName = filename;
	}

	/**
	 * Used to loads the marks read from the property file.
	 * 
	 * @param loadMarks
	 */
	public void loadMarks(Map<String, MarkModel> loadMarks) {

		this.markList = loadMarks;
	}

	public Map<String, MarkModel> getMarks() {

		return this.markList;
	}

	/**
	 * Check if it's possible to save the current marks.
	 * 
	 * @return True if it can be saved.
	 */
	public boolean canSave() {

		if ((!markList.keySet().isEmpty()) && (this.originalMap != null && this.originalMap.equals(currentMap))) {

			return true;
		}
		return false;
	}

	/**
	 * Check if it's possible to load marks from a property file.
	 * 
	 * @return True if it can be done.
	 */
	public boolean canLoad() {

		if ((this.originalMap != null && this.originalMap.equals(currentMap))
					&& (this.imagePath != null && !this.imagePath.equals(""))) { //$NON-NLS-1$
			return true;
		}
		return false;
	}

	public IMap getMap() {

		return this.originalMap;
	}

	public CoordinateReferenceSystem getCRS() {

		return this.crsTarget;
	}

	/**
	 * Check if certain image tools can be enabled. To fulfill this we need that
	 * exists at least 1 point and that we are working on the correct map.
	 * 
	 * @return
	 */
	public boolean canEnableImageTools() {

		return canSave();
	}

	/**
	 * Check if certain map tools can be enabled. To fulfill this at least one
	 * coordinate must have both X y Y values.
	 * 
	 * @return True if a valid coordinate exist.
	 */
	public boolean canEnableMapTools() {

		return canSave() && checkOneCoordinateExist();
	}

	/**
	 * Check that at least one valid coordinate exist.
	 * 
	 * @return True if one coordinate has both X and Y values.
	 */
	private boolean checkOneCoordinateExist() {

		Collection<MarkModel> collection = markList.values();
		for (MarkModel mark : collection) {

			if (!mark.getXCoord().equals(Double.NaN) && !mark.getYCoord().equals(Double.NaN)) {
				return true;
			}
		}

		return false;
	}

}
