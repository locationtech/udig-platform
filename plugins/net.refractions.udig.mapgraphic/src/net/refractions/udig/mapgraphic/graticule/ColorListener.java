/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
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
package net.refractions.udig.mapgraphic.graticule;

import java.awt.Color;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Event;

/**
 * Opens a lineColor chooser dialog and fires the modify method in the ModifyListener.
 * 
 * @author Jesse
 * @author kengu
 * 
 * @since 1.3.3
 */
public class ColorListener implements SelectionListener {

    private GraticuleStyleConfigurator configurator;

    public ColorListener( GraticuleStyleConfigurator configurator ) {
        this.configurator = configurator;
    }

    public void widgetDefaultSelected( SelectionEvent e ) {
        widgetSelected(e);
    }

    public void widgetSelected( SelectionEvent e ) {
        
        ColorDialog dialog = new ColorDialog(e.display.getActiveShell());
        Color currentColor = (Color) e.widget.getData();
        RGB currentRGB = new RGB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue());
        dialog.setRGB(currentRGB);
        dialog.open();
        
        RGB rgb = dialog.getRGB();
        
        e.widget.setData(new Color(rgb.red, rgb.green, rgb.blue));
        
        configurator.updateColorButtons();
        Event event = new Event();
        event.item=e.item;
        event.widget=e.widget;
        event.detail=e.detail;
        configurator.handleEvent(event);
    }

}
