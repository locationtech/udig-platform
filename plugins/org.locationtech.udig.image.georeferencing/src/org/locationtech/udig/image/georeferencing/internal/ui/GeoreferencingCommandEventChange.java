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

import org.locationtech.udig.image.georeferencing.internal.process.MarkModel;

/**
 * This class maintains the relation between a change event and a mark used in
 * the {@link GeoReferencingCommand} when it notifies something through the
 * Observable.
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.3
 * 
 */
public final class GeoreferencingCommandEventChange {

	/* Change event types */
	public enum ChangeEvent {
			MARK_ADDED,
			MARK_DELETED,
			CAN_EXECUTE,
			ALL_MARKS_DELETED,
			TEXT_CHANGE,
			IMAGE_LOADED,
			MAP_CHANGE,
			MAP_CHANGE_TO_ORIGINAL
	};

	private MarkModel	mark	= null;
	private ChangeEvent	event	= null;

	/**
	 * Constructor that associated a mark with an event.
	 * 
	 * @param mark
	 *            Mark model which triggered the event.
	 * @param event
	 *            Change event.
	 */
	public GeoreferencingCommandEventChange(MarkModel mark, ChangeEvent event) {

		assert mark != null;
		assert event != null;

		this.mark = mark;
		this.event = event;
	}

	/**
	 * Constructor used when an event was triggered but it doesn't belong to an
	 * specific mark, it could belong to all the marks like the event
	 * {@link ChangeEvent#ALL_MARKS_DELETED} or to any mark like
	 * {@link ChangeEvent#IMAGE_LOADED}
	 * 
	 * @param event
	 */
	public GeoreferencingCommandEventChange(ChangeEvent event) {

		assert event != null;

		this.event = event;
	}

	public MarkModel getMark() {
		return mark;
	}

	public ChangeEvent getEvent() {
		return event;
	}

}
