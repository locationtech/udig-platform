/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2012,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project;

import java.util.List;

/**
 * Used to group ILayers in a Legend view of map decorator.
 * <p>
 * The grouping of layers into folders is strictly for presentation and does
 * not effect draw order etc...
 * <p>
 * User can show/hide IFolder; this is only with respect to it being listed
 * in the current legend and does not effect the visibility of the contained
 * layers.
 * 
 * @author paul.pfeiffer
 */
public interface IFolder extends ILegendItem {

    /**
     * LegendItems grouped into this IFolder. Please note that an IFolder
     * may contain other IFolders (forming a tree structure of subfolders).
     * <p>
     * Example:<code>folder.getChildren().get(3); // the third item in the folder</code>
     * @return LegendItems contained by this IFolder
     */
    List<? extends ILegendItem> getItems();

}
