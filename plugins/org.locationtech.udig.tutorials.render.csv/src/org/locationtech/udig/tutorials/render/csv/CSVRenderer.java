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
package org.locationtech.udig.tutorials.render.csv;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.render.impl.RendererImpl;
import org.locationtech.udig.project.render.RenderException;
import org.locationtech.udig.tutorials.catalog.csv.CSV;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import au.com.bytecode.opencsv.CSVReader;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

public class CSVRenderer extends RendererImpl {

    @Override
    public void render( IProgressMonitor monitor ) throws RenderException {
        Graphics2D g = getContext().getImage().createGraphics();
        render(g, monitor);
    }

    /**
     * This is an example of making a renderer that is capable of transforming from the data crs to
     * the world crs.
     * <p>
     * Of note:
     * <ul>
     * <li>Use layer.getCRS() for the data CRS - this lets your users "correct" data for which no
     * CRS is provided.
     * <li>transform 1) data to world 2) worldToPixel
     * </ul>
     */
    public void render( Graphics2D g, IProgressMonitor monitor ) throws RenderException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        monitor.beginTask("csv render", 100);

        CSVReader reader = null;
        try {
            g.setColor(Color.BLACK);
            ILayer layer = getContext().getLayer();
            IGeoResource resource = layer.findGeoResource(CSV.class);
            if (resource == null)
                return;

            CoordinateReferenceSystem dataCRS = layer.getCRS();
            CoordinateReferenceSystem worldCRS = context.getCRS();
            MathTransform dataToWorld = CRS.findMathTransform(dataCRS, worldCRS, false);

            ReferencedEnvelope bounds = getRenderBounds();
            monitor.subTask("connecting");
            
            CSV csv = resource.resolve(CSV.class, new SubProgressMonitor(monitor, 10) );
            reader = csv.reader();
            
            int nameIndex = csv.getHeader("name");

            IProgressMonitor drawMonitor = new SubProgressMonitor(monitor, 90);
            Coordinate worldLocation = new Coordinate();
            
            drawMonitor.beginTask("draw "+csv.toString(), csv.getSize());            
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
                g.fillOval(p.x, p.y, 10, 10);
                String name = row[nameIndex];
                g.drawString(name, p.x + 15, p.y + 15);
                drawMonitor.worked(1);

                if (drawMonitor.isCanceled())
                    break;
            }
            drawMonitor.done();            
        } catch (IOException e) {
            throw new RenderException(e); // rethrow any exceptions encountered
        } catch (FactoryException e) {
            throw new RenderException(e); // rethrow any exceptions encountered
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RenderException(e);
                }
            monitor.done();
        }
    }
    /**
     * Replacement for getRenderBounds() that figures out
     * which is smaller.
     * 
     * @return smaller of viewport bounds or getRenderBounds()
     */
    public ReferencedEnvelope getBounds() {
        ReferencedEnvelope renderBounds = getRenderBounds();
        ReferencedEnvelope viewportBounds = context.getViewportModel().getBounds();
        if (renderBounds == null) {
            return viewportBounds;
        }
        if (viewportBounds == null) {
            return renderBounds;
        }
        if (viewportBounds.contains((BoundingBox)renderBounds)) {
            return renderBounds;
        } else if (renderBounds.contains((BoundingBox)viewportBounds)) {
            return viewportBounds;
        }
        return renderBounds;
    }
    /**
     * The following example is simple and is shown to introduce the concept of a renderer. This
     * example assumes the world is in WGS84; and makes use of a single worldToPixel transformation.
     * <p>
     * Please compare with the complete *render* method below
     * 
     * @param g
     * @param monitor
     * @throws RenderException
     */
    public void render_example( Graphics2D g, IProgressMonitor monitor ) throws RenderException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        monitor.beginTask("csv render", IProgressMonitor.UNKNOWN);
        CSVReader reader = null;
        try {
            g.setColor(Color.BLUE);

            ILayer layer = getContext().getLayer();
            IGeoResource resource = layer.findGeoResource(CSV.class);
            if (resource == null)
                return;

            ReferencedEnvelope bounds = getRenderBounds();
            monitor.subTask("connecting");

            CSV csv = resource.resolve(CSV.class, new SubProgressMonitor(monitor, 10));
            reader = csv.reader();
            
            monitor.subTask("drawing");
            int nameIndex = csv.getHeader("name");
            Coordinate worldLocation = new Coordinate();
            String [] row;
            while ((row = reader.readNext()) != null) {
                Point point = csv.getPoint(row);
                worldLocation = point.getCoordinate();
                if (bounds != null && !bounds.contains(worldLocation)) {
                    continue; // optimize!
                }
                java.awt.Point p = getContext().worldToPixel(worldLocation);
                g.fillOval(p.x, p.y, 10, 10);
                String name = row[nameIndex];
                g.drawString(name, p.x + 15, p.y + 15);
                monitor.worked(1);

                if (monitor.isCanceled())
                    break;
            }
        } catch (IOException e) {
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
