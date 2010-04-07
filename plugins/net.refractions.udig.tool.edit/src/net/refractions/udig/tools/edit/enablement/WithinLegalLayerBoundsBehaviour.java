/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.tools.edit.enablement;

import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EnablementBehaviour;
import net.refractions.udig.tools.edit.EventType;

import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

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
