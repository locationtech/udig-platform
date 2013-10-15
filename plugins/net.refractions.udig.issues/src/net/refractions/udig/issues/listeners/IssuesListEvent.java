/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
