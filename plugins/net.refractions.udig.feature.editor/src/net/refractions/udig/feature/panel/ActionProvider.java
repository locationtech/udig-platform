/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 * (C) 2006 IBM Corporation and others
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
package net.refractions.udig.feature.panel;

import org.eclipse.ui.IActionBars;

/**
 * Allows a feature panel to contribute to the action bars.
 * <p>
 * The most frequent use of setActionBars() is to retarget the global actions
 * for undo and redo based on the active tabbed properties view contributor.
 * </p>
 */
public interface ActionProvider {

    /**
     * Allows the page to make contributions to the given action bars. The
     * contributions will be visible when the page is visible.
     * 
     * @param contributor
     *            the feature panel page contributor.
     * @param actionBars
     *            the action bars for this page
     */
    public void setActionBars(FeaturePanelPageContributor contributor,
            IActionBars actionBars);
}
