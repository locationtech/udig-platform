package net.refractions.udig.tools.edit.commands;

import java.util.Collection;

import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.EditUtils;
import net.refractions.udig.tools.edit.support.Point;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.geotools.feature.Feature;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Adds the feature's geometry to the edit blackboard (effectively selecting it).
 *
 * @author jones
 * @since 1.1.0
 */
public class SelectFeatureCommand extends AbstractCommand implements UndoableMapCommand {

    private Collection<EditGeom> added;
    private Feature feature;
    private EditBlackboard blackboard;
    private Layer editLayer;
    private Envelope refreshBounds;
    private EditToolHandler handler;
    private Point mouseLocation;
    private SetEditFeatureCommand setEditFeatureCommand;

    /**
     * new instance
     * @param handler used to obtain the blackboard for the currently selected layer.
     * @param feature the feature to add.
     * @param mouseLocation If not null it will set the current shape to be the Geom that intersects the mouseLocation
     */
    public SelectFeatureCommand( EditToolHandler handler, Feature feature, Point mouseLocation) {
        this.feature=feature;
        editLayer = (Layer) handler.getEditLayer();
        this.blackboard=handler.getEditBlackboard(editLayer);
        this.handler=handler;
        this.mouseLocation=mouseLocation;
    }

    /**
     * New Instance
     * @param blackboard blackboard to add features to.
     * @param feature2 the feature to add
     */
    public SelectFeatureCommand( EditBlackboard blackboard2, Feature feature2 ) {
        this.feature=feature2;
        this.blackboard=blackboard2;
    }


    public void run( IProgressMonitor monitor ) throws Exception {
        if (editLayer==null)
            editLayer=(Layer) getMap().getEditManager().getSelectedLayer();
        this.added=blackboard.addGeometry(feature.getDefaultGeometry(), feature.getID()).values();
        if( !added.isEmpty() && mouseLocation!=null ){
            Class<?> type = editLayer.getSchema().getDefaultGeometry().getType();
            boolean polygonLayer=Polygon.class.isAssignableFrom(type) || MultiPolygon.class.isAssignableFrom(type);
            EditGeom geom = EditUtils.instance.getGeomWithMouseOver(added, mouseLocation, polygonLayer);
            handler.setCurrentShape(geom.getShell());
            setEditFeatureCommand = new SetEditFeatureCommand(handler, mouseLocation, geom.getShell());
            setEditFeatureCommand.setMap(getMap());
            setEditFeatureCommand.run(SubMonitor.convert(monitor));

        }

        //make sure the display refreshes
        this.refreshBounds=feature.getBounds();
        EditUtils.instance.refreshLayer(editLayer, feature, refreshBounds, false, true);
    }

    public String getName() {
        return Messages.AddGeomCommand_name;
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        if( setEditFeatureCommand!=null ){
            setEditFeatureCommand.rollback(SubMonitor.convert(monitor));
        }
        blackboard.removeGeometries(added);

        refreshBounds.expandToInclude(feature.getBounds());
        EditUtils.instance.refreshLayer(editLayer, feature, refreshBounds, true, false);
    }

}
