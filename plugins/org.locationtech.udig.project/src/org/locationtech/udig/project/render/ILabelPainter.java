/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project.render;

import java.awt.Rectangle;

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
