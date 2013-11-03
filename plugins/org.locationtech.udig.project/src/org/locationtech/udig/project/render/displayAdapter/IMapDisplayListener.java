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
package org.locationtech.udig.project.render.displayAdapter;

/**
 * An interface for objects to listen to the MapEditor
 * <p>
 * All events are executed in a back ground thread so updating the UI must be done using the
 * {@linkplain org.eclipse.swt.widgets.Display#asyncExec(java.lang.Runnable)}method or the
 * {@linkplain org.eclipse.swt.widgets.Display#syncExec(java.lang.Runnable)}method
 * </p>
 * 
 * @author jeichar
 * @since 0.2
 */
public interface IMapDisplayListener {

    /**
     * Called with the size of the MapEditor has changed.
     * <p>
     * All events are executed in a back ground thread so updating the UI must be done using the
     * {@linkplain org.eclipse.swt.widgets.Display#asyncExec(java.lang.Runnable)}method or the
     * {@linkplain org.eclipse.swt.widgets.Display#syncExec(java.lang.Runnable)}method
     * </p>
     * 
     * @param event An event with a reference to the MapEditor and the new size.
     */
    public void sizeChanged( MapDisplayEvent event );
}
