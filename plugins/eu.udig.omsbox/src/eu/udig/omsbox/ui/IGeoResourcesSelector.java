/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.omsbox.ui;

import java.util.List;

import net.refractions.udig.catalog.IGeoResource;

/**
 * Interface for objects that can select resources
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public interface IGeoResourcesSelector {

    /**
     * @return the list of selected {@link IGeoResource}s.
     */
    public abstract List<IGeoResource> getSelectedResources();

}