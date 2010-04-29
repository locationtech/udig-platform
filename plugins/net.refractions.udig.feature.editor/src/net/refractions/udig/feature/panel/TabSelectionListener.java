/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 * (C) 2001, 2008 IBM Corporation and others
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
