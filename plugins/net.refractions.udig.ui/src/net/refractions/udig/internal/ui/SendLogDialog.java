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
package net.refractions.udig.internal.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog for sending the error log to the development team.
 * 
 * @author chorner
 */
public class SendLogDialog extends TitleAreaDialog {

    private Label contactLabel;
    private Text contact;
    private Label noteLabel;
    private Text notes;
    private Label logLabel;
    private Text log;

    boolean hasLog = false;
    
    protected SendLogDialog( Shell parentShell ) {
        super(parentShell);
        setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL
                | getDefaultOrientation());
    }
    
    @Override
    protected void configureShell( Shell newShell ) {
        super.configureShell(newShell);
        newShell.setText(Messages.SendLogDialog_title);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(640, 720);
    }

    @Override
    protected Control createDialogArea( Composite parent ) {
        setTitle(Messages.SendLogDialog_description); 
        ImageDescriptor image = Images.getDescriptor(ImageConstants.LOG_WIZ);
        if (image != null) setTitleImage(image.createImage());
        
        // create a composite with standard margins and spacing
        Composite composite = new Composite(parent, SWT.RESIZE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        applyDialogFont(composite);

        contactLabel = new Label(composite, SWT.NONE);
        contactLabel.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false));
        contactLabel.setText(Messages.SendLogDialog_contact);

        contact = new Text(composite, SWT.BORDER);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData.horizontalSpan = 2;
        contact.setLayoutData(gridData);
        contact.addModifyListener(new ModifyListener() {

            public void modifyText( ModifyEvent e ) {
                refreshButtons();
            }
            
        });
        
        noteLabel = new Label(composite, SWT.NONE);
        noteLabel.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false));
        noteLabel.setText(Messages.SendLogDialog_notes);

        notes = new Text(composite, SWT.WRAP | SWT.BORDER | SWT.MULTI);
        gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData.heightHint = notes.getLineHeight() * 2;
        gridData.horizontalSpan = 2;
        notes.setLayoutData(gridData);
        notes.addModifyListener(new ModifyListener() {

            public void modifyText( ModifyEvent e ) {
                refreshButtons();
            }
            
        });

        logLabel = new Label(composite, SWT.NONE);
        gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData.verticalAlignment = SWT.END;
        logLabel.setLayoutData(gridData);
        logLabel.setText(Messages.SendLogDialog_log);

        log = new Text(composite, SWT.WRAP | SWT.READ_ONLY | SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
        log.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.horizontalSpan = 2;
        log.setLayoutData(gridData);
        log.setText(Messages.SendLogDialog_reading);
        log.setEnabled(false);
        
        //start a thread to acquire the log file
        PopulateLogRunnable populateLog = new PopulateLogRunnable();
        PlatformGIS.run(populateLog);
        contact.setFocus();
        
        return composite;
    }

    @Override
    protected void createButtonsForButtonBar( Composite parent ) {
        createButton(parent, IDialogConstants.PROCEED_ID, Messages.SendLogDialog_submit, false);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        refreshButtons();
    }

    private void refreshButtons() {
        boolean hasContact = false;
        boolean hasNote = false;
        if (notes.getText() != null && notes.getText().length() > 0) {
            hasNote = true;
        }
        if (contact.getText() != null && contact.getText().length() > 0) {
            hasContact = true;
        }
        Button proceed = getButton(IDialogConstants.PROCEED_ID);
        if (hasContact && hasNote) {
            setMessage(null);
            proceed.setEnabled(hasLog); //only allow submission if a log exists
        } else if (!hasContact) {
            setMessage(Messages.SendLogDialog_contact_message, IMessageProvider.WARNING);
            proceed.setEnabled(false);
        } else {
            setMessage(Messages.SendLogDialog_notes_message, IMessageProvider.WARNING);
            proceed.setEnabled(false);
        }
        
    }
    
    @Override
    protected void buttonPressed( int buttonId ) {
        if (IDialogConstants.PROCEED_ID == buttonId) {
            try {
                sendLog();
            } catch (RuntimeException e) {
                throw (RuntimeException) new RuntimeException().initCause(e);
            } finally {
                okPressed();
            }
        }
        if (buttonId == IDialogConstants.CANCEL_ID) {
            okPressed();
        }
    }

    private void sendLog() {
        try {
            URL url = new URL("http://udig.refractions.net/errorlog.php"); //$NON-NLS-1$
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST"); //$NON-NLS-1$
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded"); //$NON-NLS-1$ //$NON-NLS-2$
            connection.setDoInput(true);

            StringBuilder text = new StringBuilder();
            text.append("Contact:\r\n"); //$NON-NLS-1$
            text.append(contact.getText()); 
            text.append("\r\n\r\nUser comments:\r\n"); //$NON-NLS-1$
            text.append(notes.getText());
            text.append("\r\n\r\nSystem Info:\r\n"); //$NON-NLS-1$
            text.append(getSystemInfo());
            text.append("\r\n----\r\n\r\n"); //$NON-NLS-1$
            text.append(log.getText());
            text.append("\r\n"); //$NON-NLS-1$
            String body = "body=" + URLEncoder.encode(text.toString(), "UTF-8"); //$NON-NLS-1$//$NON-NLS-2$
            
            OutputStream outStream;
            outStream = connection.getOutputStream();
            outStream.write(body.getBytes());
            outStream.flush();
            outStream.close();

            connection.getResponseCode();
        } catch (Exception e) {
            UiPlugin.log("Error log submission failed", e); //$NON-NLS-1$
        } finally {
            UiPlugin.log("Log submitted, chars: " + log.getText().length(), null); //$NON-NLS-1$
        }
    }

    public static boolean logExists() {
        String filename = Platform.getLogFileLocation().toOSString();
        File log = new File(filename);
        return log.exists();
    }

    private String getSystemInfo() {
        StringBuilder content = new StringBuilder();
        //udig version number
        content.append("uDig "); //$NON-NLS-1$
        content.append(UiPlugin.getDefault().getVersion());
        return content.toString(); 
    }
    
    private String getLogText( IProgressMonitor monitor ) {
        String filename = Platform.getLogFileLocation().toOSString();
        File file = new File(filename);
        FileReader in = null;
        BufferedReader br = null;
        try {
            StringBuilder content = new StringBuilder();
            in = new FileReader(file);
            br = new BufferedReader(in);
            String line;
            while( (line = br.readLine()) != null ) {
                content.append(line);
                content.append("\n"); //$NON-NLS-1$
            }
            return content.toString();
        } catch (IOException e) {
            return null;
        } finally {
            try {
                if (br != null)
                    br.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
            }
        }
    }
    
    private class PopulateLogRunnable implements IRunnableWithProgress {

        public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
            String text;
            if (logExists()) {
                text = getLogText(monitor);
            } else {
                text = Messages.SendLogDialog_empty;
            }

            if (monitor.isCanceled()) {
                //freak out
                throw new InterruptedException("Log acquisition was canceled."); //$NON-NLS-1$
            } else {
                //update dialog contents, mark log as acquired
                final String logText = text;
                PlatformGIS.syncInDisplayThread(new Runnable() {

                    public void run() {
                        log.setText(logText);
                        log.setEnabled(true);
                        hasLog = true;
                        refreshButtons();
                    }
                    
                });
            }
        }
    }

}
