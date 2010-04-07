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
 * <pre><code>
 * contextModel.eAdapters().add(new ContextModelListenerAdapter(){
 *     protected void layerAdded( Notification msg ) {
 *         //Enter behaviour
 *     }
 * });
 * </code></pre>
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
 * <pre><code>
 * contextModel.getDeepAdapters().add(new ContextModelListenerAdapter(){
 *     protected void styleChanged( Notification msg ) {
 *         //Enter behaviour
 *     }
 * });
 * </code></pre>
 * 
 * </p>
 * 
 * @author jeichar
 * @since 0.3
 */

public class ContextModelListenerAdapter extends AdapterImpl {

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
        }// if
        else if (msg.getNotifier() instanceof Layer) {
            switch( msg.getFeatureID(Layer.class) ) {
            case ProjectPackage.LAYER__GLYPH: {
                glyphChanged(msg);
                break;
            }// case
            case ProjectPackage.LAYER__STYLE_BLACKBOARD: { // FIXME: Changed from LAYER__STYLE
                styleChanged(msg);
                break;
            }// case
            case ProjectPackage.LAYER__VISIBLE: {
                visibilityChanged(msg);
                break;
            }// case
            }// switch

        }// else
    }

    /**
     * This method is called when a single layer has been added.
     * <p>
     * msg.getNewValue() will return the layer added
     * </p>
     * 
     * @param msg The Notification object with all the information about the event.
     */
    protected void layerAdded( Notification msg ) {
        // do nothing
    }

    /**
     * This method is called when more than on layer has been added.
     * <li>msg.getNewValue() will return a list of the layers added</li>
     * <li>msg.getNotifier() will return the ContextModel</li>
     * </ul>
     * </p>
     * 
     * @param msg The Notification object with all the information about the event.
     */
    protected void manyLayersAdded( Notification msg ) {
        // do nothing
    }

    /**
     * This method is called when a single layer has been removed.
     * <p>
     * <ul>
     * <li>msg.getOldValue() will return the layer removed</li>
     * <li>msg.getNotifier() will return the ContextModel</li>
     * </ul>
     * </p>
     * 
     * @param msg The Notification object with all the information about the event.
     */
    protected void layerRemoved( Notification msg ) {
        // do nothing
    }

    /**
     * This method is called when a multiple layers have been removed.
     * <p>
     * <ul>
     * <li>msg.getOldValue() will return a list with the layers removed</li>
     * <li>msg.getNotifier() will return the ContextModel</li>
     * </ul>
     * </p>
     * 
     * @param msg The Notification object with all the information about the event.
     */
    protected void manyLayersRemoved( Notification msg ) {
        // do nothing
    }

    /**
     * The order of the layer in the list has been changed. Therefore the zorder of the layer has
     * been changed.
     * <p>
     * The closer to the beginning of the list the later the object will be rendered. In other words
     * the list is in reverse zorder
     * </p>
     * <p>
     * <ul>
     * <li>msg.getNewIntValue() will return the new position of the layer</li>
     * <li>msg.getOldIntValue() will return the old position of the layer</li>
     * <li>msg.getNotifier() will return the ContextModel</li>
     * </ul>
     * </p>
     * 
     * @param msg The Notification object with all the information about the event.
     */
    protected void zorderChanged( Notification msg ) {
        // do nothing
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
        // do nothing
    }

    /**
     * Called when the Style of a layer has been changed.
     * <p>
     * <ul>
     * <li>msg.getNewValue() will return the new Style</li>
     * <li>msg.getOldValue() will return the old Style</li>
     * <li>msg.getNotifier() will return the Layer</li>
     * </ul>
     * </p>
     * 
     * @param msg The Notification object with all the information about the event.
     */
    protected void styleChanged( Notification msg ) {
        // do nothing
    }

    /**
     * Called when the Visibility of a layer has been changed.
     * <p>
     * <ul>
     * <li>msg.getNewValue() will return the new Visibility</li>
     * <li>msg.getOldValue() will return the old Visibility</li>
     * <li>msg.getNotifier() will return the Layer</li>
     * </ul>
     * </p>
     * 
     * @param msg The Notification object with all the information about the event.
     */
    protected void visibilityChanged( Notification msg ) {
        // do nothing
    }
}
