/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Item listed in a map legend (including folders, layers and in the future
 * style based categories generated from the current style).
 * <p>
 * The purpose of these classes is to communicate the information being displayed; rather than
 * to strictly control draw order and display. As such we can show/hide individual LegendItems
 * from the legend (if for example they are not considered interesting to the task at hand).
 * <p>
 * These are focused on the legend and define the Icon and "Display Name" presented
 * to the end user. This is based on what information the map is trying to communicate and
 * does not have to directly match the name of the IGeoResource being displayed.
 * <p>
 * For example during disaster response a dataset of "urban zone planning" may be repurposed and
 * displayed in the legend as "Expected Population Densisity" (given a few assumptions about time
 * of day and the location of commuters).
 * 
 * @author paul.pfeiffer
 *
 */
public interface ILegendItem {

    /**
     * Display name of this legend item.
     * <p>
     * Note this is focused on communication and is defined by the user; as such
     * it does not need to directly represent the underlying IGeoResource title.
     * <p>
     * That said the IGeoResource title is considered a good default value.
     * 
     * @return the name from the associated metadata
     */
    public String getName();
    
    /**
     * Icon used to visually represent this LegendItem.
     * <p>
     * 
     * @return Icon used to represent this LegendItem
     */
    public ImageDescriptor getIcon();

    /**
     * Indicates if this LegendItem is shown or hidden in the legend.
     * <p>
     * Please note that this value is strictly used to control if a LegendItem
     * is listed in the Legend; it has no reflection on layer visibility (which
     * controls if a layer is drawn or not).
     * 
     * @return true if LegendItem is shown in legend
     */
    public boolean isShown();
}
