/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;

/**
 * An Adapter for ContextModels that allow other objects to listen to state changes that occur in
 * the ContextModel and its Layers.
 * <p>
 * See the example for how to use an this class as a listener.
 * </p>
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Listener for ContextModel state events</li>
 * <li>Call methods when an event occurs that can be mapped to one of the methods.
 * <p>
 * For example if a layer is added the layerAdded method is called.
 * </p>
 * </li>
 * </ul>
 * </p>
 * <p>
 * The following example adds an observer/listener that only reacts to single layer added events...
 * <b>Note that multiple layers being added are ignored </b>. Example Use:
 * 
 * <pre>
 * <code>
 * contextModel.eAdapters().add(new ContextModelListenerAdapter(){
 *     protected void layerAdded( Notification msg ) {
 *         //Enter behaviour
 *     }
 * });
 * </code>
 * </pre>
 * 
 * </p>
 * <p>
 * The following example adds an observer/listener that is notified of style change events that
 * occur in the layers contained by the ContextModel.
 * </p>
 * <p>
 * Note: getDeepAdapters() adds the listener/adapter/observer to the context model and all of the
 * children. Children that are later added or removed automatically have the deep adapter added or
 * removed.
 * </p>
 * <p>
 * Example Use:
 * 
 * <pre>
 * <code>
 * contextModel.getDeepAdapters().add(new ContextModelListenerAdapter(){
 *     protected void styleChanged( Notification msg ) {
 *         //Enter behaviour
 *     }
 * });
 * </code>
 * </pre>
 * 
 * </p>
 * 
 * @deprecated Please consider listening to the Map now that the ContextModel is not used
 * @author jeichar
 * @since 0.3
 */

public class ContextModelListenerAdapter extends LayerListListenerAdapter {

    /**
     * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyChanged( Notification msg ) {
        if (msg.getNotifier() instanceof ContextModel) {
            switch( msg.getFeatureID(ContextModel.class) ) {
            case ProjectPackage.CONTEXT_MODEL__LAYERS: {
                switch( msg.getEventType() ) {
                case Notification.ADD: {
                    layerAdded(msg);
                    break;
                }// case
                case Notification.ADD_MANY: {
                    manyLayersAdded(msg);
                    break;
                }// case
                case Notification.REMOVE: {
                    layerRemoved(msg);
                    break;
                }// case
                case Notification.REMOVE_MANY: {
                    manyLayersRemoved(msg);
                    break;
                }// case
                case Notification.MOVE: {
                    zorderChanged(msg);
                    break;
                }// case
                }// switch
                break;
            }// case
            }// switch
        }
        super.notifyChanged(msg); // process Map and Layer notifications
    }
    
    @Override
    protected void iconChanged( Notification msg ) {
        glyphChanged(msg); // for backwards compatibility
    }
    /**
     * Called when the Glyph of a layer has been changed.
     * <p>
     * <ul>
     * <li>msg.getNewValue() will return the new Glyph</li>
     * <li>msg.getOldValue() will return the old Glyph</li>
     * <li>msg.getNotifier() will return the Layer</li>
     * </ul>
     * </p>
     * 
     * @param msg The Notification object with all the information about the event.
     */
    protected void glyphChanged( Notification msg ) {
        iconChanged( msg );
    }

}
