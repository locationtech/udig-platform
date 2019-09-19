package org.locationtech.udig.core;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import static junit.framework.Assert.assertEquals;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;
import org.locationtech.udig.core.AdapterUtil;

public class AdapterUtilTest {


    @Test
    public void testNullObjectDefensiveModeReturnsNullObject() throws Exception {
        Object adapter = AdapterUtil.instance.adapt(String.class.getName(), null, new NullProgressMonitor());
        assertTrue(adapter == null);
    }

    @Test
    public void testCanAdaptToNullPointerExceptionWithClassLoader() {
        final AdapterUtil instance = AdapterUtil.instance;
        final Object nullObject = null;
        boolean canAdaptTo = instance.canAdaptTo(String.class.getName(), nullObject, this.getClass().getClassLoader());
        assertEquals(false, canAdaptTo);
    }
    
    @Test
    public void testCanAdaptToNullPointerException() {
        
        final Object nullObject = null;
        boolean canAdaptTo = AdapterUtil.instance.canAdaptTo(String.class.getName(), nullObject);
        assertEquals(false, canAdaptTo);
    }
    
    @Test
    public void testCanAdaptObjectWithNullClassLoader() {
        String testString = new String();
        boolean canAdaptTo = AdapterUtil.instance.canAdaptTo(String.class.getName(), testString, null);
    }
    
    
    @Test
    public void testCanAdaptDoubleFromInteger() throws Exception {
        Integer testVal = new Integer(2);
		Double value = AdapterUtil.instance.adaptTo(Double.class, testVal, null);
		assertEquals(value, 2.0);
    }
    
    
    @Test
    public void testCanAdaptIntegerFromDouble() throws Exception {
        Double testVal = new Double(2.2);
		Integer value = AdapterUtil.instance.adaptTo(Integer.class, testVal, null);
		assertEquals((int)value, 2);
    }
    
    @Test
    public void testCanAdaptBigDecimalFromDouble() throws Exception {
        Double testVal = new Double(2.2);
        BigDecimal value = AdapterUtil.instance.adaptTo(BigDecimal.class, testVal, null);
		assertEquals((BigDecimal)value, new BigDecimal("2.2"));
    }
}
