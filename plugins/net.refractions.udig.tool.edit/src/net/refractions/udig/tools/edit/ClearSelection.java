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
import net.refractions.udig.project.ui.tool.AbstractActionTool;
import net.refractions.udig.project.ui.tool.IToolContext;

/**
 * Action that clears the selection of the map.
 * 
 * @author Jesse
 * @since 1.0.0
 */
public class ClearSelection extends AbstractActionTool {

    private EditToolHandler handler = null;

    public ClearSelection() {
    }

    public ClearSelection( EditToolHandler handler ) {

        assert handler != null : "handler can't be null.";
        this.handler = handler;
    }

    public void run() {

        IToolContext context = null;

        // if the constructor #ClearSelection(EditToolHandler) isn't called, get the context using
        // getContext, else get the handler context.

        context = (handler == null) ? getContext() : handler.getContext();

        assert context != null;
        List<ILayer> layers = context.getMap().getMapLayers();
        for( ILayer layer : layers ) {
            EditBlackboardUtil.getEditBlackboard(context, layer).clear();
            layer.getBlackboard().put(ProjectBlackboardConstants.LAYER__RENDERING_FILTER, null);
        }
        context.getMap().getBlackboard().put(EditToolHandler.CURRENT_SHAPE, null);
        context.getMap().getBlackboard().put(EditToolHandler.EDITSTATE, EditState.NONE);
        context.getMap().getBlackboard().put(ProjectBlackboardConstants.MAP__RENDERING_FILTER,
                null);
        context.sendASyncCommand(context.getEditFactory().createNullEditFeatureCommand());
        context.sendASyncCommand(context.getSelectionFactory().createNoSelectCommand());

        // this code should not be necessary as it should be caught by the events
        // IRenderManager manager = getContext().getMap().getRenderManager();
        // if(manager!=null){
        // manager.refresh(null);
        // }

    }

    public void dispose() {
    }

}
