/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.command.navigation;

import org.locationtech.udig.project.command.NavCommand;
import org.locationtech.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A command that pans the viewport. The command can be defined in terms of pixels on the screen or
 * in terms of world units.
 * 
 * @author jeichar
 * @since 0.3
 */
public class PanCommand extends AbstractNavCommand implements NavCommand {

    double worldx;
    double worldy;

    int pixelx;
    int pixely;

    boolean inPixel;

    /**
     * Creates a new instance of PanCommand
     * 
     * @param pixelx The amount to pan in the x direction
     * @param pixely The amount to pan in the y direction
     */
    public PanCommand( int pixelx, int pixely ) {
        this.pixelx = pixelx;
        this.pixely = pixely;
        inPixel = true;
    }

    /**
     * Creates a new instance of PanCommand
     * 
     * @param worldx The amount to pan in the x direction
     * @param worldy The amount to pan in the y direction
     */
    public PanCommand( double worldx, double worldy ) {
        this.worldx = worldx;
        this.worldy = worldy;
        inPixel = false;
    }

    /**
     * @see org.locationtech.udig.project.command.navigation.AbstractNavCommand#runImpl()
     */
    @Override
    protected void runImpl( IProgressMonitor monitor ) throws Exception {
        if (inPixel)
            model.panUsingScreenCoords(pixelx, pixely);
        else
            model.panUsingWorldCoords(worldx, worldy);
    }

    /**
     * @see org.locationtech.udig.project.command.MapCommand#getName()
     */
    @Override
    public String getName() {
        return Messages.PanCommand_pan; 
    }

}
