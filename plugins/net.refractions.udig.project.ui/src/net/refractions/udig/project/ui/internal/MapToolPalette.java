package net.refractions.udig.project.ui.internal;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;

/**
 * Palette for our Map Tools.
 * 
 * @author Scott Henderson (LISAsoft)
 * @author Jody Garnett (LISAsoft)
 */
public class MapToolPalette extends GraphicalEditorWithFlyoutPalette implements IAdaptable {

	/** This is the rooot of our palette; forming a tree of categories */
    private PaletteRoot paletteRoot;

    @Override
    protected PaletteRoot getPaletteRoot() {

        if (paletteRoot == null) {
            paletteRoot = MapToolPaletteFactory.createPalette();
        }

        return paletteRoot;
    }
    
    /**
     * We would override this method to save the current tool when we save out the map
     */
    @Override
    public void doSave( IProgressMonitor monitor ) {
        // we are not going to save out right now
    }

}