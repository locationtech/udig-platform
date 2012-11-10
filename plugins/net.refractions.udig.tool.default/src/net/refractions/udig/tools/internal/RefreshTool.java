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

import net.refractions.udig.project.ui.tool.AbstractActionTool;

/**
 * A tool that triggers a rerender
 * 
 * @author jeichar
 * @since 0.3
 */
public class RefreshTool  extends AbstractActionTool {

	
	public void run() {
        // Force an update of the renderers
        context.getRenderManager().refresh(null);
    }

    public void dispose() {
        // do nothing
    }

}
