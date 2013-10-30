/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 * (C) 2006 IBM Corporation and others
 * ------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 * --------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.locationtech.udig.feature.panel;

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
