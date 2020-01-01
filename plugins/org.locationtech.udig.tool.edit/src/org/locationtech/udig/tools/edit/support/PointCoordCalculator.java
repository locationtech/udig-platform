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

import org.locationtech.udig.tools.edit.EditPlugin;

import org.opengis.referencing.operation.MathTransform;

import org.locationtech.jts.geom.Coordinate;

/**
 * Transforms between points and coordinates
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class PointCoordCalculator {
    final AffineTransform toScreen;
    final AffineTransform toWorld;
    MathTransform layerToMap;
    MathTransform mapToLayer;

    public PointCoordCalculator( AffineTransform toScreen, MathTransform layerToMap ) {
        this.toScreen = new AffineTransform(toScreen);
        this.layerToMap = layerToMap;
        AffineTransform temp;
        try {
            temp= this.toScreen.createInverse();
        } catch (Exception e) {
            temp=toScreen;
        }
        toWorld=temp;
        try {
            this.mapToLayer = this.layerToMap.inverse();
        } catch (Exception e) {
            throw (RuntimeException) new RuntimeException(e);
        }
    }

    public PointCoordCalculator( PointCoordCalculator other ) {
        toScreen = new AffineTransform(other.toScreen);
        toWorld = new AffineTransform(other.toWorld);
        layerToMap = other.layerToMap;
        mapToLayer = other.mapToLayer;
    }

    public synchronized Coordinate toCoord( Point point ) {
        double x;
        double y;
        x = point.getX() + .5;
        y = point.getY() + .5;
        
        double[] src = new double[]{x, y};
        double[] dest = new double[2];

        toWorld.transform(src, 0, src, 0, 1);

        if (mapToLayer.isIdentity()) {
            dest = src;
        } else {
            try {
                mapToLayer.transform(src, 0, dest, 0, 1);
            } catch (Exception e) {
                EditPlugin.log("", e); //$NON-NLS-1$
            }
        }
        return new Coordinate(dest[0], dest[1]);
    }

    /**
     * Transforms a Coordinate into the point location it would occupy on the screen.
     * 
     * @param coord coordinate object
     * @return point coordinate would occupy on the screen.
     */
    public synchronized Point toPoint( Coordinate coord ) {
        double[] src = new double[]{coord.x, coord.y};
        double[] dest = new double[2];
        if (layerToMap.isIdentity()) {
            dest = src;
        } else {
            try {
                layerToMap.transform(src, 0, dest, 0, 1);
            } catch (Exception e) {
                EditPlugin.log("", e); //$NON-NLS-1$
            }
        }
        toScreen.transform(dest, 0, dest, 0, 1);
        return Point.valueOf((int) dest[0], (int) dest[1]);
    }

    public void setMapToLayer( MathTransform mapToLayer2 ) {
        this.mapToLayer=mapToLayer2;
        try {
            this.layerToMap = this.mapToLayer.inverse();
        } catch (Exception e) {
            throw (RuntimeException) new RuntimeException(e);
        }
    }

}
