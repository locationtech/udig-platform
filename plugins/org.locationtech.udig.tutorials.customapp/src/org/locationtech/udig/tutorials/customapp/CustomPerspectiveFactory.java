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
package org.locationtech.udig.tutorials.customapp;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class CustomPerspectiveFactory implements IPerspectiveFactory {

    private static final String BOOKMARKS = "org.locationtech.udig.bookmarks.internal.ui.BookmarksView";
    private static final String PROJECTS = "org.locationtech.udig.project.ui.projectExplorer";
    private static final String LAYERS = "org.locationtech.udig.project.ui.layerManager";

    public void createInitialLayout(IPageLayout layout) {
        layout.addFastView(PROJECTS);
        layout.addView(LAYERS, IPageLayout.LEFT, 0.3f,
                IPageLayout.ID_EDITOR_AREA);
        layout.addView(BOOKMARKS, IPageLayout.BOTTOM, 0.7f, LAYERS);
    }

}
