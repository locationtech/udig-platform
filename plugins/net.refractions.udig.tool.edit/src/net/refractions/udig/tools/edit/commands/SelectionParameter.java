/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.tools.edit.commands;

import java.util.LinkedList;
import java.util.List;

import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditToolHandler;

import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.Geometry;

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