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
