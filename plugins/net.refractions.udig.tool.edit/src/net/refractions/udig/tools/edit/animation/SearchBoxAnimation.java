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

import net.refractions.udig.core.IProvider;
import net.refractions.udig.project.ui.IAnimation;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;
import net.refractions.udig.tools.edit.support.Point;

import org.eclipse.core.runtime.IProgressMonitor;

public class SearchBoxAnimation extends AbstractLongRunningAnimation implements IAnimation {

    protected Point center;
    private long start;
    public SearchBoxAnimation( Point center, IProvider<Boolean> isValidProvider ) {
        super(PreferenceUtil.instance().getSnappingRadius(), isValidProvider);
        this.center = center;
        start=System.currentTimeMillis();
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        graphics.setColor(PreferenceUtil.instance().getFeedbackColor());
        int radius = PreferenceUtil.instance().getSnappingRadius();
        graphics.drawOval(center.getX() - radius, center.getY() - radius, radius * 2,
                radius * 2);

        graphics.drawOval(center.getX() - frame, center.getY() - frame, frame * 2,
                frame * 2);

    }

    public short getFrameInterval() {
        return 50;
    }

    @Override
    public boolean isValid() {
        return super.isValid() && System.currentTimeMillis()-start<10000;
    }
    
    public Rectangle getValidArea() {
        int radius = PreferenceUtil.instance().getSnappingRadius()+1;
        return new Rectangle(center.getX()-radius, center.getY()-radius, radius+radius, radius+radius) ;
    }

}