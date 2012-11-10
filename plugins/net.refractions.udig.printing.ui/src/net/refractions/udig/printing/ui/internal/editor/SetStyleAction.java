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

import net.refractions.udig.printing.model.impl.MapGraphicBoxPrinter;
import net.refractions.udig.printing.ui.IBoxEditAction;
import net.refractions.udig.printing.ui.internal.PrintingPlugin;
import net.refractions.udig.printing.ui.internal.editor.parts.BoxPart;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.StyleBlackboard;
import net.refractions.udig.style.sld.editor.EditorPageManager;
import net.refractions.udig.style.sld.editor.StyleEditorDialog;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Opens the style dialog for the selected MapGraphicBox 
 * 
 * @author jesse
 * @since 1.1.0
 */
public class SetStyleAction implements IBoxEditAction {

    private BoxPart owner;
    private StyleBlackboard original;

    public Command getCommand() {
        final StyleBlackboard originalStyleBlackboard = this.original;
        this.original = null;
        
        final Layer layer = getSelectedLayer();
        final StyleBlackboard newBlackboard = layer.getStyleBlackboard();
        
        return new Command(){
            @Override
            public String getLabel() {
                return "Change Style";
            }
            @Override
            public void execute() {
                layer.setStyleBlackboard(newBlackboard);
            }
            
            @Override
            public void undo() {
                layer.setStyleBlackboard(originalStyleBlackboard);
            }
        };
    }

    public void init( BoxPart owner ) {
        this.owner = owner;
    }

    public boolean isDone() {
        return true;
    }

    private MapGraphicBoxPrinter getBoxPrinter() {
        return (MapGraphicBoxPrinter) owner.getBoxPrinter();
    }
    
    public void perform() {
        Layer selectedLayer = getSelectedLayer();
        original = selectedLayer.getStyleBlackboard(); 
        EditorPageManager manager = EditorPageManager.loadManager(PrintingPlugin.getDefault(), selectedLayer );
        Shell parentShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        StyleEditorDialog dialog = StyleEditorDialog.createDialogOn(parentShell, null, selectedLayer, manager);
        dialog.setBlockOnOpen(true);
        int returnCode = dialog.open();
        
        if( returnCode!=Window.OK ){
            selectedLayer.setStyleBlackboard(original);
            original = null;
        }
    }

    /**
     *
     * @return
     */
    private Layer getSelectedLayer() {
        return getBoxPrinter().getLayer();
    }

}
