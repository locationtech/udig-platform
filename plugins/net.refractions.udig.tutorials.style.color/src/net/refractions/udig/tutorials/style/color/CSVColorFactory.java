/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
