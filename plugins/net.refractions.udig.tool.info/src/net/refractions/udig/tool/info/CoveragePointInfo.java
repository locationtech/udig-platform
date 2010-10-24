/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.tool.info;

import net.refractions.udig.project.ILayer;

/**
 * Class to carry coverage point infos.
 */
public abstract class CoveragePointInfo {

    /**
     * Layer responsible for providing this information.
     */
    ILayer layer;

    /** Construct an LayerPointInfo for the provided Layer */
    public CoveragePointInfo( ILayer layer ) {
        this.layer = layer;
    }

    public ILayer getLayer() {
        return layer;
    }

    public abstract String getInfo();
}