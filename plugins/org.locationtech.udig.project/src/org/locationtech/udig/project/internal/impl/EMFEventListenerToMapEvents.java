/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.impl;

import org.locationtech.udig.project.IMapListener;
import org.locationtech.udig.project.MapEvent;
import org.locationtech.udig.project.MapEvent.MapEventType;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.ProjectPlugin;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;

/**
 * Listens to the EMF events fired by maps and converts them into Map Events and fires the events.
 * @author jesse
 */
final class EMFEventListenerToMapEvents extends AdapterImpl {
	/**
	 * 
	 */
	private final MapImpl map;


	/**
	 * @param mapImpl
	 */
	EMFEventListenerToMapEvents(MapImpl mapImpl) {
		map = mapImpl;
	}


	@Override
	public void notifyChanged( Notification msg ) {
	    switch( msg.getFeatureID(Map.class) ) {
	    case ProjectPackage.MAP__ABSTRACT:
	        notifyMapListeners(new MapEvent(map, MapEventType.ABSTRACT, msg.getNewValue(), msg.getOldValue()));
	        break;
	    case ProjectPackage.MAP__COLOR_PALETTE:
	        notifyMapListeners(new MapEvent(map, MapEventType.COLOR_PALETTE, msg.getNewValue(), msg.getOldValue()));
	        break;
	    case ProjectPackage.MAP__COLOUR_SCHEME:
	        notifyMapListeners(new MapEvent(map, MapEventType.COLOUR_SCHEME, msg.getNewValue(), msg.getOldValue()));
	        break;
	    case ProjectPackage.MAP__COMMAND_STACK:
	        notifyMapListeners(new MapEvent(map, MapEventType.MAP_COMMAND, msg.getNewValue(), msg.getOldValue()));
	        break;
	    case ProjectPackage.MAP__EDIT_MANAGER_INTERNAL:
	        notifyMapListeners(new MapEvent(map, MapEventType.EDIT_MANAGER, msg.getNewValue(), msg.getOldValue()));
	        break;
	    case ProjectPackage.MAP__LAYER_FACTORY:
	        notifyMapListeners(new MapEvent(map, MapEventType.LAYER_FACTORY, msg.getNewValue(), msg.getOldValue()));
	        break;
	    case ProjectPackage.MAP__NAME:
	        notifyMapListeners(new MapEvent(map, MapEventType.NAME, msg.getNewValue(), msg.getOldValue()));
	        break;
	    case ProjectPackage.MAP__NAV_COMMAND_STACK:
	        notifyMapListeners(new MapEvent(map, MapEventType.NAV_COMMAND, msg.getNewValue(), msg.getOldValue()));
	        break;
	    case ProjectPackage.MAP__PROJECT_INTERNAL:
	        notifyMapListeners(new MapEvent(map, MapEventType.PROJECT, msg.getNewValue(), msg.getOldValue()));
	        break;
	    case ProjectPackage.MAP__RENDER_MANAGER_INTERNAL:
	        notifyMapListeners(new MapEvent(map, MapEventType.RENDER_MANAGER, msg.getNewValue(), msg.getOldValue()));
	        break;
	    case ProjectPackage.MAP__VIEWPORT_MODEL_INTERNAL:
	        notifyMapListeners(new MapEvent(map, MapEventType.VIEWPORT_MODEL, msg.getNewValue(), msg.getOldValue()));
	        break;
	    default:
	        break;
	    }
	}
	

    private void notifyMapListeners( MapEvent event ){
        for( IMapListener listener : map.mapListeners ) {
            try{
                listener.changed(event);
            } catch (Throwable t) {
                ProjectPlugin.log("", t); //$NON-NLS-1$
            }
        }
    }

}
