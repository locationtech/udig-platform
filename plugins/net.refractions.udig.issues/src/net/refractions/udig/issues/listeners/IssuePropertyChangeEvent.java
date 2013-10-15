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

import net.refractions.udig.issues.IIssue;

/**
 * Represents the event where one of an issue's properties ({@link IIssue#getProperty(String)} has changed
 * @author Jesse
 * @since 1.1.0
 */
public class IssuePropertyChangeEvent extends AbstractIssueEvent {

    private final String propertyKey;

    /**
     * New Instance
     * 
     * @param propertyKey the property that has changed
     */
    public IssuePropertyChangeEvent( IIssue source, String propertyKey, Object newValue, Object oldValue ) {
        super(source, newValue, oldValue);
        this.propertyKey=propertyKey;
    }

    /**
     * Returns the name of the property that has changed
     *
     * @return the name of the property that has changed
     */
    public String getPropertyKey() {
        return propertyKey;
    }

}
