/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.tests.ui;

import java.util.List;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.render.IRenderer;
import org.locationtech.udig.project.ui.ApplicationGIS;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;
import org.junit.Ignore;

/**
 * Test RenderManager getRenderer
 * 
 * @author Jesse
 * @since 1.1.0
 */
@Ignore
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
