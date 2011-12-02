package net.refractions.udig.tutorials.style.color;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.ui.operations.IOp;

public class RemoveColorOp implements IOp {

    public void op( Display display, Object target, IProgressMonitor monitor ) throws Exception {
        ILayer layer = (ILayer) target;
        
        layer.getStyleBlackboard().remove( ColorStyle.ID );
        layer.refresh(null);
    }

}
