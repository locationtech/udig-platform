/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tutorials.examples;

import org.eclipse.core.runtime.IProgressMonitor;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.Interaction;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.internal.Layer;

public class LayerExample {

    public void interactionExample(ILayer layer) {
        if (layer.getInteraction(Interaction.BACKGROUND)) {
            // layer is intended as a background layer
            
            // We can clear the background setting using a custom command
            IMap map = layer.getMap();
            final Layer modifyLayer = (Layer) layer;
            map.sendCommandASync( new AbstractCommand(){
                public void run(IProgressMonitor monitor) throws Exception {
                    modifyLayer.setInteraction( Interaction.BACKGROUND, false );
                }
                public String getName() {
                    return "Clear Interaction";
                }
            });
        }
    }
}
