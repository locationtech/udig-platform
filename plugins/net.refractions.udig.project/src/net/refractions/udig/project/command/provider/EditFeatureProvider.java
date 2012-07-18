/**
 * 
 */
package net.refractions.udig.project.command.provider;

import net.refractions.udig.core.IBlockingProvider;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.MapCommand;

import org.eclipse.core.runtime.IProgressMonitor;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Safely obtain {@link EditManager#getEditFeature()} for a command.
 */
public class EditFeatureProvider implements IBlockingProvider<SimpleFeature> {
    private MapCommand command;
    private IMap map;
    public EditFeatureProvider( IMap map ) {
        this.map=map;
    }
    public EditFeatureProvider( MapCommand command ) {
        this.command = command;
    }
    /** Get the {@link EditManager#getEditFeature()} */
    public SimpleFeature get( IProgressMonitor monitor, Object... params ) {
        if( map!=null ){
            return map.getEditManager().getEditFeature();
        }
        if( command != null ){
            return command.getMap().getEditManager().getEditFeature();
        }
        return null; // not found
    }
}