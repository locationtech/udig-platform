/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues.internal.view;

import org.locationtech.udig.issues.IIssuesExpansionProvider;

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
