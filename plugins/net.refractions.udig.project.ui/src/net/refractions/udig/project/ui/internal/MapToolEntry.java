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

	public MapToolEntry( String label, ModalItem item, String shortcut, String categoryId) {
        super( label, description( item.getToolTipText(), shortcut), item.getImageDescriptor(), item.getLargeImageDescriptor());
        setId(item.getId());
        this.categoryId = categoryId;
        this.item = item;
        item.getMapToolEntries().add( this ); // register for enablement
	}

	static String description( String tooltip, String shortcut ){
	    if( shortcut == null){
	        return tooltip;
	    }
	    StringBuilder build = new StringBuilder();
	    if( tooltip != null){
	        build.append(tooltip);
	        build.append(" ");
	    }
	    build.append("(");
	    build.append( shortcut );
	    build.append(")");
	    return build.toString();
	}
    public ModalTool getMapTool() {
        IToolManager tools = ApplicationGIS.getToolManager();
        ModalTool tool = (ModalTool) tools.findTool(getId());
        return tool;
    }

    public String getCategoryId() {
        return categoryId;
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
