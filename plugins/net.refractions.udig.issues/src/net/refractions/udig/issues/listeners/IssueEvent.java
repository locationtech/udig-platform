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

import net.refractions.udig.issues.IIssue;

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
