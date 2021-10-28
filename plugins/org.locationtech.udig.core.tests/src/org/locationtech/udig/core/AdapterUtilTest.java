/**
 * uDig - User Friendly Desktop Internet GIS client
 * https://locationtech.org/projects/technology.udig
 * (C) 2021, Eclipse Foundation
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 */
package org.locationtech.udig.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

public class AdapterUtilTest {

    @Test
    public void testNullObjectDefensiveModeReturnsNullObject() throws Exception {
        Object adapter = AdapterUtil.instance.adapt(String.class.getName(), null,
                new NullProgressMonitor());
        assertTrue(adapter == null);
    }

    @Test
    public void testCanAdaptToNullPointerExceptionWithClassLoader() {
        final AdapterUtil instance = AdapterUtil.instance;
        final Object nullObject = null;
        boolean canAdaptTo = instance.canAdaptTo(String.class.getName(), nullObject,
                this.getClass().getClassLoader());
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
        boolean canAdaptTo = AdapterUtil.instance.canAdaptTo(String.class.getName(), testString,
                null);
        assertEquals(false, canAdaptTo);
    }

    @Test
    public void testCanAdaptDoubleFromInteger() throws Exception {
        Integer testVal = Integer.valueOf(2);
        Double value = AdapterUtil.instance.adaptTo(Double.class, testVal, null);
        assertEquals(value, Double.valueOf(2.0));
    }

    @Test
    public void testCanAdaptIntegerFromDouble() throws Exception {
        Double testVal = Double.valueOf(2.2);
        Integer value = AdapterUtil.instance.adaptTo(Integer.class, testVal, null);
        assertEquals(value, Integer.valueOf(2));
    }

    @Test
    public void testCanAdaptBigDecimalFromDouble() throws Exception {
        Double testVal = Double.valueOf(2.2);
        BigDecimal value = AdapterUtil.instance.adaptTo(BigDecimal.class, testVal, null);
        assertEquals(value, BigDecimal.valueOf(2.2));
    }
}
