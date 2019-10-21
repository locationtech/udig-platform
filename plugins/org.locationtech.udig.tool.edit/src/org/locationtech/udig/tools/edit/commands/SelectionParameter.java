/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.commands;

import java.util.LinkedList;
import java.util.List;

import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.EditToolHandler;

import org.opengis.filter.Filter;

import org.locationtech.jts.geom.Geometry;

/**
 * Gathers up all the parameters need to define how selection works.
 * 
 * @since 1.1.0
 */
public class SelectionParameter {
    /** Handler for the current edit tool */
    public final EditToolHandler handler;
    
    /** Event provided by the user */
    public final MapMouseEvent event;
    
    /** Class that are acceptable selections */
    public final Class< ? extends Geometry>[] acceptableClasses;
    
    /** The kind of filter to generate, intersects, bbox etc... */
    public final Class<? extends Filter> filterType;
    
    public final boolean permitClear;
    
    public final boolean onlyAdd;

    /**
     * Call back objects for when something is selected.
     */
    public final List<SelectionStrategy> selectionStrategies = new LinkedList<SelectionStrategy>();
    /**
     * Call back objects for when something is deselected.
     */
    public final List<DeselectionStrategy> deselectionStrategies = new LinkedList<DeselectionStrategy>();
    
    public SelectionParameter( EditToolHandler handler, MapMouseEvent e,
            Class< ? extends Geometry>[] acceptableClasses, Class<? extends Filter> filterType, boolean permitClear,
            boolean onlyAdd ) {
        this.handler = handler;
        this.event = e;
        this.acceptableClasses = acceptableClasses;
        this.filterType = filterType;
        this.permitClear = permitClear;
        this.onlyAdd = onlyAdd;
    }

}
