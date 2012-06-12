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
package eu.udig.image.georeferencing.internal.ui.imagepanel.tools;

import org.eclipse.swt.graphics.Cursor;

import eu.udig.image.georeferencing.internal.ui.MouseSelectionListener;
import eu.udig.image.georeferencing.internal.ui.imagepanel.ImageComposite;

/**
 * 
 * Interface used by all the tools that interact with the canvas in the
 * {@link ImageComposite}.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.0.0
 * 
 */
public interface ImageTool {

	/**
	 * @param active
	 *            True to turn on the tool, false in other case.
	 */
	public void setActive(boolean active);

	/**
	 * It will trigger the behaviour associated to the tool.
	 * 
	 * @param ev
	 *            Image input event.
	 * @return True or false.
	 */
	public boolean eventHandle(ImageInputEvent ev);

	/**
	 * @return True if the tool is active.
	 */
	public boolean isActive();

	/**
	 * @return The cursor associated to the tool.
	 */
	public Cursor getCursor();

	/**
	 * Adds the {@link MouseSelectionListener} associated to this tool.
	 * 
	 * @param listener
	 *            The listener.
	 */
	public void addMouseSelectionListener(MouseSelectionListener listener);

	/**
	 * Removes the {@link MouseSelectionListener} associated to this tool.
	 * 
	 * @param listener
	 *            The listener.
	 */
	public void deleteMouseSelectionListener(MouseSelectionListener listener);
}
