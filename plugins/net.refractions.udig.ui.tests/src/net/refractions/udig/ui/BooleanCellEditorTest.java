/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
