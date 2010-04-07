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
package net.refractions.udig.project;

/**
 * An event that indicates a change to the map.  See the Enum for more information of the types of events taht 
 * are raised.  
 * 
 * @author Jesse
 * @see MapEventType
 * @since 1.1.0
 */
public class MapEvent extends UDIGEvent {

    public static enum MapEventType{
        /**
         * Type indicating the containing project of the map has changed.
         */
        PROJECT,
        /**
         * Type indicating the name of the map has changed.
         */
        NAME,
        /**
         * Type indicating the abstract of the map has changed.
         */
        ABSTRACT,
        /**
         * Type indicating a NavCommand has been executed. 
         */
        NAV_COMMAND,
        /**
         * Type indicating a MapCommand has been executed. 
         */
        MAP_COMMAND,
        /**
         * Type indicating the LayerFactory of the map has changed.
         */
        LAYER_FACTORY,
        /**
         * Type indicating the ViewportModel of the map has changed.
         */
        VIEWPORT_MODEL,
        /**
         * Type indicating the EditManager of the map has changed.
         */
        EDIT_MANAGER,
        /**
         * Type indicating the RenderManager of the map has changed.
         */
        RENDER_MANAGER,
        /**
         * Type indicating the Colour Palette used by the map has changed.
         */
        COLOR_PALETTE,
        /**
         * Type indicating the Colour Scheme used by the map has changed.
         */
        COLOUR_SCHEME,
    }
    private final MapEventType type;
    /**
     * Construct <code>EditManagerEvent</code>.
     * 
     * @param source the object that raised the event.
     * @param type the type of event this object represents.
     * @param newValue the new value, if this applies.
     * @param oldValue the old value, if this applies.
     */
    public MapEvent( IMap source, MapEventType type, Object newValue, Object oldValue ) {
        super(source, newValue, oldValue);
        this.type=type;
    }

    /**
     * Gets the type of the event.
     * 
     * @return the type of the event.
     * @see MapEventType
     */
    public MapEventType getType() {
        return type;
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
