/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
 package eu.udig.catalog.jgrass.activeregion.dialogs;

/**
 * @author Andrea Antonello - www.hydrologis.com d
 */
public interface IResourcesSelector {

    /**
     * @return an object containing what the resources selector wants to supply, usually lists of
     *         selected resources
     */
    public abstract Object getSelectedLayers();

}