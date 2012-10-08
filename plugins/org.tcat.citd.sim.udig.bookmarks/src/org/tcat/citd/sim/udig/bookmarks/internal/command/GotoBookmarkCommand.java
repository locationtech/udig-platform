/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2006, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.tcat.citd.sim.udig.bookmarks.internal.command;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.Command;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.command.navigation.AbstractNavCommand;
import net.refractions.udig.project.render.IViewportModel;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.tcat.citd.sim.udig.bookmarks.Bookmark;

import com.vividsolutions.jts.geom.Envelope;

/**
 * @author cole.markham
 * @since 1.0.0
 */
public class GotoBookmarkCommand extends AbstractNavCommand {
    private Bookmark target;
    /**
     * @param target
     */
    public GotoBookmarkCommand( Bookmark target ) {
        this.target = target;
    }

     @Override
    protected void runImpl( IProgressMonitor monitor ) throws Exception {
         URI mapID = target.getMap().getMapID();
         IMap map = (IMap) (ProjectPlugin.getPlugin().getProjectRegistry().eResource()
                 .getResourceSet().getResource(mapID, true).getContents().get(0));
         ApplicationGIS.openMap(map);
         IViewportModel v = map.getViewportModel();
         
         final ReferencedEnvelope bookmarkEnvelope = target.getEnvelope();
         final Envelope viewportBounds = v.getBounds();

         final CoordinateReferenceSystem viewportCrs = v.getCRS();
         final CoordinateReferenceSystem bookmarkCrs = bookmarkEnvelope.getCoordinateReferenceSystem();

         final ReferencedEnvelope bookmarkedEnvelopeInVieportCRS;
         
         if(bookmarkCrs.equals(viewportCrs)){
         	bookmarkedEnvelopeInVieportCRS = bookmarkEnvelope;
         }else{
         	//reproject bookmark to viewport CRS
         	boolean lenient = true;
         	MathTransform transform = CRS.findMathTransform(bookmarkCrs, viewportCrs, lenient);
         	Envelope xformedEnvelope = JTS.transform(bookmarkEnvelope, transform);
         	bookmarkedEnvelopeInVieportCRS = new ReferencedEnvelope(xformedEnvelope, viewportCrs);
         }

         if (!bookmarkedEnvelopeInVieportCRS.equals(viewportBounds)) {
             model.zoomToBox(bookmarkedEnvelopeInVieportCRS);
         }
    }

    public Command copy() {
        return null;
    }

    public String getName() {
        return null;
    }

}
