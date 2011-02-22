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
