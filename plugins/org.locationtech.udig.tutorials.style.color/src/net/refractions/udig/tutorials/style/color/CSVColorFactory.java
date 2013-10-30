/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
