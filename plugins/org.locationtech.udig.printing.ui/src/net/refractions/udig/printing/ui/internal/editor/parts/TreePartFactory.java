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
import net.refractions.udig.printing.model.Page;
import net.refractions.udig.printing.model.impl.MapBoxPrinter;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

/**
 * Provides ...TODO summary sentence
 * <p>
 * TODO Description
 * </p><p>
 * Responsibilities:
 * <ul>
 * <li>
 * <li>
 * </ul>
 * </p><p>
 * Example Use:<pre><code>
 * TreePartFactory x = new TreePartFactory( ... );
 * TODO code example
 * </code></pre>
 * </p>
 * @author Richard Gould
 * @since 0.3
 */
public class TreePartFactory implements EditPartFactory {

    public EditPart createEditPart( EditPart context, Object model ) {
        
        if (model instanceof Box) {
            Box box = (Box) model;
            if (model instanceof Page) {
                return new PageTreeEditPart((Page) model);
            }
            
            if (box.getBoxPrinter() instanceof MapBoxPrinter) {
                return new MapTreePart(box);
            }
            
            return new BoxTreeEditPart((Box) model);
        }
        return null;
    }
}
