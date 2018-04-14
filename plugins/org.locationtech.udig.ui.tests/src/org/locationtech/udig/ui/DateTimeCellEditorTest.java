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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Nikolaos Pringouris <nprigour@gmail.com>
 * @since 2.0.0
 */
public class DateTimeCellEditorTest {

    private Shell shell;

    @Before
    public void setUp() throws Exception {
        shell = new Shell(Display.getCurrent());
    }

    @After
    public void tearDown() throws Exception {
        shell.dispose();
    }

    @Test
    public void testDoGetValue() {
        DateTimeCellEditor editor;
        editor = new DateTimeCellEditor(shell);

        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String dateString = "12/12/2022 23:00:00";
        try {
            Date time = format.parse(dateString);
            editor.setValue(time);
            assertEquals(time.getTime(), ((Date) editor.getValue()).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        editor.setValue("");
        assertEquals(null, editor.getValue());

    }

    @Test
    public void testIsValidCall() throws Exception {
        DateTimeCellEditor editor;
        editor = new DateTimeCellEditor(shell);

        // empty string
        editor.setValue("");
        assertFalse(editor.isValueValid());

        // empty string
        editor.setValue(null);
        assertTrue(editor.isValueValid());
    }
}
