/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.printing.ui.internal.editor.parts;

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.Connection;
import net.refractions.udig.printing.model.Page;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

/**
 * A Factory for GEF so that it can create the visual parts from the model items
 * 
 * @author Richard Gould
 * @since 0.3
 */
public class PartFactory implements EditPartFactory {

    public EditPart createEditPart( EditPart context, Object model ) {
        EditPart part = null;

        if (model instanceof Connection) {
            part = new ConnectionPart();
        } else if (model instanceof Page) {
            part = new PagePart();
        } 

        // Default to something that will provide a decent default
        if (part == null && model instanceof Box) {
            part = new BoxPart();
        }

        part.setModel(model);

        return part;
    }

}
