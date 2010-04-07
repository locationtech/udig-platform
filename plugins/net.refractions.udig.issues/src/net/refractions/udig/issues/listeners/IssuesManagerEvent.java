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
package net.refractions.udig.issues.listeners;

import net.refractions.udig.issues.IIssuesManager;

/**
 * Encapsulates data about IssuesManager event occurrance.
 * @author Jesse
 * @since 1.1.0
 */
public class IssuesManagerEvent {
    private IIssuesManager source;
    private IssuesManagerEventType type;
    private Object newValue;
    private Object oldValue;

    public IssuesManagerEvent( IIssuesManager source, IssuesManagerEventType type, Object newValue, Object oldValue){
        this.source=source;
        this.type=type;
        this.newValue=newValue;
        this.oldValue=oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public IIssuesManager getSource() {
        return source;
    }

    public IssuesManagerEventType getType() {
        return type;
    }
}
