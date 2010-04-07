/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.commands.selection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.Envelope;

/**
 * A MapCommand that selects all features in the bounding box encapsulated by the class.
 * 
 * @author jeichar
 * @since TODO provide version
 */
public class BBoxSelectionCommand extends AbstractCommand implements UndoableMapCommand {

    /** Add bbox to layers' current filters */
    public final static int ADD = 1;

    /** Replaces layers' filters */
    public final static int NONE = 0;

    /** "and" bbox to layer's current filters */
    public final static int SUBTRACT = -1;

    Envelope bbox = null;

    private int modifiers;

    private Map<Layer, Filter> undoState=new HashMap<Layer, Filter>();

    // private FilterQuery query=null;

    /**
     * Creates a new instance of BBoxSelectionCommand
     * 
     * @param bbox
     */
    public BBoxSelectionCommand( Envelope bbox, int modifiers ) {
        this.bbox = bbox;
        this.modifiers = modifiers;
    }

    /**
     * @see net.refractions.udig.project.command.UndoableCommand#rollback()
     */
    public void rollback( IProgressMonitor monitor ) throws Exception {
        Set<Entry<Layer, Filter>> state = undoState.entrySet();
        for( Entry<Layer, Filter> entry : state ) {
            entry.getKey().setFilter(entry.getValue());
        }
    }

    /**
     * @see net.refractions.udig.project.command.MapCommand#run()
     */
    public void run( IProgressMonitor monitor ) throws Exception {
        List<Layer> layers = getMap().getLayersInternal();
        for( Layer layer : layers ) {
            undoState.put(layer, layer.getFilter());
        }
        // check for key modifiers for AND/OR toggle
        if (modifiers == NONE) {
            getMap().select(bbox);
        } else if (modifiers == ADD) {
            getMap().select(bbox, true);
        } else if (modifiers == SUBTRACT) {
            // TODO there's no common modifier key for subtracting.  This should
            // probably be done by selecting already selected features....?
            getMap().select(bbox, false);
        }
    }

    /**
     * @see net.refractions.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return Messages.BBoxSelectionCommand_boxSelection; 
    }

}