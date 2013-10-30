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

import java.util.List;

import net.refractions.udig.mapgraphic.MapGraphicChooserDialog;
import net.refractions.udig.mapgraphic.internal.MapGraphicResource;
import net.refractions.udig.printing.model.impl.MapGraphicBoxPrinter;
import net.refractions.udig.printing.ui.IBoxEditAction;
import net.refractions.udig.printing.ui.internal.editor.parts.BoxPart;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

/**
 * Changes the MapGraphic within the MapGraphic Box
 * @author jesse
 * @since 1.1.0
 */
public class SetGraphicAction implements IBoxEditAction {

    private BoxPart owner;
    private MapGraphicResource graphic;

    public Command getCommand() {
        final MapGraphicResource newGraphic = graphic;
        graphic = null;
        final MapGraphicBoxPrinter boxPrinter = getBoxPrinter();
        final MapGraphicResource old = boxPrinter.getMapGraphic();
        
        return new Command(){
            @Override
            public void execute() {
                boxPrinter.setMapGraphic(newGraphic);
            }
            
            @Override
            public void undo() {
                boxPrinter.setMapGraphic(old);
            }
        };
    }

    public void init( BoxPart owner ) {
        this.owner = owner;
    }

    public boolean isDone() {
        return graphic!=null;
    }

    public void perform() {
        graphic = null;
        MapGraphicChooserDialog dialog = new MapGraphicChooserDialog(Display.getCurrent().getActiveShell(), false);
        dialog.setBlockOnOpen(true);
        
        dialog.open();
        
        if( dialog.getReturnCode() == Window.OK ){
            List<MapGraphicResource> selectedResources = dialog.getSelectedResources();
            if( selectedResources!=null && !selectedResources.isEmpty()){
                this.graphic = selectedResources.get(0);
            }
        }
    }
    
    private MapGraphicBoxPrinter getBoxPrinter(){
        return (MapGraphicBoxPrinter) owner.getBoxPrinter();
    }

}
