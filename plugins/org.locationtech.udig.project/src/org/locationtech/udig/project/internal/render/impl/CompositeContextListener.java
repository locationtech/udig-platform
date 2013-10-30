/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.render.impl;

import java.util.List;

import org.locationtech.udig.project.internal.render.CompositeRenderContext;
import org.locationtech.udig.project.internal.render.RenderContext;

/**
 * Listener listening for composition changes in the CompositeContext object.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface CompositeContextListener {
    void notifyChanged( CompositeRenderContext context, List<RenderContext> contexts, boolean added );
}
