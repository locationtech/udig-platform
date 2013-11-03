/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project;

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
    List<ILegendItem> getItems();

}
