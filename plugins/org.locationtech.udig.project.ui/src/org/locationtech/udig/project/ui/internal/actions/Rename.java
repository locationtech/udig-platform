/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.internal.actions;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Display;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectElement;
import org.locationtech.udig.project.ui.UDIGGenericAction;
import org.locationtech.udig.project.ui.internal.Messages;

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
