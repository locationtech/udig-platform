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