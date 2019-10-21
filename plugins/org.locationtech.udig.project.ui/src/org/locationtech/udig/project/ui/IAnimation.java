/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.locationtech.udig.project.ui.commands.IDrawCommand;

/**
 * This Commands manages the timing of a short animation being drawn on the ViewportPane.
 * 
 * @author jeichar
 */
public interface IAnimation extends IDrawCommand {

    /**
     * Gets time the interval between the frames in milliseconds.
     * <p>
     * The time cannot be smaller than 100 milliseconds
     * </p>
     * 
     * @return time the interval between the frames in milliseconds.
     */
    short getFrameInterval();
    /**
     * Increments the current frame.  The next time run is called the frame should be drawn
     */
    void nextFrame();
    /**
     * Returns true if the animation has more frames to display.
     *
     * @return  true if the animation has more frames to display.
     */
    boolean hasNext();
    /**
     * This method draws the current frame <b>and</b> fires a FRAME event
     */
    public void run( IProgressMonitor monitor ) throws Exception;
}
