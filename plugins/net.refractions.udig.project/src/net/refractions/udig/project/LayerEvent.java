/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project;

import java.util.EventObject;

import net.refractions.udig.project.internal.ProjectPackage;

import org.eclipse.emf.common.notify.Notification;
import org.geotools.data.FeatureEvent;

/**
 * An event indicating a change in a layer.
 * 
 * @author Jesse
 * @since 1.0.0
 */
public class LayerEvent extends EventObject {
    /** <code>serialVersionUID</code> field */
    private static final long serialVersionUID = 3762247556733614390L;

    public static enum EventType{
        /**
         * Indicates the Filter of the layer has changed.
         */
        FILTER,
        /**
         * Indicates a Resource has changed
         */
        RESOURCE,
        /**
         * Indicates the style changed
         */
        STYLE,
        /**
         * Indicates the zorder of the layer has changed
         */
        ZORDER,
        /**
         * Indicates the visibility of the layer has changed
         */
        VISIBILITY,
        /**
         * Indicates the name of the layer has changed
         */
        NAME,
        /**
         * Indicates "something" changed.  Inspect the layer.  Shouldn't be used often. 
         */
        ALL,
        /**
         * Indicates that and edit event has occurred
         * OldValue will always be null. 
         * NewValue will always be a {@link FeatureEvent}
         */
        EDIT_EVENT
    }
    
    /** Type of event, FILTER, RESOURCE, ALL or STYLE */
    private EventType type;

    /**
     * Previous value of the changed field, may be null if unknown.
     * <p>
     * Remeber the event is issued after the change has taken place
     * </p>
     */
    private Object oldValue;

    /**
     * Current value of changed field, may be null if unknown.
     * <p>
     * Remeber the event is issued after the change has taken place
     * </p>
     */
    private Object newValue;

    // API enum?
    /** Indicate a change has occured the the filter (defines the selection) */
    public static final int FILTER = ProjectPackage.LAYER__FILTER;

    /** Resource used by the layer has changed */
    public static final int RESOURCE = ProjectPackage.LAYER__GEO_RESOURCE;

    /** Indicates something somewhere is different - make no assumptions */
    public static final int ALL = Notification.NO_FEATURE_ID;

    /**
     * The contents of the blackboard have changed.
     */
    public static final int STYLE = ProjectPackage.LAYER__STYLE_BLACKBOARD;

    /**
     * Creates a new event for the given source, indicating that all labels provided by the source
     * are no longer valid and should be updated.
     * 
     * @param source the label provider
     */
    public LayerEvent( ILayer layer ) {
        this(layer, EventType.ALL);
    }

    /** Creates a specific kind of layer event, FILTER, RESOURCE, ALL or STYLE */
    public LayerEvent( ILayer layer, EventType type ) {
        this(layer, type, null, null);
    }

    /** Creates a specific kind of layer event, FILTER, RESOURCE, ALL or STYLE */
    public LayerEvent( ILayer layer, EventType type, Object oldValue, Object newValue ) {
        super(layer);
        this.type = type;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * The layer being modified.
     * 
     * @return The modified layer
     * @see ILayer
     */
    public ILayer getSource() {
        return (ILayer) super.getSource();
    }

    /**
     * @return the newValue.
     */
    public Object getNewValue() {
        return newValue;
    }

    /**
     * @param newValue The newValue to set.
     */
    public void setNewValue( Object newValue ) {
        this.newValue = newValue;
    }

    /**
     * @return Returns the oldValue.
     */
    public Object getOldValue() {
        return oldValue;
    }

    /**
     * @param oldValue The oldValue to set.
     */
    public void setOldValue( Object oldValue ) {
        this.oldValue = oldValue;
    }

    /**
     * Type of event
     */
    public EventType getType() {
        return type;
    }

    /**
     * Type of event
     * 
     * @see EventType
     * @param type
     */
    public void setType( EventType type ) {
        this.type = type;
    }
}
