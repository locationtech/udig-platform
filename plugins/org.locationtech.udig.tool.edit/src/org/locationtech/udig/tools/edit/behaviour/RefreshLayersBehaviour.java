/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.behaviour;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ProjectBlackboardConstants;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.tools.edit.Behaviour;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.tools.edit.support.EditUtils;

import org.locationtech.jts.geom.Envelope;

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
