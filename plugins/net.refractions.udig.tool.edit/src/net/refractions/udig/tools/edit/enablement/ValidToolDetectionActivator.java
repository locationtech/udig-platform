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
package net.refractions.udig.tools.edit.enablement;

import net.refractions.udig.project.IEditManager;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ILayer.Interaction;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.EditManager;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EnablementBehaviour;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.geotools.data.FeatureStore;

/**
 * This class detects and warns the user if the current tool can or can't edit the current layer.
 * 
 * @author Jesse
 * @since 1.0.0
 */
public class ValidToolDetectionActivator implements EnablementBehaviour {

    private final Class[] legalClasses;
    private ILayer lastLayer;
    
    public ValidToolDetectionActivator( Class[] classes ) {
        Class[] c=new Class[0];
        if( classes!=null ){
            c=new Class[classes.length];
            System.arraycopy(classes, 0, c, 0, c.length);
        }
        this.legalClasses = c;
    }

    @SuppressWarnings("unchecked")
    private String detectCompatibility(EditToolHandler handler, IEditManager editManager) {
        
        ILayer selectedLayer = editManager.getSelectedLayer();
        if( ((EditManager)editManager).isEditLayerLocked() ){
            selectedLayer=editManager.getEditLayer();
        }
        if (!selectedLayer.hasResource(FeatureStore.class)) {
            return Messages.ValidToolDetectionActivator_warning1;
        } else if (!selectedLayer.isApplicable(ILayer.Interaction.EDIT)) {
            return openQuestion(handler, Messages.ValidToolDetectionActivator_question,
                    selectedLayer);
        } else {
            Class geomType = selectedLayer.getSchema().getGeometryDescriptor().getType().getBinding();
            boolean acceptable=false;
            for( Class type : legalClasses ) {
                if (geomType.isAssignableFrom(type)) {
                    acceptable=true;
                    break;
                }
            }
            if( !acceptable ){
                return Messages.ValidToolDetectionActivator_warning2;
            }
        }
        
        return null;
    }

    private String openQuestion( final EditToolHandler handler, final String string, final ILayer layer ) {
        final String[] warning=new String[1];
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        PlatformGIS.syncInDisplayThread(shell.getDisplay(), new Runnable(){
            public void run() {
                boolean decision = MessageDialog.openQuestion(shell, 
                		Messages.ValidToolDetectionActivator_questionTitle, string);

                if (decision) {
                    UndoableMapCommand command = handler.getContext().getBasicCommandFactory()
                            .createSetApplicabilityCommand(layer, Interaction.EDIT, true);
                    handler.getContext().sendASyncCommand(command);
                } else {
                    warning[0]=Messages.ValidToolDetectionActivator_warning2;
                }
            }
        });
        return warning[0];
    }

    public String isEnabled( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        if ((eventType!=EventType.MOVED  && eventType!=EventType.ENTERED) || lastLayer==handler.getEditLayer())
            return null;

        IEditManager editManager = handler.getContext().getEditManager();
        lastLayer=handler.getEditLayer();
        return detectCompatibility(handler, editManager);
    }

}
