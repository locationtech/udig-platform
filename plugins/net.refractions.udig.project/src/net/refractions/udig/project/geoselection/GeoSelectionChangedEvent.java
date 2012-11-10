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
package net.refractions.udig.project.geoselection;

/**
 * @author Vitalus
 * @param <T>
 */
public class GeoSelectionChangedEvent<T> {

    private String context;

    private T target;

    private IGeoSelection oldSelection;

    private IGeoSelection newSelection;

    /**
     * @param context
     * @param target
     * @param oldSelection
     * @param newSelection
     */
    public GeoSelectionChangedEvent( String context, T target, IGeoSelection oldSelection,
            IGeoSelection newSelection ) {
        super();
        this.context = context;
        this.target = target;
        this.oldSelection = oldSelection;
        this.newSelection = newSelection;
    }

    /**
     * @return
     */
    public String getContext() {
        return context;
    }

    /**
     * @return
     */
    public IGeoSelection getNewSelection() {
        return newSelection;
    }

    /**
     * @return
     */
    public IGeoSelection getOldSelection() {
        return oldSelection;
    }

    /**
     * @return
     */
    public T getTarget() {
        return target;
    }

}
