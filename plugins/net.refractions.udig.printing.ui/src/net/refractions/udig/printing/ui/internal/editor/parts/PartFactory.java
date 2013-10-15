/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
