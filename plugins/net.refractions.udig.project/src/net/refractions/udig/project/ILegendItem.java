/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2012,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
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
    public ImageDescriptor getGlyph();

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
