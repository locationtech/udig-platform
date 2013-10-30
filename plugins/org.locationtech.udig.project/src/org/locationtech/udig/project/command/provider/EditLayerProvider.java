/**
 * 
 */
package org.locationtech.udig.project.command.provider;

import org.locationtech.udig.core.IBlockingProvider;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.internal.commands.edit.SetAttributeCommand;

import org.eclipse.core.runtime.IProgressMonitor;

public class EditLayerProvider implements IBlockingProvider<ILayer> {

	private MapCommand command;
    private IMap map;
    
    public EditLayerProvider( MapCommand command ) {
		this.command = command;
    }
    
    public EditLayerProvider( IMap map ) {
        this.map=map;
    }
    public ILayer get( IProgressMonitor monitor, Object... params ) {
        if( map!=null ){
            return map.getEditManager().getEditLayer();
        }
        return command.getMap().getEditManager().getEditLayer();
    }
}
