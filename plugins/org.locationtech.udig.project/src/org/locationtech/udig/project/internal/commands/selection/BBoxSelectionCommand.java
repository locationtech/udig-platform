/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.commands.selection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.opengis.filter.Filter;

import org.locationtech.jts.geom.Envelope;

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
     * @see org.locationtech.udig.project.command.UndoableCommand#rollback()
     */
    public void rollback( IProgressMonitor monitor ) throws Exception {
        Set<Entry<Layer, Filter>> state = undoState.entrySet();
        for( Entry<Layer, Filter> entry : state ) {
            entry.getKey().setFilter(entry.getValue());
        }
    }

    /**
     * @see org.locationtech.udig.project.command.MapCommand#run()
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
     * @see org.locationtech.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return Messages.BBoxSelectionCommand_boxSelection; 
    }

}
