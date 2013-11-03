/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.tool.info.internal.display;

import java.io.IOException;

import org.locationtech.udig.project.ui.controls.FeatureTableControl;
import org.locationtech.udig.tool.info.InfoDisplay;
import org.locationtech.udig.tool.info.LayerPointInfo;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureReader;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Nested browser used to display LayerPointInfo.
 * 
 * @author Jody Garnett
 * @since 0.3
 */
public class FeatureInfoDisplay extends InfoDisplay {
    
    /** <code>content</code> field */
    protected FeatureTableControl content;
    
    public Control getControl() {
        return content.getControl();
    }
    
    public void createDisplay( Composite parent ) {        
        // content = new Text( parent, SWT.DEFAULT );
        content = new FeatureTableControl();
        content.createTableControl( parent );
    }
    
    /**
     * Focus the browser onto LayerPointInfo.getRequestURL.
     * 
     * @see org.locationtech.udig.tool.info.InfoDisplay#setInfo(org.locationtech.udig.project.render.LayerPointInfo)
     * @param info
     */
    public void setInfo( LayerPointInfo info ) {
        if( info == null ) {            
            content.clear();            
        }
        else {            
            try {
                Object value = info.acquireValue();
                if( value == null ){
                    // make empty ?
                    content.clear();                    
                }
                if( value instanceof FeatureCollection ){
                    content.setFeatures( (FeatureCollection<SimpleFeatureType, SimpleFeature>) value );
                }
                else if( value instanceof FeatureReader){
                    FeatureReader<SimpleFeatureType, SimpleFeature> reader = (FeatureReader<SimpleFeatureType, SimpleFeature>) value;
                    content.setFeatures( DataUtilities.collection(reader) );                    
                }
                else if (value instanceof FeatureCollection ){
                    content.setFeatures( (FeatureCollection<SimpleFeatureType, SimpleFeature>) value );
                }
                else {
                    content.clear();                    
                }
            } catch (IOException e) {
                content.clear();
            }
        }
    }        
}
