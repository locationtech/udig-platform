
/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
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
