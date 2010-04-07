package net.refractions.udig.project.internal.impl;

import net.refractions.udig.project.IMapListener;
import net.refractions.udig.project.MapEvent;
import net.refractions.udig.project.MapEvent.MapEventType;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.ProjectPlugin;

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