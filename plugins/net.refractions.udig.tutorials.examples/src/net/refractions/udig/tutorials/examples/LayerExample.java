/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
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
