/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2020, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.operations;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.locationtech.udig.project.ILayer;
import org.opengis.filter.Filter;

@RunWith(Parameterized.class)
public class LayerSelectionPropertyParameterTest {
    private Boolean expectedResult;

    private Filter givenFilter;

    private String givenValue;

    @SuppressWarnings("rawtypes")
    @Parameterized.Parameters
    public static Collection primeNumbers() {
        return Arrays.asList(new Object[][] {
                // no selection on layer
                { false, Filter.EXCLUDE, "true" }, { false, Filter.EXCLUDE, "TRUE" },
                { false, Filter.EXCLUDE, null }, { false, Filter.EXCLUDE, "True" },
                { true, Filter.EXCLUDE, "false" }, { true, Filter.EXCLUDE, "FALSE" },
                { true, Filter.EXCLUDE, "False" },

                // layer has selection
                { true, Filter.INCLUDE, "true" }, { true, Filter.INCLUDE, "TRUE" },
                { true, Filter.INCLUDE, "True" }, { true, Filter.INCLUDE, null },
                { false, Filter.INCLUDE, "false" }, { false, Filter.INCLUDE, "FALSE" },
                { false, Filter.INCLUDE, "False" },

                // without value or anything else than boolean values it does not really make sense
                { true, Filter.EXCLUDE, "whatever" }, { false, Filter.INCLUDE, "whatever" }, });
    }

    public LayerSelectionPropertyParameterTest(boolean expected, Filter givenFilter,
            String givenValue) {
        this.expectedResult = expected;
        this.givenFilter = givenFilter;
        this.givenValue = givenValue;
    }

    ILayer layerMock = createNiceMock(ILayer.class);

    @Test
    public void testHasSelection() {
        expect(layerMock.getFilter()).andReturn(givenFilter).anyTimes();
        LayerSelectionProperty layerSelectionProperty = new LayerSelectionProperty();
        replay(layerMock);
        assertEquals(
                MessageFormat.format("for {0} with expectedValue {1}", givenFilter, givenValue),
                expectedResult, layerSelectionProperty.isTrue(layerMock, givenValue));

        verify(layerMock);
    }
}
