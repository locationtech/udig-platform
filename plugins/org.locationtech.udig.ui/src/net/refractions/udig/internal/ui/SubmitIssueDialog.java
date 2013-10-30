/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.internal.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.UIUtilities;
import net.refractions.udig.ui.internal.Messages;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog for submitting an issue for the development team.
 * 
 * @author pjessup
 * @since 1.2.0
 */
public class SubmitIssueDialog extends TitleAreaDialog {

    private Link submitIssueLink;
    private Label logLabel;
    private Text log;
    private boolean hasLog = false;

    public SubmitIssueDialog( Shell parentShell ) {
        super(parentShell);
        setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL | getDefaultOrientation());
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

        // add issue tracker instructions with relevant links
        submitIssueLink = new Link(composite, SWT.WRAP | SWT.READ_ONLY | SWT.MULTI);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData.horizontalSpan = 2;
        submitIssueLink.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        submitIssueLink.setLayoutData(gridData);
        submitIssueLink.setText(Messages.SubmitIssueDialog_instructions);
        submitIssueLink.addListener(SWT.Selection, new Listener(){
            public void handleEvent( Event event ) {
                UIUtilities.openLink(event.text);
            }
        });

        logLabel = new Label(composite, SWT.NONE);
        gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData.verticalAlignment = SWT.END;
        logLabel.setLayoutData(gridData);
        logLabel.setText(Messages.SendLogDialog_log);

        // add text widget for displaying log file contents
        log = new Text(composite, SWT.WRAP | SWT.READ_ONLY | SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
        log.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.horizontalSpan = 2;
        log.setLayoutData(gridData);
        log.setText(Messages.SendLogDialog_reading);
        log.setEnabled(false);

        // start a thread to acquire the log file
        PopulateLogRunnable populateLog = new PopulateLogRunnable();
        PlatformGIS.run(populateLog);

        return composite;
    }

    @Override
    protected void createButtonsForButtonBar( Composite parent ) {
        createButton(parent, IDialogConstants.PROCEED_ID, Messages.SubmitIssueDialog_copy, false);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        refreshButtons();
    }

    private void refreshButtons() {
        Button proceed = getButton(IDialogConstants.PROCEED_ID);
        proceed.setEnabled(hasLog); // only allow submission if a log exists
    }

    @Override
    protected void buttonPressed( int buttonId ) {
        if (IDialogConstants.PROCEED_ID == buttonId) {
            try {
                log.selectAll();
                log.copy();
            } catch (RuntimeException e) {
                throw (RuntimeException) new RuntimeException().initCause(e);
            }
        }
        if (buttonId == IDialogConstants.CANCEL_ID) {
            okPressed();
        }
    }

    /**
     * Check whether or not there is a log file
     */
    public static boolean logExists() {
        String filename = Platform.getLogFileLocation().toOSString();
        File log = new File(filename);
        return log.exists();
    }

    /**
     * Get log file contents
     */
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

    /**
     * Runnable for opening log file and adding log file contents to GUI
     */
    private class PopulateLogRunnable implements IRunnableWithProgress {
        public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
            String text;
            if (logExists()) {
                text = getLogText(monitor);
            } else {
                text = Messages.SendLogDialog_empty;
            }

            if (monitor.isCanceled()) {
                // freak out
                throw new InterruptedException("Log acquisition was canceled."); //$NON-NLS-1$
            } else {
                // update dialog contents, mark log as acquired
                final String logText = text;
                PlatformGIS.syncInDisplayThread(new Runnable(){
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
