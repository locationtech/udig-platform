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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test NumberCellEditor
 * 
 * @author Nikolaos Pringouris <nprigour@gmail.com>
 * @since 2.0.0
 */
public class NumberCellEditorTest {

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
    public void testShort() throws Exception {
        runTest(Short.valueOf((short) 2), Short.valueOf((short) 3), Short.class);
    }

    @Test
    public void testInteger() throws Exception {
        runTest(Integer.valueOf(2), Integer.valueOf(3), Integer.class);
    }

    @Test
    public void testLong() throws Exception {
        runTest(Long.valueOf(2l), Long.valueOf(3l), Long.class);
    }

    @Test
    public void testDouble() throws Exception {
        runTest(Double.valueOf(2), Double.valueOf(3), Double.class);
    }

    @Test
    public void testFloat() throws Exception {
        runTest(Float.valueOf(2), Float.valueOf(3), Float.class);
    }

    @Test
    public void testBigDecimal() throws Exception {
        runTest(BigDecimal.valueOf(2), BigDecimal.valueOf(3), BigDecimal.class);
    }

    @Test
    public void testBigInteger() throws Exception {
        runTest(BigInteger.valueOf(2), BigInteger.valueOf(3), BigInteger.class);
    }

    @Test
    public void testNull() throws Exception {
        runTest(null, Integer.valueOf(3), Integer.class);
    }

    @Test
    public void testEmptyEqualsNull() throws Exception {
        NumberCellEditor editor;
        editor = new NumberCellEditor(shell, Integer.class);
        editor.setValue("");
        assertNull(editor.getValue());
    }

    @Test(expected = NumberFormatException.class)
    public void testWrongFormatNumber() throws Exception {
        NumberCellEditor editor;
        editor = new NumberCellEditor(shell, Integer.class);

        // empty string
        editor.getValue();
        assertNull(editor.getValue());

        // not parsable number
        editor.setValue("aa");
        editor.getValue();
        assertNull(editor.getValue());
    }

    @Test
    public void testIsValidCall() throws Exception {
        NumberCellEditor editor;
        editor = new NumberCellEditor(shell, Integer.class);

        // empty string
        editor.setValue("aa");
        assertFalse(editor.isValueValid());

        // empty string
        editor.setValue("");
        assertTrue(editor.isValueValid());
    }

    private void runTest(Object value, Object value2, Class<? extends Number> class1) {
        NumberCellEditor editor;
        editor = new NumberCellEditor(shell, class1);

        editor.setValue(value);
        assertEquals(value, editor.getValue());

        editor.setValue(value2);
        assertEquals(value2, editor.getValue());
    }

}
