/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.project.ui.operations;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.command.factory.SelectionCommandFactory;
import net.refractions.udig.project.render.IViewportModel;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.Filter;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Selects all features in the layer.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class SelectAllFeaturesOp implements IOp {

    public void op( final Display display, Object target, IProgressMonitor monitor ) throws Exception {

        final ILayer layer = (ILayer) target;
        SimpleFeatureSource source = (SimpleFeatureSource) layer.getResource(FeatureSource.class, new NullProgressMonitor());

        SimpleFeatureType schema = source.getSchema();
        GeometryDescriptor geometryDescriptor = schema.getGeometryDescriptor();
        IViewportModel viewportModel = ApplicationGIS.getActiveMap().getViewportModel();
        ReferencedEnvelope bounds = viewportModel.getBounds();
        CoordinateReferenceSystem dataCrs = schema.getCoordinateReferenceSystem();
        
        ReferencedEnvelope newBounds = bounds.transform(dataCrs, true);
        
        String name = geometryDescriptor.getLocalName();
        Filter bboxFilter = getBboxFilter(name, newBounds);
        SelectionCommandFactory cmdFactory = SelectionCommandFactory.getInstance();
        UndoableMapCommand selectCommand = cmdFactory.createSelectCommand(layer, bboxFilter);
        
        IToolContext toolContext = ApplicationGIS.createContext(ApplicationGIS.getActiveMap());
        toolContext.sendASyncCommand(selectCommand);
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
