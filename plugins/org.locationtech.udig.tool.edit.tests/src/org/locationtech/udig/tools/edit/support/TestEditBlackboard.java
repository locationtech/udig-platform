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

import org.locationtech.udig.tools.edit.preferences.PreferenceUtil;

import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Ignore;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;

/**
 * Black board with identify transforms.
 * @author jones
 * @since 1.1.0
 */
@Ignore
public class TestEditBlackboard extends EditBlackboard {
    public static final MathTransform IDENTITY;
    static {
        MathTransform tmp = null;
        try {
            tmp = CRS.findMathTransform(DefaultGeographicCRS.WGS84, DefaultGeographicCRS.WGS84);
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
