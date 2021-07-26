/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.style;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableCommand;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.LayerDecorator;
import org.locationtech.udig.project.internal.StyleBlackboard;
import org.locationtech.udig.project.internal.StyleEntry;

/**
 * Layer decorator that provides commit/revert on StyleBlackboard.
 * 
 * @author jgarnett
 * @since 0.7.0
 */
public class StyleLayer extends LayerDecorator {

    /** Our working copy of the blackboard */
    StyleBlackboard blackboard;

    /** Our backup copy of the blackboard before modifications */
    StyleBlackboard originalBlackboard;

    /**
     * Construct <code>StyleLayer</code>.
     * 
     * @param layer
     */
    public StyleLayer(Layer layer) {
        super(layer);
    }

    @Override
    public synchronized StyleBlackboard getStyleBlackboard() {
        if (blackboard == null) {
            if (originalBlackboard == null) {
                originalBlackboard = layer.getStyleBlackboard();
            }
            blackboard = (StyleBlackboard) layer.getStyleBlackboard().clone();
        }
        return blackboard;
    }

    /**
     * Revert to the current blackboard, any recent modifications will be lost.
     */
    public synchronized void revert() {
        blackboard = null;
        getStyleBlackboard(); // reloads the blackboard currently in use
    }

    /**
     * Revert to the initial blackboard, any existing modifications will be lost.
     */
    public synchronized void revertAll() {
        blackboard = (StyleBlackboard) originalBlackboard.clone();
    }

    /**
     * Apply our blackboard to the original layer.
     * <p>
     * Will check to ensure they are indeed different before.
     */
    public void apply() {
        ApplyStyleCommand applyCommand = new ApplyStyleCommand(layer, layer.getStyleBlackboard(),
                getStyleBlackboard());
        layer.getMap().sendCommandASync(applyCommand);
    }
}

/**
 * MapCommand that changes the blackboard.
 * <p>
 * This command is used to submit a change to the the Map.
 * </p>
 * 
 * @author jgarnett
 * @since 0.6.0
 */
class ApplyStyleCommand extends AbstractCommand implements UndoableCommand {
    StyleBlackboard oldStyleBlackboard;

    StyleBlackboard newStyleBlackboard;

    Layer layer;

    /**
     * Construct <code>ApplyStyleCommand</code>.
     * 
     * @param layer
     * @param oldStyleBlackboard
     * @param newStyleBlackboard
     */
    public ApplyStyleCommand(Layer layer, StyleBlackboard oldStyleBlackboard,
            StyleBlackboard newStyleBlackboard) {
        this.oldStyleBlackboard = oldStyleBlackboard;
        this.newStyleBlackboard = newStyleBlackboard;
        this.layer = layer;
    }

    /*
     * overwrite with the original blackboard
     * 
     * @see org.locationtech.udig.project.command.UndoableCommand#rollback()
     */
    public void rollback(IProgressMonitor monitor) throws Exception {
        layer.setStyleBlackboard(oldStyleBlackboard);
    }

    /** overwrite with new blackboard */
    public void run(IProgressMonitor monitor) throws Exception {
        // FIXME: This is a temporary solution
        List<StyleEntry> l = new ArrayList<>(newStyleBlackboard.getContent());
        List<String> selected = new ArrayList<>();
        for (Iterator<?> itr = l.iterator(); itr.hasNext();) {
            StyleEntry entry = (StyleEntry) itr.next();
            if (entry.getStyle() != null) {
                newStyleBlackboard.put(entry.getID(), entry.getStyle());
            }
        }
        newStyleBlackboard.setSelected(selected.toArray(new String[selected.size()]));
        layer.setStyleBlackboard(newStyleBlackboard);
    }

    /**
     * @see org.locationtech.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return layer.getName();
    }

    /*
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "StyleLayer<" + getName() + ">"; //$NON-NLS-1$ //$NON-NLS-2$
    }
}
