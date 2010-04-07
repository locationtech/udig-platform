package net.refractions.udig.project.ui.internal;

import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.tool.IMapEditorSelectionProvider;

import org.eclipse.swt.widgets.Control;

/**
 * Base interface for map parts.
 * <p>
 * This is usually a MapView or a MapEditor (bother are WorkbenchPart).
 * 
 * @author Jesse, GDavis
 * @since 1.1.0
 */
public interface MapPart {

    /**
     * Returns the map that this editor edits
     * 
     * @return Returns the map that this editor edits
     */
    public abstract Map getMap();

    /**
     * Opens the map's context menu.
     */
    public void openContextMenu();
    
    public void setFont(Control textArea);

    public void setSelectionProvider(
			IMapEditorSelectionProvider selectionProvider);
}