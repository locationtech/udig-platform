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
package net.refractions.udig.tools.edit.behaviour;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ProjectBlackboardConstants;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.tools.edit.Behaviour;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.EditUtils;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Refreshes the current edit layer so that the features on the Editblackboard are not rendered
 * by the renderer only by the edit tool feedback. 
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class RefreshLayersBehaviour implements Behaviour {

    public UndoableMapCommand getCommand( EditToolHandler handler ) {
        List<ILayer> layer = handler.getContext().getMapLayers();
        for( ILayer layer2 : layer ) {
            IBlackboard properties = layer2.getBlackboard();
            if( properties.get(ProjectBlackboardConstants.MAP__RENDERING_FILTER)!=null ){
                Envelope env=new Envelope();
                Set<String> fids=new HashSet<String>();
                for( EditGeom geom : handler.getEditBlackboard(layer2).getGeoms() ) {
                    if( env.isNull() ){
                        env.init(geom.getShell().getEnvelope() );
                    }else{
                        env.expandToInclude(geom.getShell().getEnvelope());
                    }
                    fids.add(geom.getFeatureIDRef().get());
                }
                EditUtils.instance.refreshLayer(layer2, fids, env, true, false);

            }
        }
        return null;
    }

    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        ProjectPlugin.log("", error); //$NON-NLS-1$
    }

    public boolean isValid( EditToolHandler handler ) {
        List<ILayer> layer = handler.getContext().getMapLayers();
        for( ILayer layer2 : layer ) {
            if( layer2.getBlackboard().get(ProjectBlackboardConstants.MAP__RENDERING_FILTER)!=null )
                return true;
        }
        return false;
    }

}
