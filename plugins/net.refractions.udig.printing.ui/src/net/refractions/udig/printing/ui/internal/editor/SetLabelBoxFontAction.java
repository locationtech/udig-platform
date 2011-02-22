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

import java.awt.Font;

import net.refractions.udig.printing.model.impl.LabelBoxPrinter;
import net.refractions.udig.printing.ui.IBoxEditAction;
import net.refractions.udig.printing.ui.internal.editor.parts.BoxPart;
import net.refractions.udig.ui.graphics.SWTGraphics;

import org.eclipse.gef.commands.Command;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;

/**
 * Changes the font of a LabelBoxPrinter
 *
 * @author jesse
 * @since 1.1.0
 */
public class SetLabelBoxFontAction implements IBoxEditAction {

    private BoxPart owner;
    private FontDialog dialog;

    public Command getCommand() {
        return new Command(){
            private Font oldFont = getBoxPrinter().getFont();
            private Font newFont = SWTGraphics.swtFontToAwt(dialog.getFontList()[0]);
            @Override
            public void execute() {
                getBoxPrinter().setFont(newFont);
                owner.refresh();
            }

            @Override
            public void undo() {
                getBoxPrinter().setFont(oldFont);
            }
        };
    }

    protected LabelBoxPrinter getBoxPrinter() {
        return (LabelBoxPrinter) owner.getBoxPrinter();
    }

    public void init( BoxPart owner ) {
        this.owner = owner;
    }

    public boolean isDone() {
        return dialog!=null && dialog.getFontList()!=null && dialog.getFontList().length>0;
    }

    public void perform() {
        dialog = new FontDialog(Display.getCurrent().getActiveShell());
        dialog.open();
    }

}
