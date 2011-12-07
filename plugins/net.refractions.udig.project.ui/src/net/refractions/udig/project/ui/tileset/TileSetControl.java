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
package net.refractions.udig.project.ui.tileset;

import net.refractions.udig.project.internal.Layer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * A control that shows a TileSet definition 
 * 
 * @author jhudson
 * @since 1.2.0
 */
public class TileSetControl {

    private static final String VALUE = "VALUE"; //$NON-NLS-1$
 
    private Layer layer;
    
    public TileSetControl(final Layer layer){
        this.layer = layer;
    }
    
    public Control createControl( Composite parent ) {
        
        GridLayout layout = new GridLayout(2, false);
        parent.setLayout(layout);
        Label titleLabel = new Label(parent, SWT.NONE);
        titleLabel.setText();
        final Text searchText = new Text(parent, SWT.BORDER | SWT.SEARCH);
        searchText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                | GridData.HORIZONTAL_ALIGN_FILL));

    	
    	
        
        return tree;
    }
    
    

}
