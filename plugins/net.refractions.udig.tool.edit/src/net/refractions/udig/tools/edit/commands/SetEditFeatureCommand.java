package net.refractions.udig.tools.edit.commands;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.Filter;
import org.geotools.filter.FilterFactory;
import org.geotools.filter.FilterFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * Sets the feature currently being edited in the edit manager.
 *
 * @author chorner
 * @since 1.1.0
 */
public class SetEditFeatureCommand extends AbstractCommand implements UndoableMapCommand {

    EditToolHandler handler;
    Point clickPoint;
    PrimitiveShape shape;
    Feature oldFeature;
    ILayer oldLayer;
    private Layer editLayer;
    private Filter oldFilter;

    /**
     * @deprecated
     */
    public SetEditFeatureCommand (EditToolHandler handler, MapMouseEvent e, PrimitiveShape shape) {
        this.handler = handler;
        this.clickPoint = Point.valueOf(e.x, e.y);
        this.shape = shape;
    }
    public SetEditFeatureCommand (EditToolHandler handler, Point clickPoint, PrimitiveShape shape) {
        this.handler = handler;
        this.clickPoint = clickPoint;
        this.shape = shape;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        IToolContext context = handler.getContext();
        java.awt.Point point = new java.awt.Point(clickPoint.getX(), clickPoint.getY());
        ReferencedEnvelope bbox = handler.getContext().getBoundingBox(point,7);
        FeatureCollection fc = context.getFeaturesInBbox(handler.getEditLayer(), bbox);
        FeatureIterator it = fc.features();
        Feature feature = null;
        while (it.hasNext()) {
            Feature feat = it.next();
            if (feat.getID().equals(shape.getEditGeom().getFeatureIDRef().toString())) {
                feature = feat;
                break;
            }
        }
        it.close();
        oldFeature = getMap().getEditManagerInternal().getEditFeature();
        oldLayer = getMap().getEditManagerInternal().getEditLayer();
        editLayer = (Layer) handler.getEditLayer();
        oldFilter = editLayer.getFilter();
        getMap().getEditManagerInternal().setEditFeature(feature, editLayer);
        editLayer.setFilter(fidFilter(feature));

    }

    private Filter fidFilter( Feature feature ) {
        FilterFactory factory = FilterFactoryFinder.createFilterFactory();
        Filter id = factory.createFidFilter(feature.getID());
        return id;
    }
    public String getName() {
        return "Set Current Selection";
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        getMap().getEditManagerInternal().setEditFeature(oldFeature, (Layer) oldLayer);
        editLayer.setFilter(oldFilter);
    }

}
