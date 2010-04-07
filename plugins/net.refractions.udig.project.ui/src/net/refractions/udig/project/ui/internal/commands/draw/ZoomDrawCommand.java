/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.project.ui.internal.commands.draw;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import net.refractions.udig.project.ui.commands.AbstractDrawCommand;
import net.refractions.udig.project.ui.commands.IMapTransformCommand;
import net.refractions.udig.project.ui.commands.IPreMapDrawCommand;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Sets the affine transform of the graphics to a zoom level.
 * 
 * @author jeichar
 * @since 0.3
 */
public class ZoomDrawCommand extends AbstractDrawCommand
        implements
            IMapTransformCommand,
            IPreMapDrawCommand {

    private AffineTransform transform;
    private int centerx;
    private int centery;

    /**
     * Construct <code>TranslateCommand</code>.
     * 
     * @param centerx the x-coord of the point the zoom centers around
     * @param centery the y-coord of the point the zoom centers around
     * @param zoom The amount of zoom
     */
    public ZoomDrawCommand( int centerx, int centery, double zoom ) {
        setZoom(centerx, centery, zoom);
    }

    /**
     * @see net.refractions.udig.project.internal.command.MapCommand#open()
     */
    public void run( IProgressMonitor monitor ) throws Exception {
        AffineTransform t = new AffineTransform(transform);
        t.concatenate(graphics.getTransform());
        graphics.setTransform(t);
    }

    /**
     * Sets the amount of zoom and where the center of the zoom will be when this command is called.
     * 
     * @param centerx
     * @param centery
     * @param amount
     */
    public void setZoom( int centerx, int centery, double amount ) {
        this.centerx = centerx;
        this.centery = centery;
        transform = AffineTransform.getTranslateInstance(centerx, centery);
        transform.scale(amount, amount);
        transform.translate(-centerx, -centery);
    }
    /**
     * Sets the amount the command will translate during the next paint phase
     * <ul>
     * <li>Sets the amount of zoom</li>
     * <li>repaints the display if the display is an instance of viewportPane</li>
     * </ul>
     * 
     * @param amount the amount of zoom
     */
    public void setZoom( double amount ) {
        setZoom(centerx, centery, amount);
        if (display instanceof ViewportPane)
            ((ViewportPane) display).repaint();
    }

    public Rectangle getValidArea() {
        return null;
    }
}
