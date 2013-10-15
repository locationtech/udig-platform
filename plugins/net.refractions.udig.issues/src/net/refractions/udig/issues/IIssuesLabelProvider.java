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

import org.eclipse.jface.viewers.ILabelProvider;

/**
 * A Label provider for configuring how the issues view displays is issues.  This is of course
 * closely related to/dependent on the IIssuesContentProvider.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface IIssuesLabelProvider extends ILabelProvider {

    /**
     * This configures the text that is shown in the column headers.
     *
     * @param column the column to get the header text for.
     * @return the header text for the column
     */
    String getHeaderText(Column column);
    
    /**
     * Returns the extension id so that the system can instantiate the 
     * sorter again in the future after the workbench has been shutdown.
     * 
     * @return pluginID.extensionid.
     */
    String getExtensionID();
}
