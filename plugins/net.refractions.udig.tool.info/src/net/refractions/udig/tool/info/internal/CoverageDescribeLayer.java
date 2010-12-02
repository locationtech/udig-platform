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
import net.refractions.udig.tool.info.InfoPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.coverage.grid.GridCoordinates2D;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.InvalidGridGeometryException;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Coverage click info gathering class.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class CoverageDescribeLayer {

    public static CoveragePointInfo info2( ILayer layer, ReferencedEnvelope bbox, IProgressMonitor monitor ) throws IOException,
            InvalidGridGeometryException, TransformException {

        Envelope reprojected = null;
        try {
            reprojected = bbox.transform(layer.getMap().getViewportModel().getCRS(), true);
        } catch (Exception e) {
            InfoPlugin.log("", e); //$NON-NLS-1$
            return null;
        }

        final DecimalFormat formatter = new DecimalFormat("0.####");
        final Coordinate envelopeCenter = reprojected.centre();
        IGeoResource geoResource = layer.getGeoResource();
        if (geoResource.canResolve(GridCoverage.class)) {
            GridCoverage2D coverage = (GridCoverage2D) geoResource.resolve(GridCoverage.class, monitor);

            Point2D p = new Point2D.Double(envelopeCenter.x, envelopeCenter.y);
            int bands = coverage.getSampleDimensions().length;
            final double[] evaluated = new double[bands];
            try {
                coverage.evaluate(p, evaluated);
            } catch (Exception e) {
                // TODO make this more nice
                return null;
            }
            final GridCoordinates2D gridCoord = coverage.getGridGeometry().worldToGrid(new DirectPosition2D(p));

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
                    sb.append("\t").append(formatter.format(envelopeCenter.x));
                    sb.append(", ");
                    sb.append(formatter.format(envelopeCenter.y));
                    sb.append("\n\n");
                    sb.append("\tand grid coordinates (row, col):\n");
                    sb.append("\t").append(gridCoord.y);
                    sb.append(", ");
                    sb.append(gridCoord.x);
                    sb.append("\n");

                    return sb.toString();
                }

            };
            return info;
        }
        return null;
    }
}
