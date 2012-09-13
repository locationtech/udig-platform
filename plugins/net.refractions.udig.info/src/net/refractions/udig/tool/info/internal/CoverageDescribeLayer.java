/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.refractions.udig.tool.info.internal;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.text.DecimalFormat;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.tool.info.CoveragePointInfo;

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

import com.vividsolutions.jts.geom.Coordinate;

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
        if (geoResource.canResolve(AbstractGridCoverage2DReader.class)) {
            AbstractGridCoverage2DReader reader = geoResource.resolve(AbstractGridCoverage2DReader.class, monitor);
            CoordinateReferenceSystem targetCrs = reader.getCrs();

            if (targetCrs != null) {
                evaluateCoord = transform(sourceCRS, targetCrs, envelopeCenterOrig);
            } else {
                evaluateCoord = envelopeCenterOrig;
                targetCrs = sourceCRS;
            }
            GeneralParameterValue[] parameterValues = createGridGeometryGeneralParameter(1, 1, evaluateCoord.y + 1,
                    evaluateCoord.y - 1, evaluateCoord.x + 1, evaluateCoord.x - 1, targetCrs);

            coverage = reader.read(parameterValues);
            
            /*
             * the following is done since the reader might read a singlwe pixel 
             * region and the gridcoordinate would be 0, 0 in that case. Later
             * we want to supply the gridcoordinate of the position in the whole
             * coverage. 
             */
            gridGeometry = new GridGeometry2D(reader.getOriginalGridRange(), reader.getOriginalEnvelope());
        }
        // else try with coverage
        else if (geoResource.canResolve(GridCoverage.class)) {
            coverage = (GridCoverage2D) geoResource.resolve(GridCoverage.class, monitor);
            CoordinateReferenceSystem targetCrs = coverage.getCoordinateReferenceSystem();
            gridGeometry = coverage.getGridGeometry();
            evaluateCoord = transform(sourceCRS, targetCrs, envelopeCenterOrig);
        }

        if (coverage == null) {
            return null;
        }

        Point2D p = new Point2D.Double(evaluateCoord.x, evaluateCoord.y);
        int bands = coverage.getSampleDimensions().length;
        final double[] evaluated = new double[bands];
        try {
            coverage.evaluate(p, evaluated);
        } catch (Exception e) {
            // TODO make this more nice
            return null;
        }
        final GridCoordinates2D gridCoord = gridGeometry.worldToGrid(new DirectPosition2D(p));

        CoveragePointInfo info = new CoveragePointInfo(layer){
            public String getInfo() {
                StringBuilder sb = new StringBuilder();
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
