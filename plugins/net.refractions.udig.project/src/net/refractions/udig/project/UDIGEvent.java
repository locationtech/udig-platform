/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
