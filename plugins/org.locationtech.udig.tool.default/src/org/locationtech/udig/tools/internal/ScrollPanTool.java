/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.tools.internal;

import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.internal.render.impl.ViewportModelImpl;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.project.ui.tool.AbstractModalTool;
import org.locationtech.udig.project.ui.tool.ModalTool;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.geotools.geometry.jts.ReferencedEnvelope;

import org.locationtech.jts.geom.Coordinate;

/**
 * This is a tool that pans without using complex Transforms. It pans using the scoll functionality
 * provided by canvas. This can produce a smoother panning experience.
 * <p>
 * It also allows for rendering systems to notice when panning is occurring and update the image as
 * necessary.
 * </p>
 * <p>
 * If you are using the tile rendering system, this tool will allow for tiles to be loaded while
 * panning.
 * </p>
 * <p>
 * Currently, this tool will not produce an improved panning experience
 * for the default udig rendering system as it this system uses advanced graphics
 * and does not pay attention to the IsBoundsChanging attribute of the viewport
 * model.  However it could be updated to pay attention to this attribute.
 * </p>
 * 
 * @author Emily Gouge
 * @since 1.2.0
 * @deprecated PanTool with Tool Options now covers this case
 */
public class ScrollPanTool extends AbstractModalTool implements ModalTool {
    private boolean dragging = false;
    private org.eclipse.swt.graphics.Point start = null;
    /**
     * Creates an new instance of Pan
     */
    public ScrollPanTool() {
        super(MOUSE | MOTION);

    }

    /**
     * @see org.locationtech.udig.project.ui.tool.AbstractTool#mouseDragged(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mouseDragged( MapMouseEvent e ) {
        if (dragging) {
            org.eclipse.swt.graphics.Point p = Display.getCurrent().map(
                    (Canvas) context.getViewportPane(), null, e.x, e.y);
            int xdiff = p.x - start.x;
            int ydiff = p.y - start.y;

            final ReferencedEnvelope bounds = context.getViewportModel().getBounds();
            Coordinate oldc = context.pixelToWorld(start.x, start.y);
            Coordinate newc = context.pixelToWorld(p.x, p.y);
            double xoffset = newc.x - oldc.x;
            double yoffset = newc.y - oldc.y;

            ReferencedEnvelope newbounds = new ReferencedEnvelope(bounds.getMinX() - xoffset,
                    bounds.getMaxX() - xoffset, bounds.getMinY() - yoffset, bounds.getMaxY()
                            - yoffset, bounds.getCoordinateReferenceSystem());

            ((Canvas) context.getViewportPane()).scroll(xdiff, ydiff, 0, 0, context.getMapDisplay()
                    .getWidth(), context.getMapDisplay().getHeight(), true);
            ((ViewportModel) context.getViewportModel()).setBounds(newbounds);
            start = p;
        }
    }

    /**
     * @see org.locationtech.udig.project.ui.tool.AbstractTool#mousePressed(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mousePressed( MapMouseEvent e ) {

        if (validModifierButtonCombo(e)) {
            ((ViewportPane) context.getMapDisplay()).enableDrawCommands(false);
            ((ViewportModel) context.getViewportModel()).setIsBoundsChanging(true);
            dragging = true;
            start = Display.getCurrent().map((Canvas) context.getViewportPane(), null, e.x, e.y);
        }
    }

    /**
     * Returns true if the combination of buttons and modifiers are legal to execute the pan.
     * <p>
     * This version returns true if button 1 is down and no modifiers
     * </p>
     * 
     * @param e
     * @return
     */
    protected boolean validModifierButtonCombo( MapMouseEvent e ) {
        return e.buttons == MapMouseEvent.BUTTON1 && !(e.modifiersDown());
    }

    /**
     * @see org.locationtech.udig.project.ui.tool.AbstractTool#mouseReleased(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mouseReleased( MapMouseEvent e ) {
        if (dragging) {

            ((ViewportModel) context.getViewportModel()).setIsBoundsChanging(false);
            ((ViewportPane) context.getMapDisplay()).enableDrawCommands(true);

            dragging = false;

            org.eclipse.swt.graphics.Point p = Display.getCurrent().map(
                    (Canvas) context.getViewportPane(), null, e.x, e.y);
            int xdiff = p.x - start.x;
            int ydiff = p.y - start.y;

            ReferencedEnvelope bounds = context.getViewportModel().getBounds();
            Coordinate oldc = context.pixelToWorld(start.x, start.y);
            Coordinate newc = context.pixelToWorld(p.x, p.y);
            double xoffset = newc.x - oldc.x;
            double yoffset = newc.y - oldc.y;

            ReferencedEnvelope newbounds = new ReferencedEnvelope(bounds.getMinX() - xoffset,
                    bounds.getMaxX() - xoffset, bounds.getMinY() - yoffset, bounds.getMaxY()
                            - yoffset, bounds.getCoordinateReferenceSystem());

            ((Canvas) context.getViewportPane()).scroll(xdiff, ydiff, 0, 0, context.getMapDisplay()
                    .getWidth(), context.getMapDisplay().getHeight(), true);
            start = p;

            // do one last set bounds that fires all the events to ensure our update is correct
            ((ViewportModelImpl) context.getViewportModel()).setBounds(newbounds);
        }
    }
    /**
     * @see org.locationtech.udig.project.ui.tool.Tool#dispose()
     */
    public void dispose() {
        super.dispose();
    }
}
