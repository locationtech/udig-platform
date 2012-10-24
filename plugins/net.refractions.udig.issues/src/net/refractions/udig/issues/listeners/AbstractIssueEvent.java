/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
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