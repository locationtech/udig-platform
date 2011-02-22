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
package net.refractions.udig.tool.select.internal;

import java.io.IOException;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ui.tool.AbstractActionTool;
import net.refractions.udig.tool.select.SelectPlugin;
import net.refractions.udig.ui.ProgressManager;

import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Sets the ViewportModel bounds to equal the bounds of the selected features
 * in the selected layer.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class ZoomSelection extends AbstractActionTool {

    public static final String ID = "net.refractions.udig.tool.default.show.selection"; //$NON-NLS-1$

    public ZoomSelection() {
    }

    public void run() {
        ILayer layer = getContext().getSelectedLayer();
        if ( layer.hasResource(FeatureSource.class)){
            try {
                FeatureSource resource = layer.getResource(FeatureSource.class, ProgressManager.instance().get());
                Query query = new DefaultQuery( resource.getSchema().getTypeName(), layer.getFilter(),
                        new String[]{resource.getSchema().getDefaultGeometry().getName()});
                Envelope bounds = resource.getBounds(query);
                if( bounds==null ){
                    bounds=resource.getFeatures(query).getBounds();
                }
                getContext().sendASyncCommand(getContext().getNavigationFactory().createSetViewportBBoxCommand(bounds,
                        layer.getCRS()));
            } catch (IOException e) {
                SelectPlugin.log("failed to obtain resource", e); //$NON-NLS-1$
            }

        }
    }

    public void dispose() {
    }

}
