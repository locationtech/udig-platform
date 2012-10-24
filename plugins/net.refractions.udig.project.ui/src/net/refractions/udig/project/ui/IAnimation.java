/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.project.ui;

import net.refractions.udig.project.ui.commands.IDrawCommand;

import org.eclipse.core.runtime.IProgressMonitor;

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