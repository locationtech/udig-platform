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
package net.refractions.udig.tools.edit.support;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

import org.eclipse.swt.graphics.Path;

/**
 * Provides a single interface for interacting with swt Path objects and awt GeneralPath Objects.  This is because right now Linux requires awt and other
 * platforms use SWT.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class PathAdapter {

    Path swtPath;
    GeneralPath generalPath;

    public void lineTo( int x, int y ) {
        if( isPath() )
            swtPath.lineTo(x, y);
        else
            generalPath.lineTo(x, y);
    }

    public void moveTo( int x, int y ) {
        if( isPath() )
            swtPath.moveTo(x, y);
        else
            generalPath.moveTo(x, y);
    }

    public boolean isPath() {
        return swtPath!=null;
    }

    public Path getPath() {
        return swtPath;
    }

    public PathIterator getPathIterator() {
        if( isPath() ){
            return new PathToPathIteratorAdapter(swtPath);
        }
        return generalPath.getPathIterator(AffineTransform.getScaleInstance(0, 0));
    }

    public void setPath( Path path2 ) {
        swtPath=path2;
    }

    public void setPath( GeneralPath path2 ) {
        generalPath=path2;
    }

    public void close() {
        if( isPath() )
            swtPath.close();
        else
            generalPath.closePath();
    }


}
