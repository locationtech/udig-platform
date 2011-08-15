package net.refractions.udig.project.ui.internal;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;

public class ToolPalette extends GraphicalEditorWithFlyoutPalette implements IAdaptable {
    
    private PaletteRoot paletteRoot;

    @Override
    protected PaletteRoot getPaletteRoot() {

        if (paletteRoot == null) {
            paletteRoot = ToolPaletteFactory.createPalette();
        }

        return paletteRoot;
    }
    
    @Override
    public void doSave( IProgressMonitor monitor ) {
        // TODO Auto-generated method stub
        
    }

}
