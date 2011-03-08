/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.tools.internal;

import java.awt.Point;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.Command;
import net.refractions.udig.project.command.NavCommand;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.command.navigation.PanCommand;
import net.refractions.udig.project.internal.render.ViewportModel;
import net.refractions.udig.project.ui.internal.commands.draw.TranslateCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;
import net.refractions.udig.project.ui.tool.AbstractModalTool;
import net.refractions.udig.project.ui.tool.ModalTool;

import org.eclipse.core.runtime.IProgressMonitor;


/**
 * Provides Pan functionality for MapViewport
 *
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public class Pan extends AbstractModalTool implements ModalTool {
    private boolean dragging=false;
    private Point start=null;

    private TranslateCommand command;
    /**
     * Creates an new instance of Pan
     */
    public Pan() {
        super(MOUSE | MOTION);
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseDragged(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mouseDragged(MapMouseEvent e) {
        if (dragging) {
            command.setTranslation(e.x- start.x, e.y - start.y);
            context.getViewportPane().repaint();
        }
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mousePressed(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mousePressed(MapMouseEvent e) {
    	
        if (validModifierButtonCombo(e)) {
        	((ViewportPane)context.getMapDisplay()).enableDrawCommands(false);
            dragging = true;
            start = e.getPoint();
            command=context.getDrawFactory().createTranslateCommand(0,0);
            context.sendASyncCommand(command);
        }
    }

    /**
     * Returns true if the combination of buttons and modifiers are legal to execute the pan.
     * <p>
     * This version returns true if button 1 is down and no modifiers
     * </p>
     * @param e
     * @return
     */
	protected boolean validModifierButtonCombo(MapMouseEvent e) {
		return e.buttons== MapMouseEvent.BUTTON1
                && !(e.modifiersDown());
	}

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseReleased(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mouseReleased(MapMouseEvent e) {
        if (dragging) {
        	((ViewportPane)context.getMapDisplay()).enableDrawCommands(true);
            Point end=e.getPoint();
            NavCommand finalPan = new PanCommand((start.x-end.x), (start.y-end.y));

            //clear any events before we try to pan.  This dramatically reduces the number
            //of images drawn to the screen in the wrong spot
            ((ViewportPane) getContext().getMapDisplay()).update();
            
            context.sendASyncCommand(new PanAndInvalidate(finalPan, command));

            dragging = false;

        }
    }
    /**
     * @see net.refractions.udig.project.ui.tool.Tool#dispose()
     */
    public void dispose() {
        super.dispose();
    }

    /**
     * Executes the specified pan command, and only after it is executed, expires the last translate command
     */
    private class PanAndInvalidate implements Command, NavCommand {

        private NavCommand command;
        private TranslateCommand expire;

        PanAndInvalidate(NavCommand command, TranslateCommand expire) {
            this.command = command;
            this.expire = expire;
        }

        public Command copy() {
            return new PanAndInvalidate(command, expire);
        }

        public String getName() {
            return "PanAndDiscard";
        }

        public void run( IProgressMonitor monitor ) throws Exception {
            //we need to expire the translate command first otherwise
            //the image gets drawn in the wrong spot the first time
            // and we see weird affects
            expire.setValid(false);

            command.run(monitor);
           
        }

        public void setViewportModel( ViewportModel model ) {
            command.setViewportModel(model);
        }

        public Map getMap() {
            return command.getMap();
        }

        public void setMap( IMap map ) {
            command.setMap(map);
        }

        public void rollback( IProgressMonitor monitor ) throws Exception {
            command.rollback(monitor);
        }

    }
}
