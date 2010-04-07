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
package net.refractions.udig.project.ui.internal.actions;

import net.refractions.udig.project.EditManagerEvent;
import net.refractions.udig.project.IEditManagerListener;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.render.RenderManager;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * Turns on/off the Mylar effect
 * 
 * @author Jesse
 * @since 1.0.0
 */
public class MylarAction extends ActionDelegate implements IViewActionDelegate, IWorkbenchWindowActionDelegate {

    public static final String KEY = "MYLAR"; //$NON-NLS-1$

    
    IEditManagerListener selectedLayerListener = new IEditManagerListener(){
        
        public void changed( EditManagerEvent event ) {
            IMap map = event.getSource().getMap();
            if( map!=currentMap){
                map.getEditManager().removeListener(this);
            }
            if (event.getOldValue() != event.getNewValue()) {
                //update image
                ((RenderManager)map.getRenderManager()).refreshImage();
            }
        }

    };
    private Map currentMap;
    
    @Override
    public void runWithEvent( IAction action, Event event ) {
        currentMap = (Map) ApplicationGIS.getActiveMap();
        if (currentMap == ApplicationGIS.NO_MAP)
            return;
        Boolean temp = (Boolean)currentMap.getBlackboard().get(KEY);
        boolean currentStatus=temp==null?false:temp;
        if( !currentStatus ){
            currentMap.getEditManager().addListener(selectedLayerListener);
        }else{
            currentMap.getEditManager().removeListener(selectedLayerListener);
        }
        currentMap.getBlackboard().put(KEY, !currentStatus);
        action.setChecked(!currentStatus);
        //update image
        currentMap.getRenderManagerInternal().refreshImage();
    }

    public void init( IWorkbenchWindow window ) {
    }
    
    @Override
    public void selectionChanged( IAction action, ISelection selection ) {
        currentMap = (Map) ApplicationGIS.getActiveMap();
        if (currentMap == ApplicationGIS.NO_MAP)
            return;
        Boolean temp = (Boolean)currentMap.getBlackboard().get(KEY);

        action.setChecked(temp==null?false:temp);
        
    }

    public void init( IViewPart view ) {
    }

}
