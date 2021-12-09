/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tutorials.examples;

import org.eclipse.core.runtime.IProgressMonitor;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.Interaction;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.internal.Layer;

public class LayerExample {

    public void interactionExample(ILayer layer) {
        if (layer.getInteraction(Interaction.BACKGROUND)) {
            // layer is intended as a background layer

            // We can clear the background setting using a custom command
            IMap map = layer.getMap();
            final Layer modifyLayer = (Layer) layer;
            map.sendCommandASync(new AbstractCommand() {
                @Override
                public void run(IProgressMonitor monitor) throws Exception {
                    modifyLayer.setInteraction(Interaction.BACKGROUND, false);
                }

                @Override
                public String getName() {
                    return "Clear Interaction"; //$NON-NLS-1$
                }
            });
        }
    }
}
