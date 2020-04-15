/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.command;

import java.util.Arrays;

import org.locationtech.udig.project.internal.command.navigation.NavComposite;
import org.locationtech.udig.project.internal.command.navigation.PanCommand;
import org.locationtech.udig.project.internal.command.navigation.SetViewportBBoxCommand;
import org.locationtech.udig.project.internal.command.navigation.SetViewportCenterCommand;
import org.locationtech.udig.project.internal.command.navigation.SetViewportHeight;
import org.locationtech.udig.project.internal.command.navigation.SetViewportWidth;
import org.locationtech.udig.project.internal.command.navigation.ZoomCommand;
import org.locationtech.udig.project.internal.command.navigation.ZoomExtentCommand;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

/**
 * Factory providing default implementations of common navigation commands.
 * 
 * @author jeichar
 * @deprecated Moved to org.locationtech.udig.project.command.factory.NavigationCommandFactory
 * @since TODO provide version
 */
public class NavigationCommandFactory {
    /**
     * Creates a new NavigationCommandFactory object
     * 
     * @return a new NavigationCommandFactory object
     */
    public static NavigationCommandFactory getInstance() {
        return instance;
    }
    private static final NavigationCommandFactory instance = new NavigationCommandFactory();
    protected NavigationCommandFactory() {
        // no op
    }

    /**
     * Creates a new {@linkplain NavComposite}
     * 
     * @param commands an array of commands to execute as a simgle command. The array will be
     *        executed from position 0 to position length-1 in order.
     * @return a new NavComposite object
     * @see NavCommand
     */
    public NavCommand createCompositeCommand( NavCommand[] commands ) {
        return new NavComposite(Arrays.asList(commands));
    }

    /**
     * Creates a new {@linkplain SetViewportBBoxCommand}
     * 
     * @param newbbox the new bounding box to set in the viewport
     * @return a new SetViewportBBoxCommand object
     * @see NavCommand
     * @see Envelope
     */
    public NavCommand createSetViewportBBoxCommand( Envelope newbbox ) {
        return new SetViewportBBoxCommand(newbbox);
    }

    /**
     * Creates a new {@linkplain ZoomCommand}
     * 
     * @param zoomfactor the amount to zoom
     * @return a new ZoomCommand object
     * @see NavCommand
     */
    public NavCommand createZoomCommand( double zoomfactor ) {
        return new ZoomCommand(zoomfactor);
    }

    /**
     * Creates a new {@linkplain ZoomExtentCommand}
     * 
     * @return a new ZoomExtentCommand object
     * @see NavCommand
     */
    public NavCommand createZoomExtentCommand() {
        return new ZoomExtentCommand();
    }

    /**
     * Creates a new {@linkplain SetViewportCenterCommand}
     * 
     * @param center Sets the center of the viewport. The Coordinate must be in world coordinates.
     * @return a new SetViewportCenterCommand object
     * @see NavCommand
     * @see Coordinate
     */
    public NavCommand createSetViewportCenterCommand( Coordinate center ) {
        return new SetViewportCenterCommand(center);
    }

    /**
     * Creates a new {@linkplain SetViewportHeight}
     * 
     * @param height The new viewport height
     * @return a new SetViewportHeight object
     * @see NavCommand
     */
    public NavCommand createSetViewportHeight( double height ) {
        return new SetViewportHeight(height);
    }

    /**
     * Creates a new {@linkplain SetViewportWidth}
     * 
     * @param width the new viewport width
     * @return a new SetViewportWidth object
     * @see NavCommand
     */
    public NavCommand createSetViewportWidth( double width ) {
        return new SetViewportWidth(width);
    }

    /**
     * Creates a new {@linkplain PanCommand}Pans the viewport in terms of pixels on the screen.
     * Each pixel represents a distance in world coordinates, the x and y distances differ, so a pan
     * of 8 pixels in the x direction will be translated to a pan of 8*xdistance in the world.
     * 
     * @param xpixels The amount, in pixels, to pan in the x direction
     * @param ypixels The amount, in pixels, to pan in the y direction
     * @return a new PanCommand object
     * @see NavCommand
     */
    public NavCommand createPanCommandUsingScreenCoords( int xpixels, int ypixels ) {
        return new PanCommand(xpixels, ypixels);
    }

    /**
     * Creates a new {@linkplain PanCommand}
     * 
     * @param x The amount, in world coordinates, to pan in the x direction
     * @param y The amount, in world coordinates, to pan in the y direction
     * @return a new PanCommand object
     * @see NavCommand
     */
    public NavCommand createPanCommandUsingWorldCoords( double x, double y ) {
        return new PanCommand(x, y);
    }

	public NavCommand createSetViewportBBoxCommand(Envelope bounds, CoordinateReferenceSystem crs) {
		return new SetViewportBBoxCommand(bounds, crs);
	}

}
