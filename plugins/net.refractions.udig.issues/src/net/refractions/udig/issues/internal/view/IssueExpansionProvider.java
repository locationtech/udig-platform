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
package net.refractions.udig.issues.internal.view;

import net.refractions.udig.issues.IIssuesExpansionProvider;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Default expansion provider that always returns true.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class IssueExpansionProvider implements IIssuesExpansionProvider {

    public String getExtensionID() {
        return null;
    }

    public boolean expand( TreeViewer viewer, TreeItem item, Object element ) {
        return true;
    }

}
