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

import org.locationtech.udig.tool.info.InfoDisplay;
import org.locationtech.udig.tool.info.LayerPointInfo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;

import org.locationtech.jts.geom.Geometry;

/**
 * Nested browser used to display LayerPointInfo.
 * 
 * @author Jody Garnett
 * @since 0.3
 */
public class FeatureDisplay extends InfoDisplay {

    /** <code>text</code> field */
    protected Text text;
    //private Text location;
    //private ViewForm viewForm;
    
    /**
     * Nested viewForm containing text, and locationbar 
     * @return Control maintained by this display
     */
    public Control getControl() {
        return text; //viewForm;
    }
    
    public void createDisplay( Composite parent ) {
        /*
        viewForm= new ViewForm( parent, SWT.NONE);        
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        viewForm.setLayout(gridLayout);
        
        Label labelAddress = new Label(viewForm, SWT.NONE);
        labelAddress.setText("A&ddress");
        
        location = new Text(viewForm, SWT.BORDER);
        GridData data = new GridData();
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        location.setLayoutData(data);
        */
        text = new Text( parent, SWT.V_SCROLL | SWT.READ_ONLY | SWT.WRAP | SWT.BORDER);
        /*
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.horizontalSpan = 2;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        text.setLayoutData(data);
        */              
    }
    
    public void setInfo( SimpleFeature feature ) {
        text.setToolTipText( null );
        if( feature == null ) {
            text.setText( "" ); //$NON-NLS-1$
            return;
        }
        SimpleFeatureType type = feature.getFeatureType();
        
        StringBuffer buf = new StringBuffer();
        buf.append( feature.getID() );
        buf.append( ": (" ); //$NON-NLS-1$
        buf.append( type.getName().getNamespaceURI() );
        buf.append( ":" ); //$NON-NLS-1$
        buf.append( type.getName().getLocalPart() );
        
        for( int i=0; i<feature.getAttributeCount(); i++ ) {
            AttributeDescriptor attribute = type.getDescriptor( i );
            Object value = feature.getAttribute( i );
            buf.append( "\n" ); //$NON-NLS-1$
            buf.append( attribute.getName() );
            buf.append( " = " ); //$NON-NLS-1$
            if ( attribute instanceof GeometryDescriptor ) {
                buf.append( ((Geometry) value).getGeometryType() );
            }
            else {
                buf.append( value );
            }
        }                
        text.setText( buf.toString() );                    
    }
    public void setInfo( LayerPointInfo info ) {
        if( info == null ){
            text.setText( "" );             //$NON-NLS-1$
        }
        else {
            SimpleFeature feature;
            try {
                feature = (SimpleFeature) info.acquireValue();
                setInfo( feature );                
            } catch (IOException noValue) {
                text.setText( noValue.getLocalizedMessage() );
            }
            if( info.getRequestURL() != null ) {
                String req = info.getRequestURL().toString();
                text.setToolTipText( req );
            }
            else {
                //location.setText( info.getMimeType() +":" );
                text.setToolTipText( info.getMimeType() );
            }
        }
    }    
    
}
