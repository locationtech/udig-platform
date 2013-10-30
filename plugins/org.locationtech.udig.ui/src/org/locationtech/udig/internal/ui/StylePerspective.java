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

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * A perspective factory generates the initial page layout and visible
 * action set for a page.
 */
public class StylePerspective implements IPerspectiveFactory {

	/** <code>ID_PERSPECTIVE</code> field */
	public static final String ID_PERSPECTIVE = "org.locationtech.udig.ui.stylePerspective"; //$NON-NLS-1$

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
        layout.addView("org.locationtech.udig.style.styleView", IPageLayout.BOTTOM, 0.7f, editorArea); //$NON-NLS-1$
        layout.addView( "org.locationtech.udig.project.ui.layerManager", IPageLayout.RIGHT, 0.82f, editorArea ); //$NON-NLS-1$
    }
}
