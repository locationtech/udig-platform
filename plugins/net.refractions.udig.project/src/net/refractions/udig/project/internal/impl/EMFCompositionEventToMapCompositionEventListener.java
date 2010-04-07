package net.refractions.udig.project.internal.impl;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMapCompositionListener;
import net.refractions.udig.project.MapCompositionEvent;
import net.refractions.udig.project.MapCompositionEvent.EventType;
import net.refractions.udig.project.internal.ContextModel;
import net.refractions.udig.project.internal.ContextModelListenerAdapter;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.ProjectPlugin;

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
         * @see net.refractions.udig.project.internal.ContextModelListenerAdapter#notifyChanged(org.eclipse.emf.common.notify.Notification)
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