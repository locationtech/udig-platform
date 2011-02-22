package net.refractions.udig.tutorials.style.color;

import java.awt.Color;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveAdapterFactory;
import net.refractions.udig.tutorials.catalog.csv.CSVGeoResource;

public class CSVColorFactory implements IResolveAdapterFactory {
    public Object adapt( IResolve resolve, Class< ? extends Object> adapter,
            IProgressMonitor monitor ) throws IOException {
        if( Color.class.isAssignableFrom(adapter) ){
            return Color.ORANGE;
        }
        return null;
    }
    public boolean canAdapt( IResolve resolve, Class< ? extends Object> adapter ) {
        return Color.class.isAssignableFrom(adapter);
    }
}
