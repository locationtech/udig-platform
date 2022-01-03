/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.commands;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Transforms the Viewport's graphics current transform.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class TransformDrawCommand extends AbstractDrawCommand
        implements IMapTransformCommand, IPreMapDrawCommand {

    private double scaleFactorY = 1;

    private double scaleFactorX = 1;

    private int translateY;

    private int translateX;

    private double rotation;

    private Point fixedPoint;

    @Override
    public Rectangle getValidArea() {
        return null;
    }

    @Override
    public synchronized void run(IProgressMonitor monitor) throws Exception {
        if (fixedPoint == null) {
            fixedPoint = new Point(display.getWidth() / 2, display.getHeight() / 2);
        }
        AffineTransform t = new AffineTransform();
        t.translate(fixedPoint.getX(), fixedPoint.getY());
        t.rotate(rotation);
        t.scale(scaleFactorX, scaleFactorY);
        t.translate(-fixedPoint.getX(), -fixedPoint.getY());
        t.translate(translateX, translateY);
        t.concatenate(graphics.getTransform());
        graphics.setTransform(t);
    }

    public synchronized void fixPoint(Point fixedPoint) {
        this.fixedPoint = fixedPoint;
    }

    public synchronized void zoom(double scaleFactorX, double scaleFactorY) {
        this.scaleFactorX = scaleFactorX;
        this.scaleFactorY = scaleFactorY;
    }

    public synchronized void pan(int x, int y) {
        this.translateX = x;
        this.translateY = y;
    }

    public synchronized void rotate(double theta) {
        this.rotation = theta;
    }

    public synchronized AffineTransform getTransform() {
        AffineTransform t = new AffineTransform();
        t.translate((double) display.getWidth() / (double) 2,
                (double) display.getHeight() / (double) 2);
        t.rotate(rotation);
        t.scale(scaleFactorX, scaleFactorY);
        t.translate((double) -display.getWidth() / (double) 2,
                (double) -display.getHeight() / (double) 2);
        t.translate(translateX, translateY);
        return t;
    }

}
