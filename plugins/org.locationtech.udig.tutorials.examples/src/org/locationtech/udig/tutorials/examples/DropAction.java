/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tutorials.examples;

import java.awt.Rectangle;

import org.locationtech.udig.project.ui.commands.AbstractDrawCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.ui.IDropAction;
import org.locationtech.udig.ui.graphics.ViewportGraphics;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

/**
 * This contains an example of a DropAction that draws the results of toString() (of the dropped item)
 * on the screen.  I am assuming that the extension point looks like the following:
 * <pre>
 * 
 *   &lt extension
 *           point="org.locationtech.udig.ui.operation" &gt
 *       &lt operation
 *             class="java.lang.Object"
 *             enablesFor="1"
 *             id="org.locationtech.udig.code.examples.draw.string"
 *             name="DrawString"
 *             targetClass="org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane" /&gt
 *   &lt/ extension &gt
 * </pre>
 * 
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class DropAction extends IDropAction{

    @Override
    public boolean accept() {
        // for this one we don't care what the object is we just accept anything.
        return true;
    }

    @Override
    public void perform( IProgressMonitor monitor ) {
        final String string=getData().toString();
        
        // Since I declared my target to be a ViewportPane then I know that is what the 
        // destination is going to be.
        ViewportPane pane=(ViewportPane) getDestination();

        // In order to know where to draw the string we have to calculate where the 
        // drop event has taken place with respect to the ViewportPane.
        // The Event returns the location but it is w.r.t the Display.
        // All SWT widgets have helper methods to determine this so:
        Control control=pane.getControl();
        final Point drawLocation = control.toControl(getEvent().x, getEvent().y);
        
        // By Adding a custom draw command we can draw on the viewport model... 
        pane.addDrawCommand(new AbstractDrawCommand(){

            public Rectangle getValidArea() {
                // I'm being lazy and returning null so that this will be re-drawn every time the 
                // Viewport is updated.
                return null;
            }

            public void run( IProgressMonitor monitor ) throws Exception {
                // draw the string
                graphics.drawString(string, drawLocation.x, drawLocation.y, 
                        ViewportGraphics.ALIGN_LEFT, ViewportGraphics.ALIGN_BOTTOM);
            }
            
        });
        
    }

}
