/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;
import org.locationtech.udig.project.EditManagerEvent;
import org.locationtech.udig.project.IEditManagerListener;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.ui.ApplicationGIS;

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
