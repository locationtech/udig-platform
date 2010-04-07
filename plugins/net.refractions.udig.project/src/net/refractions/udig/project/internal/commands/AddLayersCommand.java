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

public class AddLayersCommand extends AbstractCommand implements UndoableMapCommand{

    Collection<Object> resources;

    List<Layer> layers;

    private int index;

    private Layer selection;

    /**
     * Creates a the command from a set of
     * 
     * @see Layer or
     * @see IGeoResource objects.
     * @param resources A list containing a combination of layers or resources.
     */
    @SuppressWarnings("unchecked") 
    public AddLayersCommand( Collection resources ) {
        this( resources, -1);
        
    }

    /**
     * Creates a the command from a set of
     * 
     * @see Layer or
     * @see IGeoResource objects.
     * @param resources A list containing a combination of layers or resources.
     */
    @SuppressWarnings("unchecked") 
    public AddLayersCommand( Collection resources, int i ) {
        this.resources = resources;
        index=i;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        Map map = getMap();
        if( layers==null ){
            layers=new ArrayList<Layer>();
            LayerFactory layerFactory = map.getLayerFactory();
            
            selection = (Layer) map.getEditManager().getSelectedLayer();
            
            for( Object o : resources ) {
                Layer layer = null;
                
                if (o instanceof IGeoResource) {
                    
                    // ensure that the service is part of the catalog so that the find method in layer
                    // turn into layer
                    layer = layerFactory.createLayer((IGeoResource) o);
                }
                if (o instanceof Layer) {
                    // leave as is
                    layer = (Layer) o;
                }
    
                if (layer != null) {
                    layers.add(layer);
                }
                
            }
        }
        if (!layers.isEmpty()){
            if( index<0 ){
                index=map.getLayersInternal().size();
            }
            trace();
            
            map.getLayersInternal().addAll(index, layers);
            map.getEditManagerInternal().setSelectedLayer(layers.get(0));
        }

            
    }

    private void trace() {
        if( ProjectPlugin.isDebugging(Trace.COMMANDS)){
            List<String> ids=new ArrayList<String>();
            for( Layer layer : layers ) {
                ids.add(layer.getID().toString() );
            }
            ProjectPlugin.trace(getClass(), "Adding "+layers.size()+" layers to map:"+getMap().getName()+".  IDs="+ids, null);  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
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