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

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

    private GraticuleLinesConfigurator configurator;

    public ColorListener(GraticuleLinesConfigurator configurator) {
        this.configurator = configurator;
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        widgetSelected(e);
    }

    @Override
    public void widgetSelected(SelectionEvent e) {        
        Event event = new Event();
        event.item = e.item;
        event.widget = e.widget;
        event.detail = e.detail;
        configurator.handleEvent(event);
    }

}
