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
 * A superclass for udig model events.
 * @author Jesse
 * @since 1.1.0
 */
public abstract class UDIGEvent {

    final protected Object source;
    final protected Object oldValue;
    final protected Object newValue;

    protected UDIGEvent( Object source2, Object newValue2, Object oldValue2){
        this.source=source2;
        this.newValue=newValue2;
        this.oldValue=oldValue2;
    }
    
    /**
     * Gets the new value, if it applies.
     * 
     * @return The new value, if it applies.
     */
    public Object getNewValue() {
        return newValue;
    }

    /**
     * Gets the old value, if it applies.
     * 
     * @return The old value, if it applies.
     */
    public Object getOldValue() {
        return oldValue;
    }

    /**
     * Gets the source of the event.
     * 
     * @return the source of the event.
     */
    public abstract Object getSource();

}
