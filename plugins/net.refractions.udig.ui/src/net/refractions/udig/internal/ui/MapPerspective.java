/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.internal.ui;


import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * A perspective factory generates the initial page layout and visible
 * action set for a page.
 */
public class MapPerspective implements IPerspectiveFactory {

	/** <code>ID_PERSPECTIVE</code> field */
	public static final String ID_PERSPECTIVE = "net.refractions.udig.ui.mapPerspective"; //$NON-NLS-1$
    
    /**
     * Creates the initial layout for a page.
     * <p>
     * Implementors of this method may add additional views to a
     * perspective.  The perspective already contains an editor folder
     * identified by the result of <code>IPageLayout.getEditorArea()</code>.  
     * Additional views should be added to the layout using this value as 
     * the initial point of reference.  
     * </p>
     *
     * @param layout the page layout
     */
    public void createInitialLayout(IPageLayout layout) {
        // Get the editor area.
        String editorArea = layout.getEditorArea();
        
        layout.addView( "net.refractions.udig.project.ui.projectExplorer", IPageLayout.LEFT, 0.25f, editorArea ); //$NON-NLS-1$
        //layout.addView( "net.refractions.udig.project.ui.layerManager", IPageLayout.BOTTOM, 0.25f, //$NON-NLS-1$
        //        "net.refractions.udig.project.ui.projectExplorer" ); //$NON-NLS-1$
        
        IFolderLayout folder = layout.createFolder("net.refractions.udig.mapPerspective.selection", IPageLayout.BOTTOM, 0.25f, //$NON-NLS-1$
                "net.refractions.udig.project.ui.projectExplorer");
        folder.addView("net.refractions.udig.project.ui.layerManager");
        folder.addView("net.refractions.udig.project.ui.boundaryView");
        
        //layout.addView( "net.refractions.udig.project.ui.limit.Limit", IPageLayout.BOTTOM, 0.25f, "net.refractions.udig.project.ui.projectExplorer" ); //$NON-NLS-1$
        layout.addView("net.refractions.udig.catalog.ui.CatalogView", IPageLayout.BOTTOM, 0.65f, editorArea);         //$NON-NLS-1$
        layout.addActionSet("net.refractions.udig.helpMenuItems");
        layout.addActionSet("net.refractions.udig.ui.default");
        layout.addPerspectiveShortcut(StylePerspective.ID_PERSPECTIVE);
    }
    
     
}