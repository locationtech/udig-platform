
/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.edit.commands;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

import net.refractions.udig.project.ui.IAnimation;
import net.refractions.udig.project.ui.commands.AbstractDrawCommand;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A simple animation that indicates that a selection is taking place.
 * 
 * @author jesse
 * @since 1.1.0
 */
class BlockingSelectionAnim extends AbstractDrawCommand implements IAnimation {
    private int x;
    private int y;

    BlockingSelectionAnim( int x, int y ) {
        this.x = x;
        this.y = y;
    }

    int frame = 2;

    public short getFrameInterval() {
        return 250;
    }

    public void nextFrame() {
        frame -= 2;
        if (frame < 0)
            frame = 6;
    }

    public boolean hasNext() {
        return isValid();
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        graphics.setColor(Color.RED);
        int rad = 3 + frame;
        graphics.draw(new Ellipse2D.Float(x - rad, y - rad, rad * 2, rad * 2));
    }

    public Rectangle getValidArea() {
        int rad = 3 + frame;
        return new Rectangle(x - rad, y - rad, rad * 2, rad * 2);
    }

}
