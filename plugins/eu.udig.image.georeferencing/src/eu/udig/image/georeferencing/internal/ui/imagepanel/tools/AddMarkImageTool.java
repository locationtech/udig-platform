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
package eu.udig.image.georeferencing.internal.ui.imagepanel.tools;

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;

import eu.udig.image.georeferencing.internal.process.MarkModel;
import eu.udig.image.georeferencing.internal.process.MarkModelFactory;
import eu.udig.image.georeferencing.internal.ui.InputEvent;
import eu.udig.image.georeferencing.internal.ui.imagepanel.ImageComposite;
import eu.udig.image.georeferencing.internal.ui.imagepanel.ImagePanelUtil;
import eu.udig.image.georeferencing.internal.ui.imagepanel.MarkImagePresenter;
import eu.udig.image.georeferencing.internal.ui.imagepanel.MarkImagePresenterFactory;

/**
 * Tool used to add marks to the image.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.3.3
 * 
 */
public class AddMarkImageTool extends AbstractImageTool {

	/**
	 * Constructor.
	 * 
	 * @param cursor
	 *            Tool cursor.
	 * @param imgComposite
	 *            Parent composite.
	 */
	public AddMarkImageTool(Cursor cursor, ImageComposite imgComposite) {
		super(cursor, imgComposite);
	}

	@Override
	protected boolean canHandle(ImageInputEvent ev) {

		if (InputEvent.MOUSE_UP.equals(ev.event)) {
			return true;
		}

		return false;
	}

	@Override
	protected boolean executeBehaviour(ImageInputEvent ev) {

		int x = ev.x;
		int y = ev.y;
		int hScroll = Math.abs(this.imgComposite.getHScrollValue());
		int vScroll = Math.abs(this.imgComposite.getVScrollValue());
		Point point = ImagePanelUtil.createMarkPosition(hScroll, vScroll, x, y, this.imgComposite.getScale());

		if (validateInside(point)) {

			addMark(x, y, hScroll, vScroll);
			return true;
		}
		return false;
	}

	/**
	 * Adds a mark on the image.
	 * 
	 * @param x			Cursor X position.
	 * @param y			Cursor Y position.
	 * @param hScroll
	 * @param vScroll
	 */
	private void addMark(int x, int y, int hScroll, int vScroll) {

		MarkModel markModel = MarkModelFactory.getInstance().create();
		
		Point position = ImagePanelUtil.createMarkPosition(hScroll, vScroll, x, y, this.imgComposite.getScale());

		MarkImagePresenter markPresenter = MarkImagePresenterFactory.createMarkPresenter(
																		markModel, position, hScroll, vScroll,
																		this.imgComposite.getCanvas(),
																		this.imgComposite.getScale());
		
		this.imgComposite.addMarkPresenter(markPresenter);

		this.imgComposite.getCmd().addMark(markModel);
		this.imgComposite.getCmd().evalPrecondition();
		this.imgComposite.getMainComposite().refreshMapGraphicLayer();

	}
}
