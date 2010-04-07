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

import java.util.Collection;

import net.refractions.udig.issues.IIssue;

/**
 * Event representing a change to an issues list.
 * @author Jesse
 * @since 1.1.0
 */
public class IssuesListEvent {

    private final Collection< ? extends IIssue> changed;
    private final IssuesListEventType type;

    /**
    * @param changed The issues that have been changed.
    * @param type Indicates whether the issues have been added or removed.
    */
    public IssuesListEvent(Collection< ? extends IIssue> changed, IssuesListEventType type ){
        this.changed=changed;
        this.type=type;
    }

    /**
     * The issues that have been changed.
     */
    public Collection< ? extends IIssue> getChanged() {
        return changed;
    }

    /**
     * Returns the type that indicates whether the issues have been added or removed.
     *
     * @return the type that indicates whether the issues have been added or removed.
     */
    public IssuesListEventType getType() {
        return type;
    }
}
