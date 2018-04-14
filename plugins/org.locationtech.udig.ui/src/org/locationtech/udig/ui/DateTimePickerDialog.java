/* uDig - User Friendly Desktop Internet GIS client
 * https://locationtech.org/projects/technology.udig
 * (C) 2015, Eclipse Foundation
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 */
package org.locationtech.udig.ui;

import java.util.Calendar;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Class DatePickerDialog. A dialog where the user can select a dateValue using a month/dateValue
 * view. The class provides an extra option for un-setting the dateValue value.
 * 
 * @author Nikolaos Pringouris <nprigour@gmail.com>
 * @since 2.0.0
 */
public class DateTimePickerDialog extends Dialog {
    // the calendar value to be set
    private Calendar dateValue;

    // attributes required to support nullify operation.
    private Button unsetDateCheckbox;

    private boolean shouldNullify = false;

    private boolean allowNullOption;

    // a description title for the Dialog
    private String description;

    // the Date swt widget
    private DateTime swtCalendar;

    private DateTime swtTime;

    /**
     * Create the dialog
     * 
     * @param parent
     */
    public DateTimePickerDialog(Shell parent, String subject) {
        this(parent, subject, false);
        description = subject;
        setDate(null);
    }

    /**
     * Create the dialog with null option enabled
     * 
     * @param parent
     */
    public DateTimePickerDialog(Shell parent, String subject, boolean allowNullOPtion) {
        super(parent);
        description = subject;
        setDate(null);
        this.allowNullOption = allowNullOPtion;
    }

    /**
     * Set the dateValue (used as start dateValue in calendar)
     * 
     * @param value dateValue to start with, if null, the current dateValue is used and displayed in
     *        the default locale
     */
    public void setDate(Calendar value) {
        if (null == value) {
            dateValue = Calendar.getInstance();
        } else {
            dateValue = value;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell newShell) {
        // newShell.setText(Messages.getString("RCPDatePickerDialog.DatePickerTitle"));
        // //$NON-NLS-1$
        super.configureShell(newShell);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea(Composite parent) {

        Composite client = (Composite) super.createDialogArea(parent);
        client.setLayout(new GridLayout(2, false));
        Label lbl = new Label(client, SWT.NULL);
        lbl.setText(description);
        lbl.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 2, 0));

        if (allowNullOption) {
            unsetDateCheckbox = new Button(client, SWT.CHECK);
            unsetDateCheckbox.setText("set to null");
            unsetDateCheckbox.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 2, 0));
        }

        swtCalendar = new DateTime(client, SWT.DATE | SWT.BORDER);
        swtTime = new DateTime(client, SWT.TIME | SWT.BORDER);
        // TCHAR lpszFormat = new TCHAR (0, "dd/MM/yyyy", true);
        // OS.SendMessage (swtCalendar.handle, OS.DTM_SETFORMAT, 0, lpszFormat);
        // double click does not work since the clicked area cannot be determined,
        // it might have been at the month scroll button.
        // swtCalendar.addMouseListener(new MouseAdapter()
        // {
        // @Override
        // public void mouseDoubleClick(MouseEvent e)
        // {
        // okPressed();
        // }
        // });
        GridData gd = new GridData();
        gd.horizontalSpan = 1;
        gd.horizontalAlignment = SWT.CENTER;
        gd.verticalAlignment = SWT.CENTER;
        swtCalendar.setLayoutData(gd);
        swtCalendar.setYear(dateValue.get(Calendar.YEAR));
        swtCalendar.setMonth(dateValue.get(Calendar.MONTH));
        swtCalendar.setDay(dateValue.get(Calendar.DAY_OF_MONTH));

        swtTime.setTime(dateValue.get(Calendar.HOUR_OF_DAY), dateValue.get(Calendar.MINUTE), 0);

        return client;
    }

    /**
     * sets the selected dateValue into result and closes the dialog
     */
    @Override
    protected void okPressed() {
        if (unsetDateCheckbox != null && unsetDateCheckbox.getSelection()) {
            shouldNullify = true;
        } else {
            dateValue.set(swtCalendar.getYear(), swtCalendar.getMonth(), swtCalendar.getDay(),
                    swtTime.getHours(), swtTime.getMinutes());
        }
        super.okPressed();
    }

    /**
     * @return the selected dateValue as calendar in the current time zone or the time zone of the
     *         calendar passed with last {@link #setDate(Calendar)}
     */
    public Calendar getDate() {
        return dateValue;
    }

    public boolean shouldNullify() {
        return shouldNullify;
    }

}
