/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.core;

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
