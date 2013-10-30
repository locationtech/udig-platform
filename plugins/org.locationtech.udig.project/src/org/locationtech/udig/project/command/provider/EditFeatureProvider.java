/**
 * 
 */
package net.refractions.udig.project.command.provider;

import net.refractions.udig.core.IBlockingProvider;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.MapCommand;

import org.eclipse.core.runtime.IProgressMonitor;
import org.opengis.feature.simple.SimpleFeature;

public class EditFeatureProvider implements IBlockingProvider<SimpleFeature> {
    private MapCommand command;
    private IMap map;
    public EditFeatureProvider( MapCommand command2 ) {
        this.command = command2;
    }
    public EditFeatureProvider( IMap map ) {
        this.map=map;
    }
    public SimpleFeature get( IProgressMonitor monitor, Object... params ) {
        if( map!=null )
            return map.getEditManager().getEditFeature();
        return command.getMap().getEditManager().getEditFeature();
    }
}