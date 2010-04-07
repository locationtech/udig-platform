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
package net.refractions.udig.printing.ui.internal.editor;

import net.refractions.udig.printing.model.AbstractBoxPrinter;
import net.refractions.udig.printing.ui.IBoxEditAction;
import net.refractions.udig.printing.ui.internal.editor.parts.BoxPart;

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
