package net.refractions.udig.project.command.map;

import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;

public class LayerMoveFrontCommand extends AbstractLayerMoveCommand {
    
    public LayerMoveFrontCommand( Map map, ILayer layer ) {
        super(map, layer);
    }

    public LayerMoveFrontCommand( Map map, IStructuredSelection structuredSelection ) {
        super(map, structuredSelection);
    }

    public LayerMoveFrontCommand( Map map, List<ILayer> selection ) {
        super(map, selection);
    }

    @Override
    public String getName() {
        return "Move to Front"; //$NON-NLS-1$
    }
    
    @Override
    public void run( IProgressMonitor monitor ) throws Exception {
        for( int i = getSelection().size() -1; i >= 0; i-- ) {
            getMap().sendToFrontLayer((Layer) getSelection().get(i));
        }
    }

    @Override
    public void rollback( IProgressMonitor monitor ) throws Exception {
        for( int i = getSelection().size() - 1; i >= 0; i-- ) {
            final int index = getIndex().get(i);
            getMap().sendToIndexLayer((Layer) getSelection().get(i), index);
        }
    }

}
