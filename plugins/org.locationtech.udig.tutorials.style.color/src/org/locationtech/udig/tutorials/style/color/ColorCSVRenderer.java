/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tutorials.style.color;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IStyleBlackboard;
import org.locationtech.udig.project.internal.render.impl.RendererImpl;
import org.locationtech.udig.project.render.RenderException;
import org.locationtech.udig.tutorials.catalog.csv.CSV;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import au.com.bytecode.opencsv.CSVReader;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

public class ColorCSVRenderer extends RendererImpl {

    @Override
    public void render( IProgressMonitor monitor ) throws RenderException {
        Graphics2D g = getContext().getImage().createGraphics();
        render(g, monitor);
    }

    /**
     * This example shows how to obtain a color.
     * 
     * @param g
     * @param monitor
     * @throws RenderException
     */
public void render( Graphics2D g, IProgressMonitor monitor ) throws RenderException {
    if (monitor == null)
        monitor = new NullProgressMonitor();

    CSVReader reader = null;
    try {
        ILayer layer = getContext().getLayer();
        IGeoResource resource = layer.findGeoResource(CSV.class);
        if (resource == null)
            return;        
        ReferencedEnvelope bounds = getRenderBounds();
        monitor.subTask("connecting");
        CSV csv = resource.resolve(CSV.class, null);
        // LOOK UP STYLE
        IStyleBlackboard style = layer.getStyleBlackboard();
        Color color = (Color) style.get( ColorStyle.ID );

        // DATA TO WORLD
        CoordinateReferenceSystem dataCRS = layer.getCRS();
        CoordinateReferenceSystem worldCRS = context.getCRS();
        MathTransform dataToWorld = CRS.findMathTransform(dataCRS, worldCRS, false);

        // DRAW FILE
        monitor.beginTask("csv render", csv.getSize());
        reader = csv.reader();
        
        int nameIndex = csv.getHeader("name");
        Coordinate worldLocation = new Coordinate();
        String [] row;
        while ((row = reader.readNext()) != null) {
            Point point = csv.getPoint(row);
            Coordinate dataLocation = point.getCoordinate();
            try {
                JTS.transform(dataLocation, worldLocation, dataToWorld);
            } catch (TransformException e) {
                continue;
            }
            if (bounds != null && !bounds.contains(worldLocation)) {
                continue; // optimize!
            }
            java.awt.Point p = getContext().worldToPixel(worldLocation);

            g.setColor( color );
            g.fillRect(p.x-2, p.y-2, 6, 6);
            
            g.setColor(Color.BLACK);
            String name = row[nameIndex];
            g.drawString(name, p.x + 15, p.y + 15);
            monitor.worked(1);
            if (monitor.isCanceled()) break;
        }
    } catch (IOException e) {
        throw new RenderException(e); // rethrow any exceptions encountered
    } catch (FactoryException e) {
        throw new RenderException(e); // rethrow any exceptions encountered
    } finally {
        if (reader != null)
            try {
                reader.close();
            } catch (IOException e) {
                throw new RenderException(e); // rethrow any exceptions encountered
            }
        monitor.done();
    }
}

}
