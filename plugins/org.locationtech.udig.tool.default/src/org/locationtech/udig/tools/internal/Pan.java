/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.tools.internal;

import java.awt.Point;

import org.locationtech.udig.project.command.NavCommand;
import org.locationtech.udig.project.command.navigation.PanCommand;
import org.locationtech.udig.project.ui.internal.commands.draw.TranslateCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.project.ui.tool.AbstractModalTool;
import org.locationtech.udig.project.ui.tool.ModalTool;
import org.locationtech.udig.tools.internal.commands.PanAndInvalidate;


/**
 * Provides Pan functionality for MapViewport
 *
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 * @deprecated PanTool with Tool Options now covers this case
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
     * @see org.locationtech.udig.project.ui.tool.AbstractTool#mouseDragged(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
     */
    @Override
    public void mouseDragged(MapMouseEvent e) {
        if (dragging) {
            command.setTranslation(e.x- start.x, e.y - start.y);
            context.getViewportPane().repaint();
        }
    }

    /**
     * @see org.locationtech.udig.project.ui.tool.AbstractTool#mousePressed(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
     */
    @Override
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
     * @see org.locationtech.udig.project.ui.tool.AbstractTool#mouseReleased(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
     */
    @Override
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
     * @see org.locationtech.udig.project.ui.tool.Tool#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
    }
}
