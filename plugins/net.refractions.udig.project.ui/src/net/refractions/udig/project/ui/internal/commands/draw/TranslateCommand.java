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

import java.awt.Point;
import java.awt.Rectangle;

import net.refractions.udig.project.ui.commands.AbstractDrawCommand;
import net.refractions.udig.project.ui.commands.IMapTransformCommand;
import net.refractions.udig.project.ui.commands.IPreMapDrawCommand;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Sets the ViewportGraphics object translate its 0,0 coordinate by -x,-y. IE. shapes are drawn down
 * and right if x,y are both positive.
 * 
 * @author jeichar
 * @since 0.3
 */
public class TranslateCommand extends AbstractDrawCommand
        implements
            IMapTransformCommand,
            IPreMapDrawCommand {

    private Point offset;

    /**
     * Construct <code>TranslateCommand</code>.
     * 
     * @param offset The amount of offset
     */
    public TranslateCommand( Point offset ) {
        this.offset = offset;
    }

    /**
     * Construct <code>TranslateCommand</code>.
     * 
     * @param x The amount of offset in the x-direction
     * @param y The amount of offset in the y-direction
     */
    public TranslateCommand( int x, int y ) {
        this.offset = new Point(x, y);
    }
    /**
     * @see net.refractions.udig.project.internal.command.MapCommand#open()
     */
    public void run( IProgressMonitor monitor ) throws Exception {
        if (offset.x > 0) {
            graphics.clearRect(0, 0, offset.x, display.getHeight());
        } else {
            graphics.clearRect(display.getWidth(), 0, -offset.x, display.getHeight());
        }
        if (offset.y > 0) {
            graphics.clearRect(0, 0, display.getWidth(), offset.y);
        } else {
            graphics.clearRect(0, display.getHeight(), display.getWidth(), -offset.y);
        }
        graphics.translate(offset);
    }

    /**
     * Sets the amount the command will translate during the next paint phase
     * 
     * @param x x-translation
     * @param y y-translation
     */
    public void setTranslation( int x, int y ) {
        offset.x = x;
        offset.y = y;
    }
    /**
     * Sets the amount the command will translate during the next paint phase
     * 
     * @param offset The amount of translation
     */
    public void setTranslation( Point offset ) {
        this.offset = offset;
    }

    public Rectangle getValidArea() {
        return null;
    }

}
