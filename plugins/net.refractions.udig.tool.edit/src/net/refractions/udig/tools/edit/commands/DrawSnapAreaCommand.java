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

import java.awt.Rectangle;

import net.refractions.udig.core.IProvider;
import net.refractions.udig.project.ui.commands.AbstractDrawCommand;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;
import net.refractions.udig.tools.edit.support.Point;

import org.eclipse.core.runtime.IProgressMonitor;

public class DrawSnapAreaCommand extends AbstractDrawCommand{

    /** DrawSnapArea behaviour field */
    private IProvider<Point> tracker;
    private Rectangle lastArea;

    public DrawSnapAreaCommand(IProvider<Point> tracker){
        this.tracker=tracker;
    }
    
    public void run( IProgressMonitor monitor ) throws Exception {
        graphics.setColor(PreferenceUtil.instance().getFeedbackColor());
        int radius = PreferenceUtil.instance().getSnappingRadius();
        Point point = tracker.get();
        int y = point.getY()-radius;
        int x = point.getX()-radius;
        graphics.drawOval(x, y, radius*2, radius*2);
        lastArea = new Rectangle(x-4, y-4, radius*2+8, radius*2+8);
    }

    public Rectangle getValidArea() {
        int radius = PreferenceUtil.instance().getSnappingRadius();
        Point point = tracker.get();
        int y = point.getY()-radius;
        int x = point.getX()-radius;
        Rectangle rectangle = new Rectangle(x-4, y-4, radius*2+8, radius*2+8);
        if( lastArea!=null )
            return rectangle.union(lastArea);
        else
            return rectangle;
        
    }

}