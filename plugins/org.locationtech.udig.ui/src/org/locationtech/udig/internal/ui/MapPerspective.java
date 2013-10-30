/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.internal.ui;


import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * A perspective factory generates the initial page layout and visible
 * action set for a page.
 */
public class MapPerspective implements IPerspectiveFactory {

	/** <code>ID_PERSPECTIVE</code> field */
	public static final String ID_PERSPECTIVE = "org.locationtech.udig.ui.mapPerspective"; //$NON-NLS-1$
    
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
        
        layout.addView( "org.locationtech.udig.project.ui.projectExplorer", IPageLayout.LEFT, 0.25f, editorArea ); //$NON-NLS-1$
        //layout.addView( "org.locationtech.udig.project.ui.layerManager", IPageLayout.BOTTOM, 0.25f, //$NON-NLS-1$
        //        "org.locationtech.udig.project.ui.projectExplorer" ); //$NON-NLS-1$
        
        IFolderLayout folder = layout.createFolder("org.locationtech.udig.mapPerspective.selection", IPageLayout.BOTTOM, 0.25f, //$NON-NLS-1$
                "org.locationtech.udig.project.ui.projectExplorer");
        folder.addView("org.locationtech.udig.project.ui.layerManager");
        folder.addView("org.locationtech.udig.ui.aoiView");
        
        layout.addView("org.locationtech.udig.catalog.ui.CatalogView", IPageLayout.BOTTOM, 0.65f, editorArea);         //$NON-NLS-1$
        layout.addActionSet("org.locationtech.udig.helpMenuItems");
        layout.addActionSet("org.locationtech.udig.ui.default");
        layout.addPerspectiveShortcut(StylePerspective.ID_PERSPECTIVE);
    }
    
     
}
