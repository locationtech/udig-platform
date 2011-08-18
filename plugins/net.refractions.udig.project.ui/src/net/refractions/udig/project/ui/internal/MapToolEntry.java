package net.refractions.udig.project.ui.internal;

import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.tool.display.ModalItem;
import net.refractions.udig.project.ui.tool.IToolManager;
import net.refractions.udig.project.ui.tool.ModalTool;
import net.refractions.udig.project.ui.tool.Tool;

import org.eclipse.gef.palette.ToolEntry;

public class MapToolEntry extends ToolEntry {

    private String categoryId;

	public MapToolEntry( String label, ModalItem item, String categoryId) {
        super( label, item.getToolTipText(), item.getImageDescriptor(), item.getLargeImageDescriptor());
        setId(item.getId());
        this.categoryId = categoryId;
    }

    public ModalTool getMapTool() {
        IToolManager tools = ApplicationGIS.getToolManager();
        ModalTool tool = (ModalTool) tools.findTool(getId());

        return tool;
    }
    
}
