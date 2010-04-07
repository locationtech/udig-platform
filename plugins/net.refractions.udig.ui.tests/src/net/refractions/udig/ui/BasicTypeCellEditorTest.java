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
package net.refractions.udig.ui;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Test Cell Editor
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class BasicTypeCellEditorTest extends TestCase {

    private Shell shell;

    protected void setUp() throws Exception {
        super.setUp();
        shell=new Shell(Display.getCurrent());
    }
    
    @Override
    protected void tearDown() throws Exception {
        shell.dispose();
    }
    
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

    public void testInteger() throws Exception {
        runTest(Integer.valueOf(2), Integer.valueOf(3), Integer.class);     
    }

    public void testByte() throws Exception {
        runTest(Byte.valueOf((byte) 2), Byte.valueOf((byte) 3), Byte.class);
    }

    public void testCharacter() throws Exception {
        runTest('a', 'b', Character.class);
    }

    public void testLong() throws Exception {
        runTest(Long.valueOf(2l), Long.valueOf(3l), Long.class); 
        
    }
    
    public void testDouble() throws Exception {
        runTest(Double.valueOf(2), Double.valueOf(3), Double.class); 
    }

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
