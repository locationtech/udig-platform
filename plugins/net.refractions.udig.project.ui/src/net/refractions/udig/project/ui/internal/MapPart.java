package net.refractions.udig.project.ui.internal;

import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.tool.IMapEditorSelectionProvider;
import net.refractions.udig.project.ui.viewers.MapEditDomain;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.Control;

/**
 * WorkbenchPart that supports map editing (for an example a IViewpart or IEditorPart that has a map in it).
 * <p>
 * This is responsible for allowing collaboration with the Map. for example:
 * <ul>
 * <li>The palette will need access to the current tool;</li>
 * <li>Perhaps get a list of what tools are good for that context.</li>
 * <li>Allow tools to take control of the map display (ie set font and provide a context menu and status line)</li>
 * <li>
 * </ul>
 * @author Jesse, GDavis
 * @since 1.1.0
 * @version 1.3.0
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
    
    /**
     * Used to set the font for the map display
     * @param textArea
     */
    public void setFont(Control textArea);
    
    /**
     * This is used by tools to advertise their "selection". For example the feature seleciton
     * tool will often provide a Filter or FeatureCollection.
     * 
     * @param selectionProvider
     */
    public void setSelectionProvider(IMapEditorSelectionProvider selectionProvider);
    
    //public void setDefaultEditDomain(IMapEditorPart editorPart)

    /**
     * Access to status line manager; used to display messages and provide tool feedback.
     * @return
     */
	public abstract IStatusLineManager getStatusLineManager();

//	/**
//	 * EditDomain used to control the current tool.
//	 * @return
//	 */
//	public abstract MapEditDomain getEditDomain();
	
}