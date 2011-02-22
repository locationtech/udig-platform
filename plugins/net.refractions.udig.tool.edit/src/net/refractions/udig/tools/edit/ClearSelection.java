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
package net.refractions.udig.tools.edit;

import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ProjectBlackboardConstants;
import net.refractions.udig.project.render.IRenderManager;
import net.refractions.udig.project.ui.tool.AbstractActionTool;

/**
 * Action that clears the selection of the map.
 *
 * @author Jesse
 * @since 1.0.0
 */
public class ClearSelection extends AbstractActionTool {

    public ClearSelection( ) {
    }

    public void run() {
        List<ILayer> layers = getContext().getMap().getMapLayers();
        for (ILayer layer : layers) {
        	EditBlackboardUtil.getEditBlackboard(getContext(), layer).clear();
        	layer.getBlackboard().put(ProjectBlackboardConstants.LAYER__RENDERING_FILTER, null);
		}
        getContext().getMap().getBlackboard().put(EditToolHandler.CURRENT_SHAPE, null);
        getContext().getMap().getBlackboard().put(EditToolHandler.EDITSTATE, EditState.NONE);
        getContext().getMap().getBlackboard().put(ProjectBlackboardConstants.MAP__RENDERING_FILTER, null);
        getContext().sendASyncCommand(getContext().getEditFactory().createNullEditFeatureCommand());
        getContext().sendASyncCommand(getContext().getSelectionFactory().createNoSelectCommand());
        IRenderManager manager = getContext().getMap().getRenderManager();
        if(manager!=null){
            manager.refresh(null);
        }

    }

	public void dispose() {
	}



}
