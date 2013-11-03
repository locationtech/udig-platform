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

import org.locationtech.udig.issues.IIssue;

/**
 * Event data representing a Issue change event
 * @author Jesse
 * @since 1.1.0
 */
public class IssueEvent extends AbstractIssueEvent {

    private final IssueChangeType change;
    /**
     * @param change the type of change that has taken place
     */
    public IssueEvent(IIssue source, IssueChangeType change, Object newValue, Object oldValue){
        super( source, newValue, oldValue);
        this.change=change;
    }

    /**
     * Returns the type of change that has taken place
     *
     * @return the type of change that has taken place
     */
    public IssueChangeType getChange() {
        return change;
    }
}
