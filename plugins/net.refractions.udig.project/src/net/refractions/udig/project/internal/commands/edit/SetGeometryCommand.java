/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.commands.edit;

import net.refractions.udig.core.IBlockingProvider;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.UndoableCommand;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Sets the geometry attribute of a feature.
 * 
 * @author jeichar
 * @since 0.7
 */
public class SetGeometryCommand extends SetAttributeCommand implements UndoableCommand {
    /** The id for the DefaultGeometry */
    public static final String DEFAULT = "DEFAULT_GEOMETRY"; //$NON-NLS-1$

    /**
     * Creates a new instance of SetGeomteryCommand.
     * @param layer 
     * @param feature 
     * 
     * @param xpath the xpath which identifies the geometry to change. if <code>DEFAULT</code> the
     *        default geometry will be set.
     * @param geom the new geometry in the layer CRS.
     */
    public SetGeometryCommand( IBlockingProvider<SimpleFeature> feature, IBlockingProvider<ILayer> layer, String xpath, Geometry geom ) {
        super(feature, layer, xpath, geom);
    }
    
    /**
     * Creates a new instance of SetGeomteryCommand.
     * @param evaluationObject 
     * @param feature 
     * 
     * @param xpath the xpath which identifies the geometry to change. if <code>DEFAULT</code> the
     *        default geometry will be set.
     * @param geom the new geometry in the layer CRS.
     */
    public SetGeometryCommand( String xpath, Geometry geom ) {
        super(xpath, geom);
    }

    /**
     * @param featureID
     * @param layer
     * @param default2
     * @param geom
     */
    public SetGeometryCommand( String featureID, IBlockingProvider<ILayer> layer, String xpath, Geometry geom ) {
        super(featureID, layer, xpath, geom);
    }

    /**
     * Prep the geometry (srsName and default geometry name) prior setAttributeCommand.
     * 
     * @see net.refractions.udig.project.internal.command.MapCommand#run()
     */
    public void run( IProgressMonitor monitor ) throws Exception {
        SimpleFeatureType schema = editLayer.get(monitor).getSchema();
        GeometryDescriptor geometryDescriptor = schema.getGeometryDescriptor();
        if (xpath.equals(DEFAULT)){
            xpath = geometryDescriptor.getName().getLocalPart();
        }
        Geometry geometry = (Geometry) value;
        if( geometry.getUserData() == null ){
            CoordinateReferenceSystem crs = geometryDescriptor.getCoordinateReferenceSystem();
            if( crs != null ){
                String srsName = CRS.toSRS(crs);
                geometry.setUserData(srsName);
            }
        }
        super.run(monitor);
    }

}
