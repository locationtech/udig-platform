/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.ui;

import static org.junit.Assert.assertEquals;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BooleanCellEditorTest {

    private Shell shell;

    @Before
    public void setUp() throws Exception {
        shell=new Shell(Display.getCurrent());
    }
    
    @After
    public void tearDown() throws Exception {
        shell.dispose();
    }

    @Test(expected = Exception.class)
    public void testDoGetValue() {
        BooleanCellEditor editor;
        editor = new BooleanCellEditor(shell);
        
        editor.setValue(true);
        assertEquals( true, editor.getValue() );

        editor.setValue(false);
        assertEquals( false, editor.getValue() );
        
        editor.setValue("illegal"); //$NON-NLS-1$
    }

}
