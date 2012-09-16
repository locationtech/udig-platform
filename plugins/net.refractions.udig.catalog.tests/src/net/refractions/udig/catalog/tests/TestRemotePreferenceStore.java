package net.refractions.udig.catalog.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class TestRemotePreferenceStore {

    private DummyRemotePreferenceStore prefStore;

    private static final String key_str = new String("STRING_KEY"); //$NON-NLS-1$
    private static final String key_boo = new String("BOOLEAN_KEY"); //$NON-NLS-1$
    private static final String key_int = new String("INTEGER_KEY"); //$NON-NLS-1$
    private static final String key_dbl = new String("DOUBLE_KEY"); //$NON-NLS-1$
    private static final String key_flt = new String("FLOAT_KEY"); //$NON-NLS-1$
    private static final String key_lng = new String("LONG_KEY"); //$NON-NLS-1$
    
    private static final String val_str = new String("SOMETHING SOMETHING"); //$NON-NLS-1$
    private static final Boolean val_boo = new Boolean(false); 
    private static final Integer val_int = new Integer(1); 
    private static final Double val_dbl = new Double(24.779); 
    private static final Float val_flt = new Float(103.00); 
    private static final Long val_lng = new Long(500000); 

    private static final String val2_str = new String("SoMeThInG eLsE"); //$NON-NLS-1$
    private static final Boolean val2_boo = new Boolean(true); 
    private static final Integer val2_int = new Integer(5432); 
    private static final Double val2_dbl = new Double(0.707); 
    private static final Float val2_flt = new Float(1.4142); 
    private static final Long val2_lng = new Long(1234567890); 
    
    @Before
    public void setUp() throws Exception {
        prefStore = new DummyRemotePreferenceStore();
        prefStore.setDefault(key_str, val_str);
        prefStore.setDefault(key_boo, val_boo);
        prefStore.setDefault(key_int, val_int.intValue());
        prefStore.setDefault(key_dbl, val_dbl.doubleValue());
        prefStore.setDefault(key_flt, val_flt.floatValue());
        prefStore.setDefault(key_lng, val_lng.floatValue());
    }

    @Test
    public void testDefaults() {
        //can we read the defaults we defined during setUp()?
        assertEquals(val_str, prefStore.getDefaultString(key_str));
        assertEquals(val_boo.booleanValue(), prefStore.getDefaultBoolean(key_boo));
        assertEquals(val_int.intValue(), prefStore.getDefaultInt(key_int));
        assertEquals(val_dbl.doubleValue(), prefStore.getDefaultDouble(key_dbl), 0);
        assertEquals(val_flt.floatValue(), prefStore.getDefaultFloat(key_flt), 0);
        assertEquals(val_lng.floatValue(), prefStore.getDefaultFloat(key_lng), 0);
    }
    
    @Test
    public void testWriteAndRead() throws IOException {
        //check initial value of remote store (should be null, since nothing is defined there yet)
        assertNull(prefStore.getValue(key_str));
        assertNull(prefStore.getValue(key_boo));
        assertNull(prefStore.getValue(key_int));
        assertNull(prefStore.getValue(key_dbl));
        assertNull(prefStore.getValue(key_flt));
        assertNull(prefStore.getValue(key_lng));

        //store something
        prefStore.setValue(key_str, val2_str);
        prefStore.setValue(key_boo, val2_boo);
        prefStore.setValue(key_int, val2_int);
        prefStore.setValue(key_dbl, val2_dbl);
        prefStore.setValue(key_flt, val2_flt);
        prefStore.setValue(key_lng, val2_lng);
        prefStore.save();
        
        //read it back again
        assertEquals(val2_str, prefStore.getValue(key_str));
        assertEquals(val2_boo.booleanValue(), Boolean.parseBoolean(prefStore.getValue(key_boo)));
        assertEquals(val2_int.intValue(), Integer.parseInt(prefStore.getValue(key_int)));
        assertEquals(val2_dbl.doubleValue(), Double.parseDouble(prefStore.getValue(key_dbl)), 0);
        assertEquals(val2_flt.floatValue(), Float.parseFloat(prefStore.getValue(key_flt)), 0);
        assertEquals(val2_lng.longValue(), Long.parseLong(prefStore.getValue(key_lng)));
    }
    
}
