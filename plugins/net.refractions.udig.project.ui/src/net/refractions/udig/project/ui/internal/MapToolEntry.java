package net.refractions.udig.project.ui.internal;

import net.refractions.udig.project.ui.internal.tool.display.ModalItem;

import org.eclipse.gef.palette.ToolEntry;

public class MapToolEntry extends ToolEntry {

	public MapToolEntry(ModalItem item) {
		super( item.getName(), item.getToolTipText(), item.getImageDescriptor(), null );
		setId( item.getId() );
	}

}
