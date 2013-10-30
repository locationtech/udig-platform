/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project;

import org.locationtech.udig.project.MapEvent.MapEventType;

/**
 * Event encapsulating information about the changes of the composition of a map.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class MapCompositionEvent extends UDIGEvent{
    public static enum EventType{
        /**
         * Indicates that a layer was removed.  The oldValue will be the layer that was removed and the
         * newValue will be null.
         */
        REMOVED,
        /**
         * Indicates that more than one layer was removed.  The oldValue will be the layers removed, 
         * newValue will be an array of indices of the layers removed and layer will be null.
         */
        MANY_REMOVED,
        /**
         * Indicates that a layer was added.  The oldValue will be null and newValue will be the new layer.
         * Layer will be null.
         */
        ADDED,
        /**
         * Indicates that more than one layer was added.  The oldValue will be null, newValue will be 
         * a list of new layers and layer will be null.
         */
        MANY_ADDED,
        /**
         * Indicates that a layer was moved in the list of layers.  OldValue will be the old position, 
         * newValue will be the new position and layer will be the layer moved.
         * Layer will be null.
         */
        REORDERED
    }
    
    private final EventType type;
    private final ILayer layer;

    /**
     * Construct <code>EditManagerEvent</code>.
     * 
     * @param source the object that raised the event.
     * @param type the type of event this object represents.
     * @param newValue the new value, if this applies.
     * @param oldValue the old value, if this applies.
     */
    public MapCompositionEvent( IMap source, EventType type, Object newValue, Object oldValue, ILayer layer ) {
        super(source, newValue, oldValue);
        this.type=type;
        this.layer=layer;
    }

    /**
     * Gets the type of the event.
     * 
     * @return the type of the event.
     * @see MapEventType
     */
    public EventType getType() {
        return type;
    }

    /**
     * Returns the layer modified if the event type was {@link EventType#ADDED}, {@link EventType#REMOVED} or {@link EventType#REORDERED}.
     * @return Returns the layer.
     */
    public ILayer getLayer() {
        return layer;
    }    
    
    @Override
    public String toString() {
        return getSource().getName()+" eventType="+type+" old="+oldValue+" new="+newValue;   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
    }

    @Override
    public IMap getSource() {
        return (IMap) source;
    }
}
