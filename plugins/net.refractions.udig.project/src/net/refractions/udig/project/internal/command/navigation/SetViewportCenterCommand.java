/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.command.navigation;

import java.text.MessageFormat;

import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.command.NavCommand;
import net.refractions.udig.project.internal.Messages;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.render.ViewportModel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;

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
     * @see net.refractions.udig.project.internal.command.navigation.AbstractNavCommand#runImpl()
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
     * @see net.refractions.udig.project.internal.command.MapCommand#copy()
     */
    public MapCommand copy() {
        return new SetViewportCenterCommand(center);
    }

    /**
     * @see net.refractions.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return MessageFormat.format(
                Messages.SetViewportCenterCommand_setViewCenter, new Object[]{center}); 
    }

}