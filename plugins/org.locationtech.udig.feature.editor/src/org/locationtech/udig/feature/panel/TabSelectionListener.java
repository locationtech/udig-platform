/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 * (C) 2001, 2008 IBM Corporation and others
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

import org.eclipse.ui.views.properties.tabbed.ITabItem;

/**
 * A listener interested in tab selection events that occur for the tabbed
 * feature panel page.
 */
public interface TabSelectionListener {

	/**
	 * Notifies this listener that the selected tab has changed.
	 * 
	 * @param tabDescriptor
	 *            the selected tab descriptor.
	 */
	public void tabSelected(ITabItem tabDescriptor);
}
