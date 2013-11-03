/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.support;

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
