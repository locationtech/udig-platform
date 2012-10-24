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
package net.refractions.udig.project.render.displayAdapter;

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
