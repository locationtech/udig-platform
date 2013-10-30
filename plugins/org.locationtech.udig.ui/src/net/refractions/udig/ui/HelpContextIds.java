/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package net.refractions.udig.ui;

import org.eclipse.ui.PlatformUI;

/**
 * Help context ids for the uDig application..
 * <p>
 * This interface contains constants only; it is not intended to be implemented
 * or extended.
 * </p>
 * 
 */
public interface HelpContextIds {
	public static final String PREFIX = PlatformUI.PLUGIN_ID + "."; //$NON-NLS-1$
	
    // Wizard pages
	public static final String DATASOURCE_WIZARD_SELECTION_WIZARD_PAGE = PREFIX + "import_wizard_selection_wizard_page_context"; //$NON-NLS-1$
}
	