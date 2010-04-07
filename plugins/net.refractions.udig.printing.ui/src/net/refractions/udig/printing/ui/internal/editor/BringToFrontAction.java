/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.refractions.udig.printing.ui.internal.editor;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.impl.PageImpl;
import net.refractions.udig.printing.ui.IBoxEditAction;
import net.refractions.udig.printing.ui.internal.editor.parts.BoxPart;

/**
 * Moves a box of the printing editor above all the others.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 */
public class BringToFrontAction implements IBoxEditAction {

    public Command getCommand() {
        return null;
    }

    @SuppressWarnings("unchecked")
    public void init( BoxPart owner ) {
        EditPart parentPart = owner.getParent();
        PageImpl pageImpl = (PageImpl) parentPart.getModel();
        EList<Box> boxes = (EList<Box>) pageImpl.getBoxes();
        int size = boxes.size();
        Box box = owner.getBoxPrinter().getBox();
        boxes.move(size - 1, box);
    }

    public boolean isDone() {
        return true;
    }

    public void perform() {
    }

}
