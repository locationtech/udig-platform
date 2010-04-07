/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.ui.operations;

/**
 * Notified when the results of the {@link OpFilter} is obtained by the {@link LazyOpFilter}
 * @author Jesse
 * @since 1.1.0
 */
public interface ILazyOpListener {

    /**
     * Called when a result has been found.  Please ensure that this does not block as it is called within a lock.  A dangerous 
     * practice admittedly but has to be done in this case so the tools are correctly set.  If a lot of work must be done
     * make sure it is done in a new thread
     *
     * @param result the result of whether the OpFilter evaluated to true or false.
     */
    void notifyResultObtained( boolean result );

}
