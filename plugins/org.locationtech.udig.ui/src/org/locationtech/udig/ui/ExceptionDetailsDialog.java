/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.internal.Messages;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * An error dialog that shows a detail view with the whole stacktrace.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class ExceptionDetailsDialog extends Dialog {
    private final Object throwable;
    private final String title;
    private final String message;
    private final Image image;

    /**
     * The constructor.
     * 
     * <p>Kept private in order to be forced to use: {@link ExceptionDetailsDialog#openError(String, String, int, String, Throwable)}</p>
     * 
     * @param parentShell
     * @param title
     * @param message
     * @param status
     * @param throwable
     */
    private ExceptionDetailsDialog( Shell parentShell, String title, String message,
            IStatus status, Throwable throwable ) {
        super(new SameShellProvider(parentShell));
        this.title = getTitle(title, throwable);
        this.image = getImage(status);
        this.message = getMessage(message, throwable);
        this.throwable = throwable;
        setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
    }

    /**
     * Opens the error dialog.
     * 
     * @param title a title or null, in which case the exception name is used.
     * @param message a message or null, in which case the exception message is used.
     * @param istatus the {@link IStatus} int, as for example {@link IStatus#ERROR}.
     * @param pluginID the Id of the plugin that is calling.
     * @param throwable the exception of which to show the stacktrace.
     */
    public static void openError( final String title, final String message, final int istatus,
            final String pluginID, final Throwable throwable ) {
        PlatformGIS.syncInDisplayThread(new Runnable(){
            public void run() {
                Status status = new Status(istatus, pluginID, istatus, throwable
                        .getLocalizedMessage(), throwable);
                String msg = message;
                if (msg == null) {
                    msg = "";
                }
                msg = msg + Messages.ExceptionDetailsEditorMessage;
                ExceptionDetailsDialog dialog = new ExceptionDetailsDialog(Display.getDefault()
                        .getActiveShell(), title, msg, status, throwable);
                dialog.open();
            }
        });
    }

    protected void configureShell( Shell shell ) {
        super.configureShell(shell);
        if (title != null)
            shell.setText(title);
    }

    protected Control createDialogArea( Composite parent ) {
        Composite composite = (Composite) super.createDialogArea(parent);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        if (image != null) {
            ((GridLayout) composite.getLayout()).numColumns = 2;
            Label label = new Label(composite, 0);
            image.setBackground(label.getBackground());
            label.setImage(image);
            label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
                    | GridData.VERTICAL_ALIGN_BEGINNING));
        }

        Label label = new Label(composite, SWT.WRAP);
        if (message != null)
            label.setText(message);
        GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
        data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
        label.setLayoutData(data);
        label.setFont(parent.getFont());

        return composite;
    }

    private Button detailsButton;

    protected void createButtonsForButtonBar( Composite parent ) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
        detailsButton = createButton(parent, IDialogConstants.DETAILS_ID,
                IDialogConstants.SHOW_DETAILS_LABEL, false);
    }

    private Control detailsArea;
    private Point cachedWindowSize;

    protected void buttonPressed( int id ) {
        if (id == IDialogConstants.DETAILS_ID)
            toggleDetailsArea();
        else
            super.buttonPressed(id);
    }

    protected void toggleDetailsArea() {
        Point oldWindowSize = getShell().getSize();
        Point newWindowSize = cachedWindowSize;
        cachedWindowSize = oldWindowSize;

        // Show the details area.
        if (detailsArea == null) {
            detailsArea = createDetailsArea((Composite) getContents());
            detailsButton.setText(IDialogConstants.HIDE_DETAILS_LABEL);
        }

        // Hide the details area.
        else {
            detailsArea.dispose();
            detailsArea = null;
            detailsButton.setText(IDialogConstants.SHOW_DETAILS_LABEL);
        }

        /*
         * Must be sure to call
         *    getContents().computeSize(SWT.DEFAULT, SWT.DEFAULT)
         * before calling
         *    getShell().setSize(newWindowSize)
         * since controls have been added or removed.
         */

        // Compute the new window size.
        Point oldSize = getContents().getSize();
        Point newSize = getContents().computeSize(SWT.DEFAULT, SWT.DEFAULT);
        if (newWindowSize == null)
            newWindowSize = new Point(oldWindowSize.x, oldWindowSize.y + (newSize.y - oldSize.y));

        // Crop new window size to screen.
        Point windowLoc = getShell().getLocation();
        Rectangle screenArea = getContents().getDisplay().getClientArea();
        if (newWindowSize.y > screenArea.height - (windowLoc.y - screenArea.y))
            newWindowSize.y = screenArea.height - (windowLoc.y - screenArea.y);

        getShell().setSize(newWindowSize);
        ((Composite) getContents()).layout();
    }

    public static String getTitle( String title, Object details ) {
        if (title != null)
            return title;
        if (details instanceof Throwable) {
            Throwable e = (Throwable) details;
            while( e instanceof InvocationTargetException )
                e = ((InvocationTargetException) e).getTargetException();
            String name = e.getClass().getName();
            return name.substring(name.lastIndexOf('.') + 1);
        }
        return "Exception";
    }

    public static Image getImage( Object details ) {
        Display display = Display.getCurrent();
        if (details instanceof IStatus) {
            switch( ((IStatus) details).getSeverity() ) {
            case IStatus.ERROR:
                return display.getSystemImage(SWT.ICON_ERROR);
            case IStatus.WARNING:
                return display.getSystemImage(SWT.ICON_WARNING);
            case IStatus.INFO:
                return display.getSystemImage(SWT.ICON_INFORMATION);
            case IStatus.OK:
                return null;
            }
        }
        return display.getSystemImage(SWT.ICON_ERROR);
    }

    public static String getMessage( String message, Object details ) {
        if (details instanceof Throwable) {
            Throwable e = (Throwable) details;
            while( e instanceof InvocationTargetException )
                e = ((InvocationTargetException) e).getTargetException();
            if (message == null)
                return e.toString();
            return MessageFormat.format(message, new Object[]{e.toString()});
        }
        if (details instanceof IStatus) {
            String statusMessage = ((IStatus) details).getMessage();
            if (message == null)
                return statusMessage;
            return MessageFormat.format(message, new Object[]{statusMessage});
        }
        if (message != null)
            return message;
        return "An Exception occurred.";
    }

    public static void appendException( PrintWriter writer, Throwable ex ) {
        if (ex instanceof CoreException) {
            appendStatus(writer, ((CoreException) ex).getStatus(), 0);
            writer.println();
        }
        appendStackTrace(writer, ex);
        if (ex instanceof InvocationTargetException)
            appendException(writer, ((InvocationTargetException) ex).getTargetException());
    }
    public static void appendStatus( PrintWriter writer, IStatus status, int nesting ) {
        for( int i = 0; i < nesting; i++ )
            writer.print("  ");
        writer.println(status.getMessage());
        IStatus[] children = status.getChildren();
        for( int i = 0; i < children.length; i++ )
            appendStatus(writer, children[i], nesting + 1);
    }

    public static void appendStackTrace( PrintWriter writer, Throwable ex ) {
        ex.printStackTrace(writer);
    }

    protected Control createDetailsArea( Composite parent ) {

        // Create the details area.
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        panel.setLayout(layout);

        // Create the details content.
        createDetailsViewer(panel);

        return panel;
    }

    protected Control createDetailsViewer( Composite parent ) {
        if (throwable == null)
            return null;

        Text text = new Text(parent, SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.H_SCROLL
                | SWT.V_SCROLL);
        text.setLayoutData(new GridData(GridData.FILL_BOTH));

        // Create the content.
        StringWriter writer = new StringWriter(1000);
        if (throwable instanceof Throwable)
            appendException(new PrintWriter(writer), (Throwable) throwable);
        else if (throwable instanceof IStatus)
            appendStatus(new PrintWriter(writer), (IStatus) throwable, 0);
        text.setText(writer.toString());

        return text;
    }

}
