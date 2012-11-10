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


/**
 * Indicates a subtype of issues list that is backed onto a (probably) remote source that does not notify when it is changed and therefore must
 * be manually refreshed().
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface IRemoteIssuesList extends IIssuesList {
    /**
     * Queries the data source to ensure that the list accurately reflects the state of the backend store
     */
    void refresh() throws IOException;
    /**
     * Writes the state of the {@link IIssue} out to the store.
     */
    void save(IIssue issue) throws IOException;
}
