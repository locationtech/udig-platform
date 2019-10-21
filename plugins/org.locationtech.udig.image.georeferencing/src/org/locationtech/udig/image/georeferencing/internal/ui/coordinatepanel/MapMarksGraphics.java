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
package org.locationtech.udig.image.georeferencing.internal.ui.coordinatepanel;

import java.awt.Point;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.mapgraphic.MapGraphicContext;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ui.ApplicationGIS;

import org.locationtech.jts.geom.Coordinate;

import org.locationtech.udig.image.georeferencing.internal.process.MarkModel;
import org.locationtech.udig.image.georeferencing.internal.ui.InputEvent;
import org.locationtech.udig.image.georeferencing.internal.ui.MouseSelectionListener;
import org.locationtech.udig.image.georeferencing.internal.ui.imagepanel.ImageComposite;

/**
 * Responsible of drawing the mark collection on the map.
 * 
 * It contains a list of {@link MarkMapPresenterImp}.
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.3
 * 
 */
public final class MapMarksGraphics implements MapGraphic {

	private Map<String, MarkMapPresenter>	markMapPresenterList				= Collections.synchronizedMap(new Hashtable<String, MarkMapPresenter>());

	private MarkMapPreview						preview								= null;
	private Set<MouseSelectionListener>			listeners							= new HashSet<MouseSelectionListener>();

	private MouseSelectionListener				coordinatePanelSelectionListener	= null;
	private MouseSelectionListener				imageSelectionListener				= null;

	/**
	 * Constructor. Creates its listeners.
	 */
	public MapMarksGraphics() {

		this.coordinatePanelSelectionListener = new MouseSelectionListener() {

			public void inEvent(MarkModel mark) {

				showFeedback(mark);
			}

			public void outEvent(MarkModel mark) {

				hideFeedback(mark);
			}
		};

		this.imageSelectionListener = new MouseSelectionListener() {

			public void inEvent(MarkModel mark) {

				showFeedback(mark);
			}

			public void outEvent(MarkModel mark) {

				hideFeedback(mark);
			}
		};
	}

	/**
	 * Adds his listeners to the given composites.
	 * 
	 * @param coordComposite
	 *            {@link CoordinateTableComposite}.
	 * @param imageComposite
	 *            {@link ImageComposite}.
	 */
	public void associateListeners(CoordinateTableComposite coordComposite, ImageComposite imageComposite) {

		coordComposite.addMouseSelectionListener(coordinatePanelSelectionListener);
		imageComposite.addMouseSelectionListener(imageSelectionListener);
	}

	/**
	 * Removes his listeners from the given composites.
	 * 
	 * @param coordComposite
	 *            {@link CoordinateTableComposite}.
	 * @param imageComposite
	 *            {@link ImageComposite}.
	 */
	public void deleteAssociatedListeners(CoordinateTableComposite coordComposite, ImageComposite imageComposite) {

		coordComposite.deleteMouseSelectionListener(coordinatePanelSelectionListener);
		imageComposite.deleteMouseSelectionListener(imageSelectionListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.locationtech.udig.mapgraphic.MapGraphic#draw(org.locationtech.udig.
	 * mapgraphic.MapGraphicContext)
	 */
	public void draw(MapGraphicContext context) {

		Set<Entry<String, MarkMapPresenter>> entrySet = markMapPresenterList.entrySet();
		Iterator<Entry<String, MarkMapPresenter>> iter = entrySet.iterator();
		while (iter.hasNext()) {

			Entry<String, MarkMapPresenter> entry = iter.next();
			MarkMapPresenter presenter = entry.getValue();
			presenter.setContext(context);
			presenter.draw();
		}

		if (preview != null) {
			preview.setContext(context);
			preview.draw();
		}

	}

	/**
	 * Add a {@link MarkMapPresenterImp} to the list.
	 * 
	 * @param markPresenter
	 *            A map presenter.
	 */
	public void addMarkMapPresenter(MarkMapPresenter markPresenter) {

		markMapPresenterList.put(markPresenter.getMarkModel().getID(), markPresenter);
	}

	/**
	 * Clear all the presenters.
	 */
	public void clear() {

		markMapPresenterList.clear();
	}

	/**
	 * Get the {@link MarkMapPresenterImp} list.
	 * 
	 * @return The map list containing the presenters.
	 */
	public Map<String, MarkMapPresenter> getPresenters() {

		return markMapPresenterList;
	}

	/**
	 * <p>
	 * 
	 * <pre>
	 * Manages the D&D event which is divided into 3 parts:
	 * 
	 * -Mouse_down: Find the presenter that is under the given point and creates a preview.
	 * -Mouse_drag: Updates the preview with the current point position.
	 * -Mouse_up: Deletes the preview and update the coordinate of the mark model that has been moved.
	 * 
	 * This method also manages when to show or not the feedback of the presenters.
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param event
	 *            Input event.
	 * @param point
	 *            Point related to the map.
	 */
	public void eventhandler(InputEvent event, Point point) {

		switch (event) {
		case MOUSE_DOWN:

			// see if this event affect any of the presenters
			Set<Entry<String, MarkMapPresenter>> entrySet = markMapPresenterList.entrySet();
			Iterator<Entry<String, MarkMapPresenter>> iter = entrySet.iterator();
			while (iter.hasNext()) {

				Entry<String, MarkMapPresenter> entry = iter.next();
				MarkMapPresenter presenter = entry.getValue();

				if (presenter.eventHandler(event, point.x, point.y)) {
					// create the preview
					this.preview = new MarkMapPreview(presenter);
				}
			}

			break;
		case MOUSE_DRAG:

			if (this.preview != null) {
				this.preview.eventHandler(event, point.x, point.y);
			} else {
				showFeedback(event, point.x, point.y);
			}

			break;
		case MOUSE_UP:

			if (this.preview != null) {

				// TODO do we need to validate?

				IMap map = ApplicationGIS.getActiveMap();
				Coordinate newCoord = map.getViewportModel().pixelToWorld(point.x, point.y);

				this.preview.getMarkModel().updateCoordinatePosition(newCoord);
				this.preview.delete();
				this.preview = null;
			}
			break;
		default:
			break;
		}
	}

	/**
	 * Shows the feedback and broadcast which mark is getting affected by the
	 * feedback to the listeners.
	 * 
	 * @param inputEvent
	 *            Input event.
	 * @param x
	 *            Map position.
	 * @param y
	 *            Map position.
	 */
	public void showFeedback(InputEvent inputEvent, int x, int y) {

		// see if this event affect any of the presenters
		Set<Entry<String, MarkMapPresenter>> entrySet = markMapPresenterList.entrySet();
		Iterator<Entry<String, MarkMapPresenter>> iter = entrySet.iterator();
		while (iter.hasNext()) {

			Entry<String, MarkMapPresenter> entry = iter.next();
			MarkMapPresenter presenter = entry.getValue();

			if (presenter.eventHandler(inputEvent, x, y)) {
				presenter.showSelectedFeedback(true);
				broadcastMouseInEvent(presenter.getMarkModel());
			} else {
				presenter.showSelectedFeedback(false);
				broadcastMouseOutEvent(presenter.getMarkModel());
			}
		}
	}

	/**
	 * Adds a {@link MouseSelectionListener}.
	 * 
	 * @param listener
	 *            The listener.
	 */
	public void addMouseSelectionListener(MouseSelectionListener listener) {

		listeners.add(listener);
	}

	/**
	 * Delete a {@link MouseSelectionListener}.
	 * 
	 * @param listener
	 *            The listener.
	 */
	public void deleteMouseSelectionListener(MouseSelectionListener listener) {

		listeners.remove(listener);
	}

	/**
	 * Broadcast to all the listeners that the given mark is under the mouse
	 * position.
	 * 
	 * @param mark
	 *            The mark model.
	 */
	private void broadcastMouseInEvent(MarkModel mark) {

		for (MouseSelectionListener listener : listeners) {
			listener.inEvent(mark);
		}
	}

	/**
	 * Broadcast to all the listeners that the given mark is not under the mouse
	 * position.
	 * 
	 * @param mark
	 *            The mark model.
	 */
	private void broadcastMouseOutEvent(MarkModel mark) {

		for (MouseSelectionListener listener : listeners) {
			listener.outEvent(mark);
		}
	}

	/**
	 * Find the given mark inside the list of {@link MarkMapPresenterImp}s and
	 * shows the feedback.
	 * 
	 * @param mark
	 *            The mark model.
	 */
	private void showFeedback(MarkModel mark) {

		// find the presenter that contains the given mark.
		Set<Entry<String, MarkMapPresenter>> entrySet = markMapPresenterList.entrySet();
		Iterator<Entry<String, MarkMapPresenter>> iter = entrySet.iterator();
		while (iter.hasNext()) {

			Entry<String, MarkMapPresenter> entry = iter.next();
			MarkMapPresenter presenter = entry.getValue();

			if (presenter.getMarkModel().getID().equals(mark.getID())) {
				presenter.showSelectedFeedback(true);
			} else {
				presenter.showSelectedFeedback(false);
			}
		}
	}

	/**
	 * Find the given mark inside the list of {@link MarkMapPresenterImp}s and
	 * establish to not show the feedback.
	 * 
	 * @param mark
	 *            The mark model.
	 */
	private void hideFeedback(MarkModel mark) {

		// find the presenter that contains the given mark.
		Set<Entry<String, MarkMapPresenter>> entrySet = markMapPresenterList.entrySet();
		Iterator<Entry<String, MarkMapPresenter>> iter = entrySet.iterator();
		while (iter.hasNext()) {

			Entry<String, MarkMapPresenter> entry = iter.next();
			MarkMapPresenter presenter = entry.getValue();

			if (presenter.getMarkModel().getID().equals(mark.getID())) {
				presenter.showSelectedFeedback(false);
			}
		}
	}
}
