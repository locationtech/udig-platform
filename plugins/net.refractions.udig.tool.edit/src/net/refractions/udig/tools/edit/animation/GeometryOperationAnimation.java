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
package net.refractions.udig.tools.edit.animation;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;

import net.refractions.udig.core.IProvider;
import net.refractions.udig.project.ui.IAnimation;
import net.refractions.udig.project.ui.commands.AbstractDrawCommand;

import org.eclipse.core.runtime.IProgressMonitor;

public class GeometryOperationAnimation extends AbstractDrawCommand implements IAnimation {

    private int frame;
    private Shape shape;
    private IProvider<Boolean> stopAnimationProvider;

    /**
     * @param currentJavaShape
     */
    public GeometryOperationAnimation( Shape currentJavaShape, IProvider<Boolean> stopAnimationProvider ) {
        this.shape = currentJavaShape;
        this.stopAnimationProvider=stopAnimationProvider;
    }

    public short getFrameInterval() {
        return 100;
    }

    public void nextFrame() {
        frame++;
        frame = frame % 2;
    }

    public boolean hasNext() {
        return isValid()&& stopAnimationProvider.get();
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        if (frame == 0) {
            graphics.setColor(Color.RED);
        } else {
            graphics.setColor(Color.BLACK);
        }
        graphics.draw(shape);
    }

    public Rectangle getValidArea() {
        return shape.getBounds();
    }
}
