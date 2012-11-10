/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.edit.support;

import java.util.List;

/**
 * Listens to a blackboard for events such as geometries being added or set and coordinates being moved, deleted,
 * added, etc...
 * 
 * @author jones
 * @since 1.1.0
 */
public interface EditBlackboardListener {

    /**
     * Indicates that something on the {@link EditBlackboard} has changed.  Event object
     * has the details
     *
     * @param e details of the event.
     */
    void changed( EditBlackboardEvent e);

    /**
     * Indicates that many changes have taken place on the {@link EditBlackboard} and all
     * the changes have been batched into a list (in order of occurance) of events.
     *
     * @param e events that have taken place.  Events are ordered in order of occurance.  The
     * first event in the list was the first event to fire.
     */
    void batchChange( List<EditBlackboardEvent> e);
}
