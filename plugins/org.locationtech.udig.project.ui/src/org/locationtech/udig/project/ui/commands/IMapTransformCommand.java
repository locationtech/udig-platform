/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.commands;

/**
 * DrawCommand that is valid until the map is re-rendered. Translation is an example. A Translation
 * is valid because the raster needs to be offset until a new rendered map is available to avoid
 * display artifacts such as image stuttering.
 *
 * @author jeichar
 * @since 0.3
 */
public interface IMapTransformCommand extends IDrawCommand {
    // Tag interface
}
