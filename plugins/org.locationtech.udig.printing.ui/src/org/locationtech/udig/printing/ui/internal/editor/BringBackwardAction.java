/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.printing.ui.internal.editor;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;

import org.locationtech.udig.printing.model.Box;
import org.locationtech.udig.printing.model.impl.PageImpl;
import org.locationtech.udig.printing.ui.IBoxEditAction;
import org.locationtech.udig.printing.ui.internal.editor.parts.BoxPart;

/**
 * Moves a box of the printing editor down one level.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 */
public class BringBackwardAction implements IBoxEditAction {

    public Command getCommand() {
        return null;
    }

    @SuppressWarnings("unchecked")
    public void init( BoxPart owner ) {
        EditPart parentPart = owner.getParent();
        PageImpl pageImpl = (PageImpl) parentPart.getModel();
        EList<Box> boxes = (EList<Box>) pageImpl.getBoxes();
        Box box = owner.getBoxPrinter().getBox();
        int i = boxes.indexOf(box);
        if (i <= 0)
            return;
        boxes.move(i - 1, box);
    }

    public boolean isDone() {
        return true;
    }

    public void perform() {
    }

}
