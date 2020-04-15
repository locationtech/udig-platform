/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.tool.info.internal;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.text.DecimalFormat;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.tool.info.CoveragePointInfo;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.coverage.grid.GridCoordinates2D;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridEnvelope2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.coverage.grid.InvalidGridGeometryException;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.parameter.Parameter;
import org.geotools.referencing.CRS;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridCoverageReader;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import org.locationtech.jts.geom.Coordinate;

/**
 * Coverage click info gathering class.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class CoverageDescribeLayer {

    public static CoveragePointInfo info2( ILayer layer, ReferencedEnvelope bbox, IProgressMonitor monitor ) throws IOException,
            InvalidGridGeometryException, TransformException {

        final Coordinate envelopeCenterOrig = bbox.centre();
        CoordinateReferenceSystem sourceCRS = bbox.getCoordinateReferenceSystem();
        final DecimalFormat formatter = new DecimalFormat("0.####");
        IGeoResource geoResource = layer.getGeoResource();

        GridCoverage2D coverage = null;
        GridGeometry2D gridGeometry = null;
        Coordinate evaluateCoord = null;
        // try to go for the reader first
        boolean isOnGrid = false;
        boolean hasProblem = false;
        Point2D p = null;
        if (geoResource.canResolve(AbstractGridCoverage2DReader.class)) {
            AbstractGridCoverage2DReader reader = geoResource.resolve(AbstractGridCoverage2DReader.class, monitor);
            GeneralEnvelope originalEnvelope = reader.getOriginalEnvelope();
            CoordinateReferenceSystem targetCrs = reader.getCoordinateReferenceSystem();

            if (targetCrs != null) {
                evaluateCoord = transform(sourceCRS, targetCrs, envelopeCenterOrig);
            } else {
                evaluateCoord = envelopeCenterOrig;
                targetCrs = sourceCRS;
            }
            p = new Point2D.Double(evaluateCoord.x, evaluateCoord.y);
            if (originalEnvelope.contains(new DirectPosition2D(p))) {
                double delta = 0.0000001;
                GeneralParameterValue[] parameterValues = createGridGeometryGeneralParameter(1, 1, evaluateCoord.y + delta,
                        evaluateCoord.y - delta, evaluateCoord.x + delta, evaluateCoord.x - delta, targetCrs);
                coverage = reader.read(parameterValues);
                /*
                 * the following is done since the reader might read a singlwe pixel 
                 * region and the gridcoordinate would be 0, 0 in that case. Later
                 * we want to supply the gridcoordinate of the position in the whole
                 * coverage. 
                 */
                gridGeometry = new GridGeometry2D(reader.getOriginalGridRange(), reader.getOriginalEnvelope());
                isOnGrid = true;
            }
        }
        // else try with coverage
        else if (geoResource.canResolve(GridCoverage.class)) {
            coverage = (GridCoverage2D) geoResource.resolve(GridCoverage.class, monitor);
            CoordinateReferenceSystem targetCrs = coverage.getCoordinateReferenceSystem();
            gridGeometry = coverage.getGridGeometry();
            evaluateCoord = transform(sourceCRS, targetCrs, envelopeCenterOrig);
            p = new Point2D.Double(evaluateCoord.x, evaluateCoord.y);
            Envelope2D envelope2d = coverage.getEnvelope2D();
            if (envelope2d.contains(p)) {
                isOnGrid = true;
            }
        } else {
            hasProblem = true;
        }

        final StringBuilder sb = new StringBuilder();
        if (hasProblem) {
            sb.append("The coverage information could not be read.");
        } else if (isOnGrid) {
            int bands = coverage.getSampleDimensions().length;
            final double[] evaluated = new double[bands];
            try {
                coverage.evaluate(p, evaluated);
            } catch (Exception e) {
                e.printStackTrace();
            }
            final GridCoordinates2D gridCoord = gridGeometry.worldToGrid(new DirectPosition2D(p));
            sb.append("Coverage info:\n\n");
            int length = evaluated.length;
            if (length > 1) {
                for( int i = 0; i < evaluated.length; i++ ) {
                    sb.append("Band ").append(i);
                    sb.append(" = ").append(evaluated[i]).append("\n");
                }
            } else if (length == 1) {
                sb.append("\tValue");
                sb.append(" = ").append(evaluated[0]).append("\n\n");
            }
            sb.append("\tin coordinates (easting, northing):\n");
            sb.append("\t").append(formatter.format(envelopeCenterOrig.x));
            sb.append(", ");
            sb.append(formatter.format(envelopeCenterOrig.y));
            sb.append("\n\n");
            sb.append("\tand grid coordinates (col, row):\n");
            sb.append("\t").append(gridCoord.x);
            sb.append(", ");
            sb.append(gridCoord.y);
            sb.append("\n");
        } else {
            sb.append("Selected point is outside of coverage region.");
        }
        CoveragePointInfo info = new CoveragePointInfo(layer){
            public String getInfo() {
                return sb.toString();
            }
        };
        return info;
    }
    private static Coordinate transform( CoordinateReferenceSystem sourceCRS, CoordinateReferenceSystem targetCRS,
            Coordinate envelopeCenterOrig ) throws TransformException {
        Coordinate evaluateCoord = envelopeCenterOrig;
        if (!CRS.equalsIgnoreMetadata(sourceCRS, targetCRS)) {
            try {
                MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
                evaluateCoord = JTS.transform(envelopeCenterOrig, null, transform);
                return evaluateCoord;
            } catch (FactoryException e1) {
                return null;
            }
        }
        return evaluateCoord;
    }

    /**
     * Utility method to create read parameters for {@link GridCoverageReader} 
     * 
     * @param width the needed number of columns.
     * @param height the needed number of columns.
     * @param north the northern boundary.
     * @param south the southern boundary.
     * @param east the eastern boundary.
     * @param west the western boundary.
     * @param crs the {@link CoordinateReferenceSystem}. Can be null, even if it should not.
     * @return the {@link GeneralParameterValue array of parameters}.
     */
    public static GeneralParameterValue[] createGridGeometryGeneralParameter( int width, int height, double north, double south,
            double east, double west, CoordinateReferenceSystem crs ) {
        GeneralParameterValue[] readParams = new GeneralParameterValue[1];
        Parameter<GridGeometry2D> readGG = new Parameter<GridGeometry2D>(AbstractGridFormat.READ_GRIDGEOMETRY2D);
        GridEnvelope2D gridEnvelope = new GridEnvelope2D(0, 0, width, height);
        Envelope env;
        if (crs != null) {
            env = new ReferencedEnvelope(west, east, south, north, crs);
        } else {
            DirectPosition2D minDp = new DirectPosition2D(west, south);
            DirectPosition2D maxDp = new DirectPosition2D(east, north);
            env = new Envelope2D(minDp, maxDp);
        }
        readGG.setValue(new GridGeometry2D(gridEnvelope, env));
        readParams[0] = readGG;

        return readParams;
    }
}
