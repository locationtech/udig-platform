/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
