/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */

package org.locationtech.udig.tool.select;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.internal.commands.selection.BBoxSelectionCommand;
import org.locationtech.udig.project.ui.commands.SelectionBoxCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.tool.ModalTool;
import org.locationtech.udig.project.ui.tool.SimpleTool;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

/**
 * A tool that puts a BBOX Filter on the layer's Filter
 * 
 * @author jeichar
 * @since 1.0
 */
public class BBoxSelection extends SimpleTool implements ModalTool {
    
    /**
     * Comment for <code>ID</code>
     */
    public static final String ID = "org.locationtech.udig.tools.BBoxSelect"; //$NON-NLS-1$
    
    private Point start;

	private boolean selecting;

	org.locationtech.udig.project.ui.commands.SelectionBoxCommand shapeCommand;

    Set<String> selectedFids = new HashSet<String>();
    
	/**
	 * Creates a new instance of BBoxSelection
	 */
	public BBoxSelection() {
		super(MOUSE | MOTION);
	}
    
	/**
	 * @see org.locationtech.udig.project.ui.tool.SimpleTool#onMouseDragged(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
	 */
	protected void onMouseDragged(MapMouseEvent e) {
		Point end = e.getPoint();
		if(start == null) return; 
		shapeCommand.setShape(
				new Rectangle(Math.min(start.x, end.x), Math.min(start.y, end.y), Math.abs(start.x - end.x), Math.abs(start.y - end.y)));
		context.getViewportPane().repaint();
	}

	/**
	 * @see org.locationtech.udig.project.ui.tool.AbstractTool#mousePressed(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
	 */
	public void onMousePressed(MapMouseEvent e) {
		shapeCommand = new SelectionBoxCommand();

		if (((e.button & MapMouseEvent.BUTTON1) != 0)) {
			selecting = true;

			start = e.getPoint();
			shapeCommand.setValid(true);
			shapeCommand.setShape(new Rectangle(start.x, start.y, 0, 0));
			context.sendASyncCommand(shapeCommand);
		}
	}

	/**
	 * @see org.locationtech.udig.project.ui.tool.AbstractTool#mouseReleased(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
	 */
	public void onMouseReleased(MapMouseEvent e) {
		if (selecting) {
			Point end = e.getPoint();
			if (start == null || start.equals(end)) {

				Envelope bounds = getContext()
						.getBoundingBox(e.getPoint(),3);
				sendSelectionCommand(e, bounds);
			} else {
				Coordinate c1 = context.getMap().getViewportModel()
						.pixelToWorld(start.x, start.y);
				Coordinate c2 = context.getMap().getViewportModel()
						.pixelToWorld(end.x, end.y);

				Envelope bounds = new Envelope(c1, c2);
				sendSelectionCommand(e, bounds);
			}
		}
	}
    
    /**
	 * @param e
	 * @param bounds
	 */
	protected void sendSelectionCommand(MapMouseEvent e, Envelope bounds) {
		MapCommand command;
		if( e.isModifierDown(MapMouseEvent.MOD2_DOWN_MASK) ) {
            command = new BBoxSelectionCommand(bounds, BBoxSelectionCommand.ADD);
        }else if( e.isModifierDown(MapMouseEvent.MOD1_DOWN_MASK) ){
            command = new BBoxSelectionCommand(bounds, BBoxSelectionCommand.SUBTRACT);
        }else{
        	command = new BBoxSelectionCommand(bounds, BBoxSelectionCommand.NONE);
        }
        
        getContext().sendASyncCommand(command);
		selecting = false;
		shapeCommand.setValid(false);
		getContext().getViewportPane().repaint();
	}
}
