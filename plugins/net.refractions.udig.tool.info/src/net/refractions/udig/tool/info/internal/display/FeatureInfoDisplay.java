/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.tool.info.internal.display;

import java.io.IOException;

import net.refractions.udig.project.ui.controls.FeatureTableControl;
import net.refractions.udig.tool.info.InfoDisplay;
import net.refractions.udig.tool.info.LayerPointInfo;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureResults;
import org.geotools.feature.FeatureCollection;

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
     * @see net.refractions.udig.tool.info.InfoDisplay#setInfo(net.refractions.udig.project.render.LayerPointInfo)
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
                if( value instanceof FeatureResults ){
                    content.setFeatures( (FeatureResults) value );
                }
                else if( value instanceof FeatureReader){
                    content.setFeatures( (FeatureReader) value );
                }
                else if (value instanceof FeatureCollection ){
                    content.setFeatures( (FeatureCollection) value );
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
