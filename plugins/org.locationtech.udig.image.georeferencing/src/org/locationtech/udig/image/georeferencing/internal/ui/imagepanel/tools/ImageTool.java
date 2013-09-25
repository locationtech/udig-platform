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
package org.locationtech.udig.image.georeferencing.internal.ui.imagepanel.tools;

import org.eclipse.swt.graphics.Cursor;
import org.locationtech.udig.image.georeferencing.internal.ui.MouseSelectionListener;
import org.locationtech.udig.image.georeferencing.internal.ui.imagepanel.ImageComposite;


/**
 * 
 * Interface used by all the tools that interact with the canvas in the
 * {@link ImageComposite}.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.3.3
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
