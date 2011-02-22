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

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;

/**
 * An expansion provider controls which elements in the issues viewer are expanded or not.  This is by necessity
 * dependent on which {@link IIssuesContentProvider} is set on the viewer.
 *
 * @see IssueConfiguration#setExpansionProvider(IIssuesExpansionProvider)
 * @see IssueConfiguration#setContentProvider(IIssuesContentProvider)
 *
 * @author Jesse
 * @since 1.1.0
 */
public interface IIssuesExpansionProvider {

    /**
     * Called when an object has a child.  Should return true if the element should be expanded.
     * <b>Do NOT modify item or viewer just use the read only methods.</b>
     *
     * @param viewer tree viewer that is being expanded.
     * @param item tree item that contains the object.
     * @param element element to test whether it should be expanded.
     * @return true if element should be expanded.
     */
    boolean expand(TreeViewer viewer, TreeItem item, Object element);
    /**
     * Returns the extension id so that the system can instantiate the
     * sorter again in the future after the workbench has been shutdown.
     * @return pluginID.extensionid.
     */
    String getExtensionID();

}
