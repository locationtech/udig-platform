/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2012,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
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
