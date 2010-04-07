/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.tools.internal;

import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.IToolContext;

/**
 * Tool for panning using the middle mouse.
 * 
 * @author Jesse
 */
public class PanMiddleMouse extends Pan {

	public PanMiddleMouse() {
	}
	
	@Override
	public void setContext(IToolContext context) {
		super.setContext(context);
		// this is a modal tool so when it is setup we need to activate
		setActive(true);
	}

	@Override
	public void setActive(boolean active) {
		// this tool should never deactivate
		if( active && !isActive())
			super.setActive(active);

	}
	
	@Override
	protected boolean validModifierButtonCombo(MapMouseEvent e) {
		return e.buttons==MapMouseEvent.BUTTON2;
	}
}
