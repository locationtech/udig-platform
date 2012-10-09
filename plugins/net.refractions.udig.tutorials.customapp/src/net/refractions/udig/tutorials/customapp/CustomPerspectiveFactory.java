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
package net.refractions.udig.tutorials.customapp;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class CustomPerspectiveFactory implements IPerspectiveFactory {

    private static final String BOOKMARKS = "org.tcat.citd.sim.udig.bookmarks.internal.ui.BookmarksView";
    private static final String PROJECTS = "net.refractions.udig.project.ui.projectExplorer";
    private static final String LAYERS = "net.refractions.udig.project.ui.layerManager";

    public void createInitialLayout(IPageLayout layout) {
        layout.addFastView(PROJECTS);
        layout.addView(LAYERS, IPageLayout.LEFT, 0.3f,
                IPageLayout.ID_EDITOR_AREA);
        layout.addView(BOOKMARKS, IPageLayout.BOTTOM, 0.7f, LAYERS);
    }

}
