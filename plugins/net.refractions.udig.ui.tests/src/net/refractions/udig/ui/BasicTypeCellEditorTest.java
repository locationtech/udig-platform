/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.ui;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Cell Editor
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class BasicTypeCellEditorTest {

    private Shell shell;

    @Before
    public void setUp() throws Exception {
        shell=new Shell(Display.getCurrent());
    }
    
    @After
    public void tearDown() throws Exception {
        shell.dispose();
    }
    
    @Test
    public void testShort() throws Exception {
        try{
            @SuppressWarnings("unused")
            BasicTypeCellEditor editor = new BasicTypeCellEditor(shell, Boolean.class);
            fail("Boolean should use a combo editor"); //$NON-NLS-1$
        }catch( Exception e){
            // good
        }
        runTest(Short.valueOf((short) 2), Short.valueOf((short) 3), Short.class);
    }

    @Test
    public void testInteger() throws Exception {
        runTest(Integer.valueOf(2), Integer.valueOf(3), Integer.class);
    }

    @Test
    public void testByte() throws Exception {
        runTest(Byte.valueOf((byte) 2), Byte.valueOf((byte) 3), Byte.class);
    }

    @Test
    public void testCharacter() throws Exception {
        runTest('a', 'b', Character.class);
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

    private void runTest( Object value, Object value2, Class<? extends Object> class1 ) {
        BasicTypeCellEditor editor;
        editor = new BasicTypeCellEditor(shell, class1);
        
        editor.setValue(value);
        assertEquals( value, editor.getValue() );

        editor.setValue(value2);
        assertEquals( value2, editor.getValue() );
        
        try{
            editor.setValue(true);
            fail();
        }catch( Exception e ){
            //good
        }
    }

}
