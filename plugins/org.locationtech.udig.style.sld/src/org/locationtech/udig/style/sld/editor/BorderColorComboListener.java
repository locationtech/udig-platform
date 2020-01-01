/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.sld.editor;

import java.awt.Color;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.ui.PlatformUI;

/**
 * Listens to the BorderColor Listener and lets the user choose the outline color of the theme's polygons.
 * 
 * @author jesse
 * @since 1.1.0
 */
public class BorderColorComboListener implements SelectionListener {


    private StyleThemePage page;

    public BorderColorComboListener( StyleThemePage styleThemePage ) {
        this.page = styleThemePage;
    }

    public void widgetDefaultSelected( SelectionEvent e ) {
        widgetSelected(e);
    }

    public void widgetSelected( SelectionEvent e ) {
        Combo combo = (Combo) e.widget;
        
        if( Outline.values()[combo.getSelectionIndex()] == Outline.CUSTOM ){
            ColorDialog dialog = new ColorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
            dialog.open();
            combo.setData(dialog.getRGB());
        }
        page.generateTheme();
    }

    public static Color getBorder( Combo combo ) {
        Outline outline = Outline.values()[combo.getSelectionIndex()];
        
        switch( outline ) {
        case NONE:
            return null;
        case BLACK:
            return Color.BLACK;
        case WHITE:
            return Color.WHITE;
        case CUSTOM:
            RGB rgb = (RGB) combo.getData();
            if (rgb ==  null) {
                return Color.BLACK;
            }
            Color color = new Color( rgb.red, rgb.green, rgb.blue);
            return color;
        default:
            throw new IllegalArgumentException("This method needs to be updated since outline has been modified");
        }
    }
    
    public enum Outline {
        NONE("None"),
        BLACK("Black"),
        WHITE("White"),
        CUSTOM("Custom");
        
        public final String label;

        private Outline(String label){
            this.label = label;
        }
        
        public static String[] labels(){
            Outline[] values = values();
            String[] labels = new String[values.length];
            for( int i = 0; i < labels.length; i++ ) {
                labels[i]=values[i].label;
            }
            return labels;
        }
        
    }

}

