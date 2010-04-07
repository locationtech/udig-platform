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
package net.refractions.udig.project.ui.summary;

import java.util.Collection;

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
 * @author Jesse
 * @since 1.1.0
 */
public class SummaryDialog extends Dialog {

    private String title;
    private SummaryControl summary;

    /**
     * @param parentShell
     */
    public SummaryDialog( Shell parentShell, String title, Collection<SummaryData> data ) {
        super(parentShell);
        this.title=title;
        this.summary=new SummaryControl(data);
        setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL | getDefaultOrientation());
    }
        
    @Override
    protected void configureShell( Shell newShell ) {
        super.configureShell(newShell);
        newShell.setText(title);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(600,400);
    }
    
    @Override
    protected void createButtonsForButtonBar( Composite parent ) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }
    
    @Override
    protected Control createDialogArea( Composite parent ) {
        return summary.createControl(parent);
    }
}
