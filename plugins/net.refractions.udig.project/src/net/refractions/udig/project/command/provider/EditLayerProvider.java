/**
 * 
 */
package net.refractions.udig.project.command.provider;

import net.refractions.udig.core.IBlockingProvider;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.internal.commands.edit.SetAttributeCommand;

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
        ILayer editLayer = command.getMap().getEditManager().getEditLayer();
        if( editLayer == null ){
            // warning warning!
            System.out.println("EditLayerProvider is returning null - please select a layer");
        }
        return editLayer;
    }
}