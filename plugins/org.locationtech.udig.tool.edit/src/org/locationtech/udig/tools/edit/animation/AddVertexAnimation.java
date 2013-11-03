/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.animation;

import java.awt.Rectangle;

import org.locationtech.udig.project.ui.IAnimation;
import org.locationtech.udig.project.ui.commands.AbstractDrawCommand;
import org.locationtech.udig.tools.edit.preferences.PreferenceUtil;

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
