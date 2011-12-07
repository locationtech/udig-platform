/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.command.navigation;

import java.awt.Rectangle;

import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Increases or decreases the size of the viewport(in world space) by a constant factor, zoom. The
 * zoom is equal in both directions. The function used is: bbox.height=bbox.height/divisor
 * bbox.width=bbox.width/divisor
 * 
 * @author jeichar
 * @since TODO provide version
 */
public class ZoomCommand extends AbstractNavCommand {

    private double zoomfactor = 1.0;
    private Coordinate fixedPoint;
    private Envelope envelope;

    /**
     * Creates a new instance of ZoomCommand
     * 
     * @param zoomfactor the amount to zoom
     *        <ul>
     *        <li>A zoom must be greater than 1.</li>
     *        <li>A zoom greater than 1 is a zoom towards the map(SimpleFeature appear larger.)</li>
     *        <li>A zoom less than 1 is a zoom away from the map</li>
     *        </ul>
     */
    public ZoomCommand( double zoomfactor ) {
        this.zoomfactor = zoomfactor;
    }

    /**
     * Creates a {@link ZoomCommand} that zooms to a given {@link Envelope}.
     * 
     * @param envelope the {@link Envelope} to zoom to.
     */
    public ZoomCommand( Envelope envelope ) {
        this.envelope = envelope;
    }

    /**
     * @param fixedPoint the point that will remain fixed after zoom. 
     *              If set it will be considered together with the 
     *              zoomfactor set in the constructor.
     */
    public void setFixedPoint( Coordinate fixedPoint ) {
        this.fixedPoint = fixedPoint;
    }

    /**
     * @see net.refractions.udig.project.internal.command.MapCommand#copy()
     */
    public MapCommand copy() {
        return new ZoomCommand(zoomfactor);
    }

    /**
     * @see net.refractions.udig.project.internal.command.navigation.AbstractNavCommand#runImpl()
     */
    protected void runImpl( IProgressMonitor monitor ) {
        if (envelope!=null) {
            model.zoomToBox(envelope);
        }else{
            model.zoom(zoomfactor, fixedPoint);
        }
    }

    /**
     * @see net.refractions.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return Messages.ZoomCommand_zoom; 
    }

}