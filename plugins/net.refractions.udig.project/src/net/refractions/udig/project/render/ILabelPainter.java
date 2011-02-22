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
package net.refractions.udig.project.render;

import org.eclipse.core.runtime.IAdaptable;
import org.geotools.renderer.lite.LabelCache;

/**
 * The Labeller draws the labels from each renderer on the top layer of the map.  New implementations can be
 * registered using the net.refractions.udig.project.ui.label.painter extension point.
 * <p>
 * The basic implementation maintains a cache of Labels so that various heuristics can be ran on them.  For example making
 * sure that labels don't overlap.  At the end of each rendering the cached labels are deleted.  Except for those added
 * using the putPermanentLabel.
 * </p>
 * @author Jesse
 * @since 1.1.0
 */
public interface ILabelPainter extends IAdaptable,LabelCache {
}
