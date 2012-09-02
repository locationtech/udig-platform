package net.refractions.udig.project.internal.element.extensible.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.refractions.udig.project.memento.UdigMemento;

import org.eclipse.ui.IMemento;
import org.junit.Test;

@SuppressWarnings("nls")
public class UdigMementoTest {

    private static final String STRING_VAL = "Hello\nWorld";
    private static final String NULL_VAL = "NULL_VAL";
    private static final String TEXT_VAL = "TEXT\nT";
    private static final float FLOAT_VAL = 1.0f;
    private static final int INT_VAL = 10;
    private static final boolean BOOL_VAL = true;

    private static final String FLOAT = "float";
    private static final String INT = "int";
    private static final String STRING = "string";
    private static final String BOOLEAN = "bool";
    private static final String EMPTY= "";
    private static final String TYPE = "type";
    private static final String ID1 = "id1";

    // tests
    
    @Test
    public void testWrite() throws IOException {
        UdigMemento memento = createTestMemento();
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        memento.write(out, 0);
        
        System.out.println(new String(out.toByteArray()));
        
        UdigMemento readIn = UdigMemento.read(new ByteArrayInputStream(out.toByteArray()));
        
        assertMementoEquals(memento, readIn,true);
    }
    
    @Test
    public void testPutMemento() throws Exception {
        UdigMemento expected = createTestMemento();
        UdigMemento actual = new UdigMemento();
        actual.putMemento(expected);
        
        assertMementoEquals(expected, actual,false);
    }

    @Test
    public void testAtSymbol() throws IOException{
    	UdigMemento mem = new UdigMemento();
    	String value = "net.refractions.udig.project.test";
		String key = "@ElementFactoryImpl.ExtensionPointId.key@";
		mem.putString(key, value);
    	String persisted = mem.toString();
    	UdigMemento mem2 = UdigMemento.readString(persisted);
    	
    	assertEquals(value, mem2.getString(key));
    }

    // support methods

    private void assertMementoEquals( UdigMemento memento, UdigMemento readIn, boolean textValNotNull ) {
        assertEquals(2, readIn.getChildren(TYPE).length);
        assertEquals(1, readIn.getChildren(null).length);
        assertEquals(0, readIn.getChildren("boog").length);
        
        IMemento childNoId = readIn.findChild(TYPE, null);
        IMemento childID = readIn.findChild(TYPE, ID1);
        IMemento childNullType = readIn.findChild(null,null);
        
        assertNotNull(childNoId);
        assertNotNull(childID);
        assertNotNull(childNullType);

        assertDataEquals(memento,readIn, textValNotNull);

        assertDataEquals(memento.findChild(TYPE, null),childNoId, true);
        assertDataEquals(memento.findChild(TYPE, ID1), childID, true);
        assertDataEquals(memento.findChild(null, null), childNullType, true);
    }

    private UdigMemento createTestMemento() {
        UdigMemento memento = new UdigMemento();
        fill(memento);
        fill(memento.createChild(TYPE));
        fill(memento.createChild(TYPE,ID1));
        fill(memento.createChild(null));
        return memento;
    }

    private void assertDataEquals( IMemento expected, IMemento actual, boolean textValNotNull ) {
        assertEquals(expected.getID(), actual.getID());
        assertEquals(FLOAT_VAL, (float) actual.getFloat(FLOAT),0.0);
        assertEquals(INT_VAL, (int) actual.getInteger(INT));
        assertEquals(STRING_VAL, actual.getString(STRING));
        assertEquals(BOOL_VAL, actual.getBoolean(BOOLEAN));
        assertEquals("", actual.getString(EMPTY)); //$NON-NLS-1$
        assertEquals(NULL_VAL, actual.getString(null));
        if(textValNotNull){
            assertEquals(TEXT_VAL, actual.getTextData());
        } else {
            assertNull(actual.getTextData());
        }
    }

    private void fill( IMemento memento ) {
        memento.putFloat(FLOAT, FLOAT_VAL);
        memento.putInteger(INT, INT_VAL);
        memento.putString(STRING, STRING_VAL);
        memento.putString(EMPTY, ""); //$NON-NLS-1$
        memento.putString(null, NULL_VAL);
        memento.putBoolean(BOOLEAN, BOOL_VAL);
        memento.putTextData(TEXT_VAL);
    }
}
