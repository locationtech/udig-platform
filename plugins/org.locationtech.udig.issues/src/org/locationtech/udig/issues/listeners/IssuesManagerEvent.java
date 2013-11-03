/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues.listeners;

import org.locationtech.udig.issues.IIssuesManager;

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
