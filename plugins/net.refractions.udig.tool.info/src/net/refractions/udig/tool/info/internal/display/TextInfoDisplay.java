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

import net.refractions.udig.tool.info.InfoDisplay;
import net.refractions.udig.tool.info.LayerPointInfo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * Nested browser used to display LayerPointInfo.
 * 
 * @author Jody Garnett
 * @since 0.3
 */
public class TextInfoDisplay extends InfoDisplay {

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
        text = new Text( parent, SWT.NONE );
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
    
    public void setInfo( LayerPointInfo info ) {
        if( info == null ){
            text.setText( "" );             //$NON-NLS-1$
            //location.setText( "..." );
        }
        else {
            try {
                text.setText( info.acquireValue().toString() );
            } catch (IOException e) {
                text.setText( e.getLocalizedMessage() );
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