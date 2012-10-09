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
