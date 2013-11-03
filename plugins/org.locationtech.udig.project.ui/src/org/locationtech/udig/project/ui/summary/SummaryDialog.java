/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.summary;

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
