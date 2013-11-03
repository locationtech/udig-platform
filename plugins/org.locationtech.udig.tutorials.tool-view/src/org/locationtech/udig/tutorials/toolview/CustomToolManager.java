/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tutorials.toolview;

import org.locationtech.udig.project.ui.internal.tool.display.ToolCategory;
import org.locationtech.udig.project.ui.internal.tool.display.ToolManager;
import org.locationtech.udig.project.ui.internal.tool.display.ToolProxy;
import org.locationtech.udig.project.ui.tool.IToolManager;

/**
 * Remove info category from the tool bar
 * 
 * Requires an extension definition and a property in the plugin_customization.ini file.  
 * 
 * @author jeichar
 */
public class CustomToolManager extends ToolManager implements IToolManager {
	@Override
	protected boolean filterTool(String categoryId, ToolProxy proxy,
			Class<? extends ToolCategory> categoryType) {
		boolean defaultInfoTool = categoryId.equals("org.locationtech.udig.tool.category.info");
		return defaultInfoTool;
	}
}
