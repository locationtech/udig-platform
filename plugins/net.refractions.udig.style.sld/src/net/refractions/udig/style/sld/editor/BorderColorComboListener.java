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
package net.refractions.udig.style.sld.editor;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.ui.PlatformUI;
import org.geotools.brewer.color.StyleGenerator;
import org.geotools.styling.StyleBuilder;

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

    public static void setBorder( Combo combo, StyleGenerator sg ) {
        Outline outline = Outline.values()[combo.getSelectionIndex()];
        StyleBuilder builder = new StyleBuilder();
        
//        switch( outline ) {
//        case NONE:
//            sg.setDefaultStroke(null);
//            // we're good.  There is no default border/outline
//            break;
//        case BLACK:
//            sg.setDefaultStroke(builder.createStroke(Color.BLACK));
//            break;
//        case WHITE:
//            sg.setDefaultStroke(builder.createStroke(Color.WHITE));
//            break;
//        case CUSTOM:
//            RGB rgb = (RGB) combo.getData();
//            Color color = new Color( rgb.red, rgb.green, rgb.blue);
//            sg.setDefaultStroke(builder.createStroke(color));
//            break;
//
//        default:
//            throw new IllegalArgumentException("This method needs to be updated since outline has been modified");
//        }
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

