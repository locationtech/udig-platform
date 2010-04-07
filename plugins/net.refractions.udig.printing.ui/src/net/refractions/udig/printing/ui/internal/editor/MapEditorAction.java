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

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.impl.MapBoxPrinter;
import net.refractions.udig.printing.ui.IBoxEditAction;
import net.refractions.udig.printing.ui.internal.editor.parts.BoxPart;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.gef.commands.Command;

/**
 * Edits the map.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class MapEditorAction implements IBoxEditAction {


    private Map map;

    public void init( BoxPart owner ) {
        map = ((MapBoxPrinter)((Box)owner.getModel()).getBoxPrinter()).getMap();
    }

    public void perform() {
        ApplicationGIS.openMap(map);
    }

    public boolean isDone() {
        return true;
    }

    public Command getCommand() {
        return null;
    }

}
