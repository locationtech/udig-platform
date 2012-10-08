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

import java.awt.Color;
import java.util.Observable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import eu.udig.image.georeferencing.internal.process.MarkModel;
import eu.udig.image.georeferencing.internal.ui.AbstractMarkPresenter;
import eu.udig.image.georeferencing.internal.ui.InputEvent;
import eu.udig.image.georeferencing.internal.ui.imagepanel.MarkImagePresenter;

/**
 * Preview used to show a mark while being D&D.
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.3
 * 
 */
final class MarkImagePreview extends AbstractMarkPresenter implements MarkImagePresenter {

	private MarkImagePresenter	parent	= null;
	private Canvas				canvas;
	private GC					gc;

	private int					xRelativeToCanvas;
	private int					yRelativeToCanvas;

	private int					x;
	private int					y;

	public MarkImagePreview(MarkImagePresenter parent, Canvas canvas) {

		assert parent != null;

		this.parent = parent;
		this.canvas = canvas;
		createContent(canvas);
	}

	private void createContent(Canvas parent) {

		parent.addListener(SWT.Paint, new Listener() {

			public void handleEvent(Event event) {
				gc = event.gc;

				draw();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable arg0, Object arg1) {

		// nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.axios.udig.ui.georeferencing.internal.ui.MarkPresenter#eventHandler
	 * (es.axios.udig.ui.georeferencing.internal.ui.MarkPresenter.InputEvent,
	 * int, int)
	 */
	public boolean eventHandler(InputEvent event, int x, int y) {

		switch (event) {
		case MOUSE_DRAG: // dragging.

			this.x = x;
			this.y = y;

			this.canvas.redraw();
			break;
		default:
			break;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.axios.udig.ui.georeferencing.internal.ui.MarkPresenter#draw()
	 */
	public void draw() {

		assert this.gc != null;
		assert !this.gc.isDisposed();

		if (this.hide)
			return;

		String id = this.getMarkModel().getID();

		int x = this.x - this.xRelativeToCanvas;
		int y = this.y - this.yRelativeToCanvas;

		Color c = createColor(this.getMarkModel().hashCode());
		org.eclipse.swt.graphics.Color color = new org.eclipse.swt.graphics.Color(gc.getDevice(), c.getRed(),
					c.getGreen(), c.getBlue());
		gc.setForeground(color);
		gc.setBackground(color);

		Font swtFont = new Font(gc.getDevice(), "Arial", 10, SWT.NORMAL); //$NON-NLS-1$
		gc.setFont(swtFont);

		gc.fillOval(x - 3, y - 3, 6, 6);
		gc.drawString(String.valueOf(id), x - 3, y + 6, true);

	}

	/**
	 * retrun MarkModel
	 */
	public MarkModel getMarkModel() {

		return this.parent.getMarkModel();
	}

}
