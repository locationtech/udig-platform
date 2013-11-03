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


/**
 * Represents a change on a IBlackboard. 
 * 
 * @author jones
 * @since 1.1.0
 */
public class BlackboardEvent {
    private final IBlackboard source;
    private final Object oldValue;
    private final Object key;
    private final Object newValue;
    
    public BlackboardEvent(IBlackboard source2, Object key2, Object oldValue2, Object newValue2){
        this.source=source2;
        this.oldValue=oldValue2;
        this.key=key2;
        this.newValue=newValue2;
    }

    /**
     * @return Returns the key.
     */
    public Object getKey() {
        return this.key;
    }

    /**
     * @return Returns the newValue.
     */
    public Object getNewValue() {
        return this.newValue;
    }

    /**
     * @return Returns the oldValue.
     */
    public Object getOldValue() {
        return this.oldValue;
    }

    /**
     * @return Returns the source.
     */
    public IBlackboard getSource() {
        return this.source;
    }
    
    
}
