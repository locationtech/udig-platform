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
