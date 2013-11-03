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
package org.locationtech.udig.issues;

import java.util.List;
import java.util.Set;

import org.locationtech.udig.issues.listeners.IIssuesListListener;


/**
 * Encapsulates a list of issues.  The default implementation is a list kept 
 * in memory.  But other implementation can be defined by the user.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface IIssuesList extends List<IIssue> {
    /**
     * Returns a set of all the issue groups that are in the list. 
     *
     * @return a set of all the issue groups.
     */
    public Set<String> getGroups();
    /**
     * Gets the all issues with the groupID
     *
     * @param groupId groupId of a group of issues
     * @return all issues with the groupID
     */
    public List<IIssue> getIssues(String groupId);
    /**
     * Remove all issues in the group.
     *
     * @param groupId group id of issues to remove 
     */
    public void removeIssues(String groupId);
    /**
     * Add Listener to list
     *
     * @param listener listener to add
     */
    public void addListener( IIssuesListListener listener );
    /**
     * Remove listener from list
     *
     * @param listener listener to remove
     */
    public void removeListener( IIssuesListListener listener );
    /**
     * Returns The extension id of the list as defined in a plugin.xml file.
     * @return The extension id of the list as defined in a plugin.xml file.
     */
    public String getExtensionID();
}
