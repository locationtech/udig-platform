/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */

package net.refractions.udig.tool.select;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.internal.commands.selection.BBoxSelectionCommand;
import net.refractions.udig.project.ui.commands.SelectionBoxCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.ModalTool;
import net.refractions.udig.project.ui.tool.SimpleTool;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

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
    public static final String ID = "net.refractions.udig.tools.BBoxSelect"; //$NON-NLS-1$
    
    private Point start;

	private boolean selecting;

	net.refractions.udig.project.ui.commands.SelectionBoxCommand shapeCommand;

    Set<String> selectedFids = new HashSet<String>();
    
	/**
	 * Creates a new instance of BBoxSelection
	 */
	public BBoxSelection() {
		super(MOUSE | MOTION);
	}
    
	/**
	 * @see net.refractions.udig.project.ui.tool.SimpleTool#onMouseDragged(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
	 */
	protected void onMouseDragged(MapMouseEvent e) {
		Point end = e.getPoint();
		if(start == null) return; 
		shapeCommand.setShape(new Rectangle(Math.min(start.x, end.x), Math.min(
				start.y, end.y), Math.abs(start.x - end.x), Math.abs(start.y
				- end.y)));
		context.getViewportPane().repaint();
	}

	/**
	 * @see net.refractions.udig.project.ui.tool.AbstractTool#mousePressed(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
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
	 * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseReleased(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
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