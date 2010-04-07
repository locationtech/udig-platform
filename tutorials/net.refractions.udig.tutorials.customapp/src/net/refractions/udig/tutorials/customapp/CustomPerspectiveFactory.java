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
