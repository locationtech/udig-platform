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

/**
 * Item listing a layer in the legend to be displayed in a legend view or map decorator.
 * 
 * @author paul.pfeiffer
 */
public interface ILayerLegendItem extends ILegendItem {

    /** Access to the layer being represented by this legend */
    public ILayer getLayer();

}
