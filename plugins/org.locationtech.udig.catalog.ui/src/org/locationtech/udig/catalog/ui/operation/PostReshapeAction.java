/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.catalog.ui.operation;

import net.refractions.udig.catalog.IGeoResource;

/**
 * A strategy for performing an action after a reshape operation has taken place.
 * 
 * @author jesse
 * @since 1.1.0
 */
public interface PostReshapeAction {
    void execute( IGeoResource original, IGeoResource reshaped );
}
