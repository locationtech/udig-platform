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
package net.refractions.udig.tutorials.tool.coverage;

import java.io.IOException;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.UndoableComposite;
import net.refractions.udig.tools.edit.commands.SelectFeatureCommand;
import net.refractions.udig.tools.edit.commands.SelectionParameter;
import net.refractions.udig.tools.edit.commands.SelectionStrategy;
import net.refractions.udig.tools.edit.support.EditBlackboard;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.spatial.BBOX;
import org.opengis.geometry.BoundingBox;

/**
 * This selection strategy uses the provided feature to query the FeatureSource
 * for this layer.
 * <p>
 * The features returned by the FeatureSource are added as individual SelectFeatureCommands
 * to the provided UndoableComposite.
 */
public class SelectNeightborsStrategy implements SelectionStrategy {

	public void run(IProgressMonitor monitor, final UndoableComposite commands,
			SelectionParameter parameters, SimpleFeature feature,
			boolean firstFeature) {
		if (firstFeature) {
			try {
				ILayer editLayer = parameters.handler.getEditLayer();
				FeatureCollection<SimpleFeatureType, SimpleFeature> features = getFeatureIterator(
						monitor, editLayer, feature.getBounds());
						
				final EditBlackboard blackboard = parameters.handler.getEditBlackboard(editLayer);        
				features.accepts( new FeatureVisitor(){
                    public void visit( Feature feature ) {
                        SimpleFeature next = (SimpleFeature) feature;
                        SelectFeatureCommand selectFeatureCommand = new SelectFeatureCommand(
                                blackboard, next);
                        commands.addCommand(selectFeatureCommand);                        
                    }				    
				}, null );
			} catch (Exception e) {
				// this is a tutorial so we're just ignoring this issue :)
			}
		}
	}

@SuppressWarnings("unchecked")
private FeatureCollection<SimpleFeatureType, SimpleFeature> getFeatureIterator( IProgressMonitor monitor, ILayer editLayer, BoundingBox bounds ) throws IOException {
    FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
    
    SimpleFeatureType featureType = editLayer.getSchema();    
    String geomName = featureType.getGeometryDescriptor().getLocalName();
    
    PropertyName attributeExpr = ff.property(geomName);
    BBOX filter = ff.bbox(attributeExpr, bounds);
    
    FeatureSource<SimpleFeatureType, SimpleFeature> source = editLayer.getResource(FeatureSource.class, monitor);    
    return source.getFeatures(filter);
}


}
