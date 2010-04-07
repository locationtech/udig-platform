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

import java.awt.Rectangle;

import net.refractions.udig.project.ui.IAnimation;
import net.refractions.udig.project.ui.commands.AbstractDrawCommand;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A simple animation to run when adding a vertex 
 * 
 * @author jones
 * @since 1.1.0
 */
public class AddVertexAnimation extends AbstractDrawCommand implements IAnimation {

    private int frame;
    private int y;
    private int x;
    public AddVertexAnimation(int x, int y){
        this.x=x;
        this.y=y;
    }
    public short getFrameInterval() {
        return 100;
    }

    public void nextFrame() {
        frame++;
    }

    public boolean hasNext() {
        return frame<6;
    }

    public void run( IProgressMonitor monitor ) throws Exception {

        graphics.setColor(PreferenceUtil.instance().getFeedbackColor());
        int rad = 25-(frame*5);
        graphics.drawOval(x-rad,y-rad, rad*2, rad*2 );
    }
    
    public Rectangle getValidArea() {
        int rad = 25-(frame*5);
        return new Rectangle( x-rad, y-rad, rad*2, rad*2);
    }

}
