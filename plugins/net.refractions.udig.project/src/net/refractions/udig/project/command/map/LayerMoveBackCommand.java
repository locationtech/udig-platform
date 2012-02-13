package net.refractions.udig.project.command.map;

import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;

public class LayerMoveBackCommand extends AbstractLayerMoveCommand {
    
    public LayerMoveBackCommand( Map map, ILayer layer ) {
        super(map, layer);
    }

    public LayerMoveBackCommand( Map map, IStructuredSelection structuredSelection ) {
        super(map, structuredSelection);
    }

    public LayerMoveBackCommand( Map map, List<ILayer> selection ) {
        super(map, selection);
    }

    @Override
    public String getName() {
        return "Move to Back"; //$NON-NLS-1$
    }
    
    @Override
    public void run( IProgressMonitor monitor ) throws Exception {
        for( ILayer layer : getSelection() ) {
            getMap().sendToBackLayer((Layer) layer);
        }
    }

    @Override
    public void rollback( IProgressMonitor monitor ) throws Exception {
        for( int i = 0; i < getSelection().size(); i++ ) {
            getMap().sendToIndexLayer((Layer) getSelection().get(i), getIndex().get(i));
        }
    }

}
