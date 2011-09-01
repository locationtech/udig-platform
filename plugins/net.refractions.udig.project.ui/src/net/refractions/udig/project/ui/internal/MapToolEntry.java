package net.refractions.udig.project.ui.internal;

import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.tool.display.ModalItem;
import net.refractions.udig.project.ui.internal.tool.display.ToolProxy;
import net.refractions.udig.project.ui.tool.IToolManager;
import net.refractions.udig.project.ui.tool.ModalTool;

import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.ToolEntry;

/**
 * Descriptor class for a tool entry specifically made for the map.
 * 
 * @author Jody Garnett
 * @since 1.3.0
 * @version 1.3.0
 */
public class MapToolEntry extends ToolEntry {

    private String categoryId;
    private ModalItem item;

	public MapToolEntry( String label, ModalItem item, String categoryId) {
        super( label, item.getToolTipText(), item.getImageDescriptor(), item.getLargeImageDescriptor());
        setId(item.getId());
        this.categoryId = categoryId;
        this.item = item;
        item.getMapToolEntries().add( this ); // register for enablement
	}

    public ModalTool getMapTool() {
        IToolManager tools = ApplicationGIS.getToolManager();
        ModalTool tool = (ModalTool) tools.findTool(getId());
        return tool;
    }

    public ToolProxy getMapToolProxy(){
        return (ToolProxy) item;
    }
    @Override
    public void setVisible( boolean isVisible ) {
        super.setVisible(isVisible);
        PaletteContainer parent = getParent();
        boolean doubleCheck = false;
        FREE: for( Object child : parent.getChildren() ){
            PaletteEntry entry = (PaletteEntry) child;
            if( entry.isVisible() ){
                doubleCheck = true;
                break FREE; // yes I just did that to be funny
            }
        }
        parent.setVisible(doubleCheck);
    }
    
    public void dispose(){
        item.getMapToolEntries().remove(this);
    }
    
}
