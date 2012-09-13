package net.refractions.udig.tutorials.style.color;

import java.awt.Color;
import java.io.IOException;

import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveAdapterFactory;

import org.eclipse.core.runtime.IProgressMonitor;

public class CSVColorFactory implements IResolveAdapterFactory {
    public <T> T adapt( IResolve resolve, Class<T> adapter,
            IProgressMonitor monitor ) throws IOException {        
        if( Color.class.isAssignableFrom(adapter) ){
            return adapter.cast( Color.ORANGE );
        }
        return null;
    }
    public boolean canAdapt( IResolve resolve, Class< ? extends Object> adapter ) {
        return Color.class.isAssignableFrom(adapter);
    }
}
