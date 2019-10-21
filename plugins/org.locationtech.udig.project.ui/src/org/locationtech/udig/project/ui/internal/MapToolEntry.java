/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.tool.display.ModalItem;
import org.locationtech.udig.project.ui.internal.tool.display.ToolProxy;
import org.locationtech.udig.project.ui.tool.IToolManager;
import org.locationtech.udig.project.ui.tool.ModalTool;

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
