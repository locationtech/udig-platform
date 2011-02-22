package net.refractions.udig.tutorials.tool.coverage;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.FeatureType;
import org.geotools.filter.AttributeExpression;
import org.geotools.filter.BBoxExpression;
import org.geotools.filter.FilterFactory;
import org.geotools.filter.FilterFactoryFinder;
import org.geotools.filter.FilterType;
import org.geotools.filter.GeometryFilter;
import org.geotools.filter.IllegalFilterException;

import com.vividsolutions.jts.geom.Envelope;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.UndoableComposite;
import net.refractions.udig.tools.edit.commands.SelectFeatureCommand;
import net.refractions.udig.tools.edit.commands.SelectionParameter;
import net.refractions.udig.tools.edit.commands.SelectionStrategy;
import net.refractions.udig.tools.edit.support.EditBlackboard;

public class SelectNeightborsStrategy implements SelectionStrategy {

public void run( IProgressMonitor monitor, UndoableComposite commands,
        SelectionParameter parameters, Feature feature, boolean firstFeature ) {
    if( firstFeature ){
        try {
            ILayer editLayer = parameters.handler.getEditLayer();
            FeatureIterator iterator = getFeatureIterator(monitor, editLayer, feature.getBounds());
            while( iterator.hasNext() ){
                EditBlackboard blackboard = parameters.handler.getEditBlackboard(editLayer);
                SelectFeatureCommand selectFeatureCommand = new SelectFeatureCommand(blackboard , iterator.next());
                commands.addCommand(selectFeatureCommand);
            }
        } catch (Exception e){
            // this is a tutorial so we're just ignoring this issue :)
        }
    }
}

private FeatureIterator getFeatureIterator( IProgressMonitor monitor, ILayer editLayer, Envelope bounds ) throws IOException, IllegalFilterException {
    FilterFactory factory = FilterFactoryFinder.createFilterFactory();

    BBoxExpression bboxExpr = factory.createBBoxExpression(bounds);
    FeatureType featureType = editLayer.getSchema();
    String geomName = featureType.getDefaultGeometry().getName();
    AttributeExpression attributeExpr = factory.createAttributeExpression(geomName);

    GeometryFilter filter = factory.createGeometryFilter(FilterType.GEOMETRY_BBOX);
    filter.addRightGeometry(bboxExpr);
    filter.addLeftGeometry(attributeExpr);

    FeatureSource source = editLayer.getResource(FeatureSource.class, monitor);

    FeatureCollection features = source.getFeatures(filter);

    return features.features();
}

}
