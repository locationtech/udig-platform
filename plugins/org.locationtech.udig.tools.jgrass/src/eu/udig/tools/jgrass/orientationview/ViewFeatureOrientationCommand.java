/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.tools.jgrass.orientationview;

import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.geotools.data.FeatureSource;

import eu.udig.tools.jgrass.utils.AbstractHandlerCommand;

/**
 * 
 * Command for viewing orientation.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class ViewFeatureOrientationCommand extends AbstractHandlerCommand {

    public Object execute( ExecutionEvent event ) throws ExecutionException {
        final IOp op = new ViewFeatureOrientation();
        try {
            runOp(op, FeatureSource.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
