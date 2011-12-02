package net.refractions.udig.project.internal.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.LayerFactory;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.Messages;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.Trace;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Used to process a series of objects into layers that can be inserted into the Map Layer list.
 * 
 * @author jody
 * @since 1.2.0
 */
public class AddLayersCommand extends AbstractCommand implements UndoableMapCommand {
    /** List of resources being turned into layers.*/
    Collection<?> resources;

    /** List of created layers */
    List<Layer> layers;

    /** target index where layers will be added; often from a DnD location */
    private int index;

    /** Referene to the selected Layer obtained from the EditManager */
    private Layer selection;

    /**
     * Creates a the command from a set of
     * 
     * @see Layer or
     * @see IGeoResource objects.
     * @param resources A list containing a combination of layers or resources.
     */
    public AddLayersCommand( Collection<?> resources ) {
        this(resources, -1);

    }

    /**
     * Creates a the command from a set of
     * 
     * @see Layer or
     * @see IGeoResource objects.
     * @param resources A list containing a combination of layers or resources.
     */
    public AddLayersCommand( Collection<?> resources, int i ) {
        this.resources = resources;
        index = i;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        Map map = getMap();
        if (layers == null) {
            layers = new ArrayList<Layer>();
            LayerFactory layerFactory = map.getLayerFactory();

            selection = (Layer) map.getEditManager().getSelectedLayer();

            for( Object o : resources ) {
                try {
                    Layer layer = null;
    
                    if (o instanceof IGeoResource) {
                        // ensure that the service is part of the Catalog so that the find method in
                        // layer turn into layer
                        IGeoResource resource = (IGeoResource) o;
                        layer = layerFactory.createLayer(resource);
                    }
                    if (o instanceof Layer) {
                        // leave as is
                        layer = (Layer) o;
                    }
    
                    if (layer != null) {
                        layers.add(layer);
                    }
                }
                catch (Throwable t){
                    ProjectPlugin.log("Unable to add "+o,t);
                }
            }
        }
        if (!layers.isEmpty()) {
            if (index < 0) {
                index = map.getLayersInternal().size();
            }
            trace();

            map.getLayersInternal().addAll(index, layers);
            map.getEditManagerInternal().setSelectedLayer(layers.get(0));
        }

    }

    private void trace() {
        if (ProjectPlugin.isDebugging(Trace.COMMANDS)) {
            List<String> ids = new ArrayList<String>();
            for( Layer layer : layers ) {
                ids.add(layer.getID().toString());
            }
            ProjectPlugin
                    .trace(getClass(),
                            "Adding " + layers.size() + " layers to map:" + getMap().getName() + ".  IDs=" + ids, null); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
        }
    }

    public String getName() {
        return Messages.AddLayersCommand_name;
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        getMap().getLayersInternal().removeAll(layers);
        getMap().getEditManagerInternal().setSelectedLayer(selection);
        layers.clear();
    }

    public List<Layer> getLayers() {
        return layers;
    }

}