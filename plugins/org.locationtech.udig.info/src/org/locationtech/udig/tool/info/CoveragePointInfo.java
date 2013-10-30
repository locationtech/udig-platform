/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tool.info;

import org.locationtech.udig.project.ILayer;

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
