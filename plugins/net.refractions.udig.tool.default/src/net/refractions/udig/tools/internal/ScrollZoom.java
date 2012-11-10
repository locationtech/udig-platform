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
package net.refractions.udig.tools.internal;

import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseWheelEvent;
import net.refractions.udig.project.ui.tool.AbstractTool;


/**
 * A tool that enables zooming using the mouse wheel and simulates the mouse
 * wheel zoom when the alt key is held down and the mouse is moved horizontally
 * This tool is always "on"
 * 
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public class ScrollZoom extends AbstractTool {

    private static final int INTERVAL = 50;
    private int distance = 0;

    private int start;

    boolean in = true;
    
    /**
     * Creates an new instance of ScrollZoom
     */
    public ScrollZoom() {
        super(MOUSE|MOTION|WHEEL);
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseWheelMoved(net.refractions.udig.project.render.displayAdapter.MapMouseWheelEvent)
     */
    public void mouseWheelMoved(MapMouseWheelEvent e) {
        if( e.modifiersDown() )
            return;
        NavigationUpdateThread.getUpdater().zoomWithFixedPoint(e.clickCount * 3, getContext(), NavigationUpdateThread.DEFAULT_DELAY,
                e.getPoint());
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mousePressed(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mousePressed(MapMouseEvent e) {
        if (e.modifiers == MapMouseEvent.ALT_DOWN_MASK && e.buttons==MapMouseEvent.BUTTON1) {
            start = e.x;
        }
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseDragged(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mouseDragged(MapMouseEvent e) {
        if (e.modifiers == MapMouseEvent.ALT_DOWN_MASK && e.buttons==MapMouseEvent.BUTTON1) {
            distance += (start - e.x);

            if ((distance >= INTERVAL) || (distance <= -INTERVAL)) {
                NavigationUpdateThread.getUpdater().zoom(10*(distance > 0?1:-1), getContext(), 300);
                distance = 0;
                start = e.x;
            }
        }
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseReleased(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mouseReleased(MapMouseEvent e) {
        distance = 0;
    }


}