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

import net.refractions.udig.tools.edit.preferences.PreferenceUtil;

import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;

/**
 * Black board with identify transforms.
 * @author jones
 * @since 1.1.0
 */
public class TestEditBlackboard extends EditBlackboard {
    public static final MathTransform IDENTITY;
    static {
        MathTransform tmp = null;
        try {
            tmp = CRS.transform(DefaultGeographicCRS.WGS84, DefaultGeographicCRS.WGS84);
        } catch (FactoryException e) {
            // can't happen
        }

        IDENTITY = tmp;
    }

    public TestPreferenceUtil util=new TestPreferenceUtil();

    public class TestPreferenceUtil extends PreferenceUtil{
        private int radius=0;
        {
            instance=this;
        }

        @Override
        public int getVertexRadius() {
            return radius;
        }

        public void setVertexRadius(int newRad) {
            radius=newRad;
        }
    };

    public TestEditBlackboard(){
        super(500, 500, AffineTransform.getTranslateInstance(0,0), IDENTITY);
    }

    public TestEditBlackboard( int width, int height, AffineTransform toScreen, MathTransform layerToMap ) {
        super(width, height, toScreen, layerToMap);
    }
}
