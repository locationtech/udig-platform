/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.edit.animation;

import java.awt.Rectangle;

import net.refractions.udig.project.ui.IAnimation;
import net.refractions.udig.project.ui.commands.AbstractDrawCommand;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;
import net.refractions.udig.tools.edit.support.Point;

import org.eclipse.core.runtime.IProgressMonitor;

public class DeleteVertexAnimation extends AbstractDrawCommand implements IAnimation{

    private int frame=0;
    Point center;
    /**
     * @param point
     */
    public DeleteVertexAnimation( Point point ) {
        this.center=point;
    }

    public short getFrameInterval() {
        return 50;
    }

    public void nextFrame() {
        frame++;
    }

    public boolean hasNext() {
        return frame<6;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        graphics.setColor(PreferenceUtil.instance().getFeedbackColor());
        int a = frame*4;
        int c= (int) Math.sqrt(a*a+a*a);
        int x=center.getX();
        int y=center.getY();
        int rad=3;
        graphics.fill(new Rectangle(x-c, y,rad,rad));
        graphics.fill(new Rectangle(x+c, y,rad,rad));
        graphics.fill(new Rectangle(x, y+c,rad,rad));
        graphics.fill(new Rectangle(x, y-c,rad,rad));
        
        graphics.fill(new Rectangle(x-a, y-a,rad,rad));
        graphics.fill(new Rectangle(x+a, y-a,rad,rad));
        graphics.fill(new Rectangle(x+a, y+a,rad,rad));
        graphics.fill(new Rectangle(x-a, y+a,rad,rad));
    }

    public Rectangle getValidArea() {
        int a = frame*4;
        int c= (int) Math.sqrt(a*a+a*a);
        int x=center.getX();
        int y=center.getY();

        return new Rectangle( x-c, y-c, c+c, c+c );
    }
    
}