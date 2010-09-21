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
package net.refractions.udig.project.tests.ui;

import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.render.IRenderer;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * Test RenderManager getRenderer
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class TestGetRenderers extends ActionDelegate implements IWorkbenchWindowActionDelegate {


    @Override
    public void run( IAction action ) {
        IMap map = ApplicationGIS.getActiveMap();
        List<IRenderer> renderers = map.getRenderManager().getRenderers();
        List<ILayer> layers = map.getMapLayers();
        if( renderers.size()!=layers.size() ){
            System.out.println("Renderers.size()="+renderers.size()+" layers.size()="+layers.size()); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        for( IRenderer renderer : renderers ) {
            if( !layers.contains(renderer.getContext().getLayer()) )
                System.out.println(renderer.getContext().getLayer()+" is not one of the layers in the map"); //$NON-NLS-1$
        }
        
        
    }
    
    public void init( IWorkbenchWindow window ) {
    }

}
