/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.core;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A generic interface for object that can be asked for a 
 * type of object.
 * 
 * <p>
 * Example: 
 * A draw point object requires a point to draw.  However a different object is responsible
 * for generating those points.  That object could implement the Provider<Point> interface
 * </p>
 * 
 * @author jones
 * @since 1.1.0
 */
public interface IBlockingProvider<T> {
    T get(IProgressMonitor monitor, Object... params) throws IOException;
}
