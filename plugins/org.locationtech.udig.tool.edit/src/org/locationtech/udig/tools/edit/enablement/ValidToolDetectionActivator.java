/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.enablement;

import org.locationtech.udig.project.IEditManager;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.Interaction;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.EditManager;
import org.locationtech.udig.project.internal.commands.SetApplicabilityCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.EnablementBehaviour;
import org.locationtech.udig.tools.edit.EventType;
import org.locationtech.udig.ui.PlatformGIS;

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
        } else if (!selectedLayer.getInteraction(Interaction.EDIT)) {
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
                    UndoableMapCommand command = new SetApplicabilityCommand(layer, Interaction.EDIT, true);
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
