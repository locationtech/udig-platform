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

import org.locationtech.udig.printing.model.Box;
import org.locationtech.udig.printing.model.impl.MapBoxPrinter;
import org.locationtech.udig.printing.ui.IBoxEditAction;
import org.locationtech.udig.printing.ui.internal.editor.parts.BoxPart;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.ui.ApplicationGIS;

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
