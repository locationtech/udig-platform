/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 * (C) 2005 IBM Corporation and others
 * ------
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * --------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package net.refractions.udig.style.sld.editor.internal;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.activities.WorkbenchActivityHelper;

/**
 * The CapabilityFilter is a filter that uses the capabilities
 * support as filter for items.
 */
public class CapabilityFilter extends ViewerFilter {

    /**
     * Create a new instance of a capability filter.
     */
    public CapabilityFilter() {
        super();
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        return ! WorkbenchActivityHelper.filterItem(element);
    }

}
