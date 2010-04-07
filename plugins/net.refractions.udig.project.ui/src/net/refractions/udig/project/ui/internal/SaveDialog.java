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
package net.refractions.udig.project.ui.internal;

import net.refractions.udig.project.internal.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

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
