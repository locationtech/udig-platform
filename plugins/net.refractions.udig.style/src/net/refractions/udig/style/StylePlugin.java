/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.style;

import net.refractions.udig.core.AbstractUdigUIPlugin;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class StylePlugin extends AbstractUdigUIPlugin {
    
    /** The id of the plug-in */
  	public static final String ID = "net.refractions.udig.style"; //$NON-NLS-1$
  	
  	/** Icons path (value "icons/") */
  	public final static String ICONS_PATH = "icons/";//$NON-NLS-1$

	private static StylePlugin INSTANCE;
  	
    /**
     * The constructor.
     */
  	public StylePlugin() {
  	    super();
  	    INSTANCE = this;
  	}
  	
	public IPath getIconPath() {
		return new Path(ICONS_PATH);
	}

	public static StylePlugin getDefault() {
		return INSTANCE;
	}
}