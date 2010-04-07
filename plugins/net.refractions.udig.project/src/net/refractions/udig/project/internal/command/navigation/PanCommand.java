/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.command.navigation;

import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.command.NavCommand;
import net.refractions.udig.project.internal.Messages;

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
     * @see net.refractions.udig.project.internal.command.navigation.AbstractNavCommand#runImpl()
     */
    protected void runImpl( IProgressMonitor monitor ) throws Exception {
        if (inPixel)
            model.panUsingScreenCoords(pixelx, pixely);
        else
            model.panUsingWorldCoords(worldx, worldy);
    }

    /**
     * @see net.refractions.udig.project.internal.command.MapCommand#copy()
     */
    public MapCommand copy() {
        if (inPixel)
            return new PanCommand(pixelx, pixely);

        return new PanCommand(worldx, worldy);
    }

    /**
     * @see net.refractions.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return Messages.PanCommand_pan; 
    }

}