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
package net.refractions.udig.project.ui.tileset;

import java.io.IOException;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.project.ui.internal.Messages;
import net.refractions.udig.project.ui.internal.ProjectUIPlugin;
import net.refractions.udig.project.ui.preferences.PreferenceConstants;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
 

/**
 * Displays a set of summary information in a Table
 * 
 * @author jhudson
 * @since 1.2.0
 */
public class TileSetDialog extends Dialog {

    private String title;
    private TileSetControl tileControlPage;
    private String resourceName;

    /**
     * @param parentShell
     */
    public TileSetDialog( Shell parentShell, IGeoResource resource ) {
        super(parentShell);
        IGeoResourceInfo info = null;
        try {
            info = resource.getInfo(null);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (info != null){
            this.resourceName = info.getName();
            this.title = Messages.TileSetOp_title + " " + info.getName(); //$NON-NLS-1$
            this.tileControlPage = new TileSetControl(info.getName(), info.getBounds());
        }
        setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL | getDefaultOrientation());
    }

    @Override
    protected void configureShell( Shell newShell ) {
        super.configureShell(newShell);
        newShell.setText(title);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(300,390);
    }

    @Override
    protected void createButtonsForButtonBar( Composite parent ) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
    }

    @Override
    protected Control createDialogArea( Composite parent ) {
        if (tileControlPage.getControl() == null){
            tileControlPage.createControl(parent);
        }
        
        String resolutions = ProjectUIPlugin.getDefault().getPreferenceStore()
                .getString(PreferenceConstants.P_TILESET_RESOLUTIONS + resourceName);

        if ("".equals(resolutions)) { //$NON-NLS-1$
            tileControlPage.loadDefaults();
        }
        
        return tileControlPage.getControl();
    }

    @Override
    protected void okPressed() {
        tileControlPage.performOk();
        super.okPressed();
    }
}
