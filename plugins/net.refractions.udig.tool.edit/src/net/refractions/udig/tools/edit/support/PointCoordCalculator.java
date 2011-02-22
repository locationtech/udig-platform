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

import net.refractions.udig.tools.edit.EditPlugin;

import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Coordinate;

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
