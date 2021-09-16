/*
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2020, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.ui;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.locationtech.udig.core.AbstractUdigUIPlugin;

public class StyleUIPlugin extends AbstractUdigUIPlugin {

	/** The id of the plug-in */
	public static final String ID = "org.locationtech.udig.style.ui"; //$NON-NLS-1$

	/** Icons path (value "icons/") */
	public static final String ICONS_PATH = "icons/";//$NON-NLS-1$

	private static StyleUIPlugin INSTANCE;

	/**
	 * The constructor.
	 */
	public StyleUIPlugin() {
		super();
		INSTANCE = this;
	}

	@Override
	public IPath getIconPath() {
		return new Path(ICONS_PATH);
	}

	public static StyleUIPlugin getDefault() {
		return INSTANCE;
	}
}
