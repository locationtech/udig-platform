/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.locationtech.udig.project.internal.Map;

/**
 * Queries user if they want to save. Return values are: {@link IDialogConstants#CANCEL_ID}
 * {@link IDialogConstants#NO_ID} {@link IDialogConstants#YES_ID}
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class SaveDialog extends IconAndMessageDialog {

    private Map map;

    public SaveDialog( Shell activeShell, Map map ) {
        super(activeShell);
        this.map=map;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        message = Messages.MapEditor_saveQuestion+" "+map.getName()+"?";  //$NON-NLS-1$//$NON-NLS-2$
        
        Composite composite = (Composite) super.createDialogArea(parent);
        ((GridLayout)composite.getLayout()).numColumns = 2;
        ((GridLayout)composite.getLayout()).makeColumnsEqualWidth = false;
        
        createMessageArea(composite);
        
        return composite;
    }

    @Override
    protected void configureShell( Shell newShell ) {
        newShell.setText(Messages.MapEditor_saveTitle);
        super.configureShell(newShell);
    }

    @Override
    protected void buttonPressed( int buttonId ) {
        if (buttonId == IDialogConstants.NO_ID) {
            setReturnCode(IDialogConstants.NO_ID);
            close();
        } else if (buttonId == IDialogConstants.YES_ID) {
            setReturnCode(IDialogConstants.YES_ID);
            close();
        } else {
            setReturnCode(IDialogConstants.CANCEL_ID);
            close();
        }

    }
    @Override
    protected void createButtonsForButtonBar( Composite parent ) {
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        createButton(parent, IDialogConstants.NO_ID, IDialogConstants.NO_LABEL, false);
        createButton(parent, IDialogConstants.YES_ID, IDialogConstants.YES_LABEL, true);
    }

    @Override
    protected Image getImage() {
        return getQuestionImage();
    };
}
