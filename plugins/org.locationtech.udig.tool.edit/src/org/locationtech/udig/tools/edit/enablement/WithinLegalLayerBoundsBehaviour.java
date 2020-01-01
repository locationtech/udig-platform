/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.enablement;

import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.EnablementBehaviour;
import org.locationtech.udig.tools.edit.EventType;

import org.opengis.referencing.operation.MathTransform;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

/**
 * Sets the EditState to illegal if the Mouse moves into an area of the map where the layer is no
 * longer valid as according to its CRS. In addition puts a warning on the StatusLine.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class WithinLegalLayerBoundsBehaviour implements EnablementBehaviour {

    Map<ILayer, Envelope> cache = new WeakHashMap<ILayer, Envelope>();
    private EditState previousState = EditState.NONE;

    private boolean canTransformTo( ILayer selectedLayer, Coordinate world ) {
        MathTransform t;
        try {
            t = selectedLayer.mapToLayerTransform();
        } catch (IOException e1) {
            throw (RuntimeException) new RuntimeException().initCause(e1);
        }
        try {
            double[] dest = new double[2];
            t.transform(new double[]{world.x, world.y}, 0, dest, 0, 1);
            if( Double.isNaN(dest[0]) || Double.isNaN(dest[1])
                    || Double.isInfinite(dest[0]) || Double.isInfinite(dest[1]))
                return false;
            return true;
        } catch (Throwable throwable) {
            return false;
        }
    }

    public String isEnabled( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        if (eventType!=EventType.MOVED  && eventType!=EventType.ENTERED)
            return null;
        ILayer editLayer = handler.getEditLayer();
        Envelope env = cache.get(editLayer);

        Coordinate world = handler.getContext().pixelToWorld(e.x, e.y);
        if (env != null && env.contains(world)) {
        } else {
            if (canTransformTo(editLayer, world)) {
                // ok its at least somewhat legal, we can transform to it so lets expand the legal
                // envelope.
                if (env == null) {
                    env = new Envelope();
                }
                env.expandToInclude(world);
            } else
                return "Cannot edit.  Either in a bad projection for editing layer or out of the bounds for the layer";
        }
        return null;

    }

}
