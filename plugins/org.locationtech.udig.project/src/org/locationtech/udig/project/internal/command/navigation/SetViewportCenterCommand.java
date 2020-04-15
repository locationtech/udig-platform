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

import java.text.MessageFormat;

import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.command.NavCommand;
import org.locationtech.udig.project.internal.Messages;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.render.ViewportModel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Coordinate;

/**
 * Sets the center of the viewport. The Coordinate must be in world coordinates. The
 * {@linkplain ViewportModel#pixelToWorld(int, int)}methods can be used to calculate the value.
 * 
 * @author jeichar
 * @since TODO provide version
 */
public class SetViewportCenterCommand extends AbstractNavCommand implements NavCommand {

    private Coordinate center;
    private CoordinateReferenceSystem crs;
    /**
     * Creates a new instance of SetViewportCenterCommand
     * 
     * @param center Sets the center of the viewport. The Coordinate must be in world coordinates.
     */
    public SetViewportCenterCommand( Coordinate center ) {
        this(center, null);
    }

    public SetViewportCenterCommand( Coordinate coordinate, CoordinateReferenceSystem crs ) {
        center=coordinate;
        this.crs=crs;
    }

    /**
     * @see org.locationtech.udig.project.internal.command.navigation.AbstractNavCommand#runImpl()
     */
    protected void runImpl( IProgressMonitor monitor ) throws Exception {
        Coordinate newCenter = center;
        if( crs!=null )
            newCenter=transform();
        model.setCenter(newCenter);
    }

    private Coordinate transform() {
        try {
            return JTS.transform(center, new Coordinate(), CRS.findMathTransform(crs, model.getCRS(), true));
        } catch (Exception e) {
            ProjectPlugin.log("", e); //$NON-NLS-1$
            return null;
        } 
    }

    /**
     * @see org.locationtech.udig.project.internal.command.MapCommand#copy()
     */
    public MapCommand copy() {
        return new SetViewportCenterCommand(center);
    }

    /**
     * @see org.locationtech.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return MessageFormat.format(
                Messages.SetViewportCenterCommand_setViewCenter, new Object[]{center}); 
    }

}
