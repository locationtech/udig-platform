package net.refractions.udig.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import junit.framework.TestCase;

public class BooleanCellEditorTest extends TestCase {

    private Shell shell;

    protected void setUp() throws Exception {
        super.setUp();
        shell=new Shell(Display.getCurrent());
    }

    @Override
    protected void tearDown() throws Exception {
        shell.dispose();
    }

    public void testDoGetValue() {
        BooleanCellEditor editor;
        editor = new BooleanCellEditor(shell);

        editor.setValue(true);
        assertEquals( true, editor.getValue() );

        editor.setValue(false);
        assertEquals( false, editor.getValue() );

        try{
            editor.setValue("illegal"); //$NON-NLS-1$
            fail();
        }catch( Exception e ){
            //good
        }
    }

}
