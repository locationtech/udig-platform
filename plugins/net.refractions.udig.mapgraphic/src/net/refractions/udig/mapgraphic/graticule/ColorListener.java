/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
