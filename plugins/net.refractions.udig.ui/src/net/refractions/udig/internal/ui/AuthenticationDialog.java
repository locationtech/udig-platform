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
package net.refractions.udig.internal.ui;


import net.refractions.udig.ui.internal.Messages;

import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AuthenticationDialog extends IconAndMessageDialog {

    private String username;
    private String password;
    private Text usernameText;
    private Text passwordText;
    private boolean shouldRemember;
    private Button rememberCheckbox;
    
    public AuthenticationDialog( Shell parentShell ) {
        super(parentShell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
        
    }

//    protected Point getInitialSize() {
//        return new Point(400, 400);
//    }

    @Override
    protected Image getImage() {
        return getWarningImage();
    }

    protected Control createDialogArea( Composite parent ) {
        message = Messages.AuthenticationDialog_label_prompt; 
        
        Composite composite = (Composite) super.createDialogArea(parent);
        ((GridLayout)composite.getLayout()).numColumns = 2;
        ((GridLayout)composite.getLayout()).makeColumnsEqualWidth = false;
        
        createMessageArea(composite);
        
        Label usernameLabel = new Label(composite, SWT.NONE);
        usernameLabel.setText(Messages.AuthenticationDialog_label_username); 
        usernameText = new Text(composite, SWT.BORDER);
        usernameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        Label passwordLabel = new Label(composite, SWT.NONE);
        passwordLabel.setText(Messages.AuthenticationDialog_label_password); 
        passwordText = new Text(composite, SWT.BORDER | SWT.PASSWORD);
        passwordText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        
        rememberCheckbox = new Button(composite, SWT.CHECK);
        rememberCheckbox.setText(Messages.AuthenticationDialog_label_rememberPassword); 
        GridData gridData = new GridData(SWT.LEFT, SWT.FILL, true, false);
        gridData.horizontalSpan = 2;
        rememberCheckbox.setLayoutData(gridData);
        rememberCheckbox.setSelection(true);
        
        return composite;
    }
    
    protected void okPressed() {
        username = usernameText.getText();
        password = passwordText.getText();
        shouldRemember = rememberCheckbox.getSelection();
        super.okPressed();
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
    
    public boolean shouldRemember() {
        return shouldRemember;
    }

    protected void configureShell( Shell newShell ) {
        newShell.setText(Messages.AuthenticationDialog_dialog_title); 
        newShell.setImage(UiPlugin.getDefault().create("icon32.gif").createImage()); //$NON-NLS-1$
    }
}
