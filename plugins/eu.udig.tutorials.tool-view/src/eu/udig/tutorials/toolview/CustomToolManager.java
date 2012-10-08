/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package eu.udig.tutorials.toolview;

import net.refractions.udig.project.ui.internal.tool.display.ToolCategory;
import net.refractions.udig.project.ui.internal.tool.display.ToolManager;
import net.refractions.udig.project.ui.internal.tool.display.ToolProxy;
import net.refractions.udig.project.ui.tool.IToolManager;

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
		boolean defaultInfoTool = categoryId.equals("net.refractions.udig.tool.category.info");
		return defaultInfoTool;
	}
}
