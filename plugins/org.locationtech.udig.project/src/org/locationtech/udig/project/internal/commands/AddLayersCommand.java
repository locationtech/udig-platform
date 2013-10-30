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
package org.locationtech.udig.project.internal.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.LayerFactory;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.Messages;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.Trace;

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
