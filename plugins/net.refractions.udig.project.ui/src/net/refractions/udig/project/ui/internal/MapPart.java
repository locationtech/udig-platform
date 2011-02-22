package net.refractions.udig.project.ui.internal;

import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.tool.IMapEditorSelectionProvider;

import org.eclipse.swt.widgets.Control;

public interface MapPart {

    /**
     * Returns the map that this editor edits
     *
     * @return Returns the map that this editor edits
     */
    public abstract Map getMap();

    public abstract MapEditorSite getMapEditorSite();
    /**
     * Opens the map's context menu.
     */
    public void openContextMenu();

    public void setFont(Control textArea);

    public void setSelectionProvider(
			IMapEditorSelectionProvider selectionProvider);
}
