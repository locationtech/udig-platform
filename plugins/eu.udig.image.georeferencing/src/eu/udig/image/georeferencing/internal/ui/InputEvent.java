/* Image Georeferencing
 * 
 * Axios Engineering 
 *      http://www.axios.es 
 *
 * (C) 2011, Axios Engineering S.L. (Axios) 
 * Axios agrees to licence under Lesser General Public License (LGPL).
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
package eu.udig.image.georeferencing.internal.ui;

/**
 * Mouse input events type.
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.0.0
 * 
 */
public enum InputEvent {

	// TODO maybe it must be used only by the ImageInputEvent and define these
	// events in other class.

		MOUSE_DOWN,
		MOUSE_UP,
		MOUSE_DRAG,
		DELETE,
		ZOOM,
		PAN,
		MOUSE_SCROLL,
		MOUSE_OVER

}
