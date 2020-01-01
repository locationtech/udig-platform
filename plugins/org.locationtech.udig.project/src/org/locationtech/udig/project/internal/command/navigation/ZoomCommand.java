/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.command.navigation;

import java.awt.Rectangle;

import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

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
     * @see org.locationtech.udig.project.internal.command.MapCommand#copy()
     */
    public MapCommand copy() {
        return new ZoomCommand(zoomfactor);
    }

    /**
     * @see org.locationtech.udig.project.internal.command.navigation.AbstractNavCommand#runImpl()
     */
    protected void runImpl( IProgressMonitor monitor ) {
        if (envelope!=null) {
            model.zoomToBox(envelope);
        }else{
            model.zoom(zoomfactor, fixedPoint);
        }
    }

    /**
     * @see org.locationtech.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return Messages.ZoomCommand_zoom; 
    }

}
