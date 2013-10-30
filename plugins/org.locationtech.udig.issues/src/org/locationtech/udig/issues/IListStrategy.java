/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.issues;

import java.io.IOException;
import java.util.Collection;
import java.util.List;



/**
 * Provides the methods the {@link StrategizedIssuesList} requires for accessing the remote store.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface IListStrategy {

    /**
     * Adds issues to the back end
     *
     * @param issues issues to add.
     */
    void addIssues(List<? extends IIssue> issues) throws IOException;
    /**
     * Saves the issue to the storage
     * 
     * @param issue issue to save
     */
    void modifyIssue(IIssue issue) throws IOException;
    /**
     * Removes the issues from storage
     *
     * @param issues issues to remove.
     */
    void removeIssues( Collection< ? extends IIssue> issues ) throws IOException;
    /**
     * Returns a list of issues, the issues should be ordered in the correct order. 
     *
     * @return a list of issues
     */
    Collection< ? extends IIssue> getIssues() throws IOException;
    /**
     * Returns The extension id of the list as defined in a plugin.xml file.
     * @return The extension id of the list as defined in a plugin.xml file.
     */
    String getExtensionID();
}
