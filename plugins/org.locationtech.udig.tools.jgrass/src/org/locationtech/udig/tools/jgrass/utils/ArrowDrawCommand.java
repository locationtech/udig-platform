/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.tools.jgrass.utils;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;

import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;
import org.locationtech.udig.project.ui.commands.AbstractDrawCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseListener;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;

import org.eclipse.core.runtime.IProgressMonitor;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;

/**
 * Arrow drawing class. Adapted from udig's MessageBubble class.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class ArrowDrawCommand extends AbstractDrawCommand {

    private Coordinate start;
    private Coordinate end;
    private Rectangle validArea;
    private Color lineColor1 = new Color(255, 0, 0, 255);
    private Color lineColor2 = new Color(0, 0, 0, 255);

    /**
     * Contructor.
     * 
     * @param start the startng point of the arrow.
     * @param end the end point of the arrow.
     */
    public ArrowDrawCommand( final Coordinate start, final Coordinate end ) {
        this.start = start;
        this.end = end;
    }

    public void run( IProgressMonitor monitor ) throws Exception {

        display.addMouseListener(mouseListener);

        LineSegment l = new LineSegment(start, end);
        start = l.pointAlong(0.1);
        end = l.pointAlong(0.9);
        l = new LineSegment(start, end);

        Coordinate tmp = l.pointAlong(0.9);
        double distance = end.distance(tmp);
        Coordinate left = l.pointAlongOffset(0.5, distance / 2);
        Coordinate right = l.pointAlongOffset(0.5, -distance / 2);

        validArea = new Rectangle((int) start.x, (int) start.y, (int) (start.x + (end.x - start.x)),
                (int) (start.y + (end.y - start.y)));

        graphics.setLineWidth(3);
        graphics.setColor(lineColor2);
        graphics.drawLine((int) start.x, (int) start.y, (int) tmp.x, (int) tmp.y);
        graphics.setLineWidth(1);
        graphics.setColor(lineColor1);
        graphics.drawLine((int) start.x, (int) start.y, (int) tmp.x, (int) tmp.y);

        GeneralPath path = new GeneralPath();
        path.moveTo(end.x, end.y);
        path.lineTo(left.x, left.y);
        path.lineTo(right.x, right.y);
        path.closePath();
        graphics.fill(path);
        graphics.setColor(lineColor2);
        graphics.draw(path);
    }
    public Rectangle getValidArea() {
        return validArea;
    }

    public void setValid( boolean valid ) {
        super.setValid(valid);
        display.removeMouseListener(mouseListener);
    }

    private MapMouseListener mouseListener = new MapMouseListener(){

        public void mouseDoubleClicked( MapMouseEvent event ) {
        }

        public void mouseEntered( MapMouseEvent event ) {
        }

        public void mouseExited( MapMouseEvent event ) {
            disable((ViewportPane) event.source, this);
        }

        public void mousePressed( MapMouseEvent event ) {
            disable((ViewportPane) event.source, this);
        }

        public void mouseReleased( MapMouseEvent event ) {
        }

    };

    void disable( ViewportPane pane, Object listener ) {
        if (!isValid(pane)) {
            if (listener instanceof MapMouseListener) {
                pane.removeMouseListener((MapMouseListener) listener);
            }
            return;
        }

        setValid(false);
        pane.repaint();
    }

    private boolean isValid( IMapDisplay source ) {
        if (!ArrowDrawCommand.this.isValid())
            return false;
        if (source != display)
            return false;
        return true;
    }

}
