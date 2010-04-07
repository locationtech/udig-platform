/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.project.ui.internal.actions;

import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.internal.ProjectElement;
import net.refractions.udig.project.ui.UDIGGenericAction;
import net.refractions.udig.project.ui.internal.Messages;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Display;

/**
 * An action for renaming objects in UDIG
 * 
 * @author jeichar
 * @since 0.6.0
 */
public class Rename extends UDIGGenericAction {
    /**
     * Construct <code>Rename</code>.
     */
    public Rename() {
        super();
    }

    protected void operate( Layer layer ) {
        layer.setName(getNewName(layer.getName()));
        layer.getMapInternal().getProjectInternal().eResource().setModified(true);
    }

    @Override
    protected void operate( Layer[] layers, Object context ) {
        if (layers != null)
            operate(layers[0]);
    }
    
    @Override
    protected void operate( ProjectElement element, Object context ) {
        element.setName(getNewName(element.getName()));
        element.getProjectInternal().eResource().setModified(true);
    }
    
    @Override
    protected void operate( Project project, Object context ) {
        project.setName(getNewName(project.getName()));
        project.eResource().setModified(true);
    }

    /**
     * Opens a dialog asking the user for a new name.
     * 
     * @return The new name of the element.
     */
    private String getNewName( String oldName ) {
        InputDialog dialog = new InputDialog(Display.getDefault().getActiveShell(),
                Messages.Rename_enterNewName, "", oldName, null); //$NON-NLS-1$
        int result = dialog.open();
        if (result == Dialog.CANCEL)
            return oldName;
        return dialog.getValue();
    }

}
