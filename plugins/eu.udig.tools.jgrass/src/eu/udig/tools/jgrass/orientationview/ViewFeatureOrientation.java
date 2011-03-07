/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package eu.udig.tools.jgrass.orientationview;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.render.IViewportModel;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.commands.AbstractDrawCommand;
import net.refractions.udig.project.ui.commands.IDrawCommand;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.Filter;
import org.opengis.geometry.BoundingBox;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Operation that draws arrows to show the orientation of the lines in a layer.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class ViewFeatureOrientation implements IOp {

    public void op( final Display display, Object target, IProgressMonitor monitor ) throws Exception {
        ILayer selectedLayer = (ILayer) target;
        SimpleFeatureSource featureSource = (SimpleFeatureSource) selectedLayer.getResource(FeatureSource.class,
                new SubProgressMonitor(monitor, 1));
        if (featureSource == null) {
            return;
        }
        GeometryDescriptor geometryDescriptor = featureSource.getSchema().getGeometryDescriptor();
        ReferencedEnvelope bounds = ApplicationGIS.getActiveMap().getViewportModel().getBounds();
        String name = geometryDescriptor.getLocalName();
        Filter bboxFilter = getBboxFilter(name, bounds);
        SimpleFeatureCollection featureCollection = featureSource.getFeatures(bboxFilter);

        FeatureIterator<SimpleFeature> featureIterator = featureCollection.features();
        IViewportModel viewPort = ApplicationGIS.getActiveMap().getViewportModel();
        List<AbstractDrawCommand> commands = new ArrayList<AbstractDrawCommand>();
        while( featureIterator.hasNext() ) {
            SimpleFeature feature = featureIterator.next();
            Geometry fGeom = (Geometry) feature.getDefaultGeometry();
            Coordinate[] coords = fGeom.getCoordinates();

            java.awt.Point start = viewPort.worldToPixel(coords[0]);
            java.awt.Point end = viewPort.worldToPixel(coords[coords.length - 1]);
            commands.add(new ArrowDrawCommand(new Coordinate(start.x, start.y), new Coordinate(end.x, end.y)));
        }

        IToolContext toolContext = ApplicationGIS.createContext(ApplicationGIS.getActiveMap());
        IDrawCommand compositeCommand = toolContext.getDrawFactory().createCompositeDrawCommand(commands);
        toolContext.sendASyncCommand(compositeCommand);
    }

    /**
     * Create a bounding box filter from a bounding box.
     * 
     * @param attribute the geometry attribute or null in the case of default "the_geom".
     * @param bbox the {@link BoundingBox}.
     * @return the filter.
     * @throws CQLException
     */
    public static Filter getBboxFilter( String attribute, BoundingBox bbox ) throws CQLException {
        double w = bbox.getMinX();
        double e = bbox.getMaxX();
        double s = bbox.getMinY();
        double n = bbox.getMaxY();

        return getBboxFilter(attribute, w, e, s, n);
    }

    /**
     * Create a bounding box filter from the bounds coordinates.
     * 
     * @param attribute the geometry attribute or null in the case of default "the_geom".
     * @param west western bound coordinate.
     * @param east eastern bound coordinate.
     * @param south southern bound coordinate.
     * @param north northern bound coordinate.
     * @return the filter.
     * @throws CQLException
     */
    @SuppressWarnings("nls")
    public static Filter getBboxFilter( String attribute, double west, double east, double south, double north )
            throws CQLException {

        if (attribute == null) {
            attribute = "the_geom";
        }

        StringBuilder sB = new StringBuilder();
        sB.append("BBOX(");
        sB.append(attribute);
        sB.append(",");
        sB.append(west);
        sB.append(",");
        sB.append(south);
        sB.append(",");
        sB.append(east);
        sB.append(",");
        sB.append(north);
        sB.append(")");

        Filter bboxFilter = CQL.toFilter(sB.toString());

        return bboxFilter;
    }
}
