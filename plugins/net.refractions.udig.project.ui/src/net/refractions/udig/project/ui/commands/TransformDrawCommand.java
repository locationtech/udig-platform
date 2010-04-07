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
package net.refractions.udig.project.ui.commands;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Tranforms the Viewport's graphics current transform.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class TransformDrawCommand extends AbstractDrawCommand
        implements
            IMapTransformCommand,
            IPreMapDrawCommand {

    private double scaleFactorY=1;
    private double scaleFactorX=1;
    private int translateY;
    private int translateX;
    private double rotation;

    public Rectangle getValidArea() {
        return null;
    }

    public synchronized void run( IProgressMonitor monitor ) throws Exception {
        AffineTransform t = new AffineTransform();
        t.translate((double)display.getWidth()/(double)2, (double)display.getHeight()/(double)2);
        t.rotate(rotation);
        t.scale(scaleFactorX, scaleFactorY);
        t.translate((double)-display.getWidth()/(double)2, (double)-display.getHeight()/(double)2);
        t.translate(translateX, translateY);
        t.concatenate(graphics.getTransform());
        graphics.setTransform(t);
    }

    public synchronized void zoom( double scaleFactorX, double scaleFactorY ) {
        this.scaleFactorX=scaleFactorX;
        this.scaleFactorY=scaleFactorY;
    }

    public synchronized void pan( int x, int y ) {
        this.translateX=x;
        this.translateY=y;
    }

    public synchronized void rotate( double theta ) {
        this.rotation=theta;
    }

    public synchronized AffineTransform getTransform() {
        AffineTransform t = new AffineTransform();
        t.translate((double)display.getWidth()/(double)2, (double)display.getHeight()/(double)2);
        t.rotate(rotation);
        t.scale(scaleFactorX, scaleFactorY);
        t.translate((double)-display.getWidth()/(double)2, (double)-display.getHeight()/(double)2);
        t.translate(translateX, translateY);
        return t;
    }

}
