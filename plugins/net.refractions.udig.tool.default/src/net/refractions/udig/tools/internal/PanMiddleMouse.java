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
package net.refractions.udig.tools.internal;

import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.IToolContext;

/**
 * Tool for panning using the middle mouse.
 * 
 * @author Jesse
 */
public class PanMiddleMouse extends PanTool {

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
