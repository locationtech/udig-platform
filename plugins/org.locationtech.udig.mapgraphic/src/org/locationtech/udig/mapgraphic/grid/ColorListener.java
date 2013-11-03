/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.mapgraphic.grid;

import java.awt.Color;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Event;

/**
 * Opens a color chooser dialog and fires the modify method in the ModifyListener.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class ColorListener implements SelectionListener {

    private GridStyleConfigurator configurator;

    public ColorListener( GridStyleConfigurator configurator ) {
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
        
        configurator.updateColorButton();
        Event event = new Event();
        event.item=e.item;
        event.widget=e.widget;
        event.detail=e.detail;
        configurator.handleEvent(event);
    }

}
