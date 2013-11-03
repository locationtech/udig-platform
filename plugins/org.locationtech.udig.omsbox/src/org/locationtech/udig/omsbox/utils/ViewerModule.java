/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.omsbox.utils;

import org.locationtech.udig.omsbox.core.ModuleDescription;

/**
 * A modules wrapper.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class ViewerModule {
    private final ModuleDescription moduleDescription;

    private ViewerFolder parentFolder;

    public ViewerModule( ModuleDescription moduleDescription ) {
        this.moduleDescription = moduleDescription;
    }
    
    public ModuleDescription getModuleDescription() {
        return moduleDescription;
    }

    public void setParentFolder( ViewerFolder parentFolder ) {
        this.parentFolder = parentFolder;
    }

    public ViewerFolder getParentFolder() {
        return parentFolder;
    }
}
