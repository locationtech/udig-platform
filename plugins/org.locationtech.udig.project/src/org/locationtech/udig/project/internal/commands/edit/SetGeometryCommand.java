/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.commands.edit;

import org.locationtech.udig.core.IBlockingProvider;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.UndoableCommand;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Geometry;

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
     * Prepairs the geometry (srsName and default geometry name) prior setAttributeCommand.
     * 
     * @see org.locationtech.udig.project.internal.command.MapCommand#run()
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
