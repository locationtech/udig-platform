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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Class represents a special cell editor which will open a calendar/date dialog in which a date and
 * time can be selected. The class can return a java.util.Date or java.sql.Date with format
 * dd/MM/yyyy HH:mm:ss
 * 
 * @author Nikolaos Pringouris <nprigour@gmail.com>
 * @since 2.0.0
 * 
 */
public class DateTimeCellEditor extends DialogCellEditor {

    private DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public DateTimeCellEditor() {
        super();
    }

    public DateTimeCellEditor(Composite parent, int style) {
        super(parent, style);
    }

    public DateTimeCellEditor(Composite parent) {
        super(parent);
    }

    @Override
    protected Object openDialogBox(Control cellEditorWindow) {
        DateTimePickerDialog dialog = new DateTimePickerDialog(cellEditorWindow.getShell(),
                "choose date:", true);
        Date d = (Date) doGetValue();
        java.util.Calendar c = Calendar.getInstance();
        if (d != null) {
            c.setTime(d);
        }
        dialog.setDate(c);

        if (dialog.open() != Dialog.CANCEL) {
            if (dialog.shouldNullify()) {// call explicitly doSeValue passing null as argument
                doSetValue(null);
                return null;
            } else {
                return (dialog.getDate() != null) ? dialog.getDate().getTime() : null;
            }
        }
        return null;
    }

    @Override
    protected Object doGetValue() {
        Object object = super.doGetValue();
        if (object == null) {
            return null;
        } else if (Date.class.isAssignableFrom(object.getClass())) {
            return object;
        } else if (object instanceof String) {
            try {
                return format.parse((String) object);
            } catch (ParseException e) {
                // probably a an empty String
                // e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void doSetValue(Object value) {
        // if instance of Date apply the appropriate format
        if (value instanceof Date) {
            super.doSetValue(getDateFormatter().format(value));
        } else {
            super.doSetValue(value);
        }
    }

    @Override
    public boolean isValueValid() {
        Object object = super.doGetValue();
        if (object instanceof String) {
            try {
                format.parse((String) object);
            } catch (ParseException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return localized date formatter to use
     */
    public DateFormat getDateFormatter() {
        return format;
    }

    /**
     * @return localized date formatter to use
     */
    public void setDateFormatter(DateFormat format) {
        this.format = format;
    }

}
