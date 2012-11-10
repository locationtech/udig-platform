/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.issues.listeners;

import net.refractions.udig.issues.IIssue;

public class AbstractIssueEvent {

    protected final IIssue source;
    protected final Object newValue;
    protected final Object oldValue;

    /**
     * 
     * @param source the issue has changed
     * @param newValue the new value after the change
     * @param oldValue the value before the change
     */
    public AbstractIssueEvent(IIssue source2, Object newValue2, Object oldValue2) {
        this.source=source2;
        this.newValue=newValue2;
        this.oldValue=oldValue2;
    }

    /**
     * Returns the new value after the change
     *
     * @return the new value after the change
     */
    public Object getNewValue() {
        return newValue;
    }

    /**
     * Returns the value before the change
     *
     * @return the value before the change
     */
    public Object getOldValue() {
        return oldValue;
    }

    /**
     * Returns the issue has changed
     *
     * @return the issue has changed
     */
    public IIssue getSource() {
        return source;
    }

}