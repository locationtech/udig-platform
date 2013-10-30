/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
