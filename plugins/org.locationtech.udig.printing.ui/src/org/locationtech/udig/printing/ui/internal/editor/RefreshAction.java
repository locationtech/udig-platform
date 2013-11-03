/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.printing.ui.internal.editor;

import org.locationtech.udig.printing.model.AbstractBoxPrinter;
import org.locationtech.udig.printing.ui.IBoxEditAction;
import org.locationtech.udig.printing.ui.internal.editor.parts.BoxPart;

import org.eclipse.gef.commands.Command;

/**
 * Forces a refresh of the MapBox
 * 
 * @author jesse
 * @since 1.1.0
 */
public class RefreshAction implements IBoxEditAction {

    private AbstractBoxPrinter boxPrinter;

    public Command getCommand() {
        return null;
    }

    public void init( BoxPart owner ) {
        this.boxPrinter = (AbstractBoxPrinter) owner.getBoxPrinter();
    }

    public boolean isDone() {
        return true;
    }

    public void perform() {
        boxPrinter.setDirty(true);
    }

}
