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

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMapCompositionListener;
import org.locationtech.udig.project.MapCompositionEvent;
import org.locationtech.udig.project.MapCompositionEvent.EventType;
import org.locationtech.udig.project.internal.ContextModel;
import org.locationtech.udig.project.internal.ContextModelListenerAdapter;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.ProjectPlugin;

import org.eclipse.emf.common.notify.Notification;

/**
     * Listens for EMF events that indicate map composition changes and creates and fires the associated MapCompositionEvents 
     * @author jesse
     */
    final class EMFCompositionEventToMapCompositionEventListener extends
			ContextModelListenerAdapter {
		/**
		 * 
		 */
		private final MapImpl map;

		/**
		 * @param mapImpl
		 */
		EMFCompositionEventToMapCompositionEventListener(MapImpl mapImpl) {
			map = mapImpl;
		}

		/**
         * @see org.locationtech.udig.project.internal.ContextModelListenerAdapter#notifyChanged(org.eclipse.emf.common.notify.Notification)
         */
        @SuppressWarnings("deprecation")
        public void notifyChanged( Notification msg ) {
            if (! (msg.getNotifier() instanceof ContextModel)) {
                throw new IllegalArgumentException( "This listener should only be added to Context Models" ); //$NON-NLS-1$
            }
            
            fireEventToCompositionListeners(msg);
            if( map.eResource()!=null )
                map.eResource().setModified(true);
        }

		private void notifyCompositionListeners( MapCompositionEvent event ){
	        for( IMapCompositionListener listener : map.compositionListeners ) {
	            try{
	                listener.changed(event);
	            } catch (Throwable t) {
	                ProjectPlugin.log("", t); //$NON-NLS-1$
	            }
	        }
	    }

		@SuppressWarnings("deprecation")
        private void fireEventToCompositionListeners( Notification msg ) {
            if( msg.getFeatureID(ContextModel.class)==ProjectPackage.CONTEXT_MODEL__LAYERS ){
                switch( msg.getEventType() ) {
                case Notification.ADD:
                    notifyCompositionListeners(new MapCompositionEvent(map, EventType.ADDED, 
                            msg.getNewValue(), msg.getOldValue(), (ILayer) msg.getNewValue()));
                    break;
                case Notification.ADD_MANY:
                    notifyCompositionListeners(new MapCompositionEvent(map, EventType.MANY_ADDED, 
                            msg.getNewValue(), msg.getOldValue(), null));
                    break;
                case Notification.REMOVE:
                    notifyCompositionListeners(new MapCompositionEvent(map, EventType.REMOVED, 
                            msg.getNewValue(), msg.getOldValue(), (ILayer)msg.getOldValue()));
                    break;
                case Notification.REMOVE_MANY:
                    notifyCompositionListeners(new MapCompositionEvent(map, EventType.MANY_REMOVED, 
                            msg.getNewValue(), msg.getOldValue(), null));
                    break;
                case Notification.MOVE:
                    notifyCompositionListeners(new MapCompositionEvent(map, EventType.REORDERED, 
                            msg.getPosition(), msg.getOldValue(), (ILayer)msg.getNewValue()));                    
                    break;

                default:
                    break;
                }
            }
        }

	}
