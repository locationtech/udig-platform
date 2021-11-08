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
package org.locationtech.udig.render.wms.basic.internal;

import static org.junit.Assert.assertEquals;
import static org.locationtech.udig.render.wms.basic.internal.WMSRequestUtil.getRequestImageFormatBestMatch;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.geotools.data.ows.OperationType;
import org.geotools.ows.wms.WMSCapabilities;
import org.geotools.ows.wms.WMSRequest;
import org.geotools.ows.wms.WebMapServer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * @author fgdrf
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class WMSRequestUtilTest {

    @Mock
    WebMapServer webMapServer;

    @Mock
    WMSCapabilities wmsCapabilities;

    @Mock
    WMSRequest wmsRequest;

    @Mock
    OperationType operationType;

    @Before
    public void beforeTest() {
        when(webMapServer.getCapabilities()).thenReturn(wmsCapabilities);
        when(wmsCapabilities.getRequest()).thenReturn(wmsRequest);
        when(wmsRequest.getGetMap()).thenReturn(operationType);

    }

    @Test
    public void getFormat_onlyOneMatch() {
        when(operationType.getFormats()).thenReturn(Collections.singletonList("image/jpg"));

        assertEquals("image/jpg", getRequestImageFormatBestMatch(webMapServer,
                new String[] { "image/png8", "image/png", "image/jpg" }));
    }

    @Test
    public void getFormat_useFirstMatchFromPreferredList() {
        when(operationType.getFormats()).thenReturn(Arrays.asList("image/jpg", "image/png"));

        assertEquals("image/png", getRequestImageFormatBestMatch(webMapServer,
                new String[] { "image/png8", "image/png", "image/jpg" }));
    }

//    @Test
//    public void getFormat_useFirstEntryFromPreferredList_() {
//        when(operationType.getFormats()).thenReturn(Arrays.asList("image/jpg", "image/png"));
//    }

    @Test
    public void getFormat_useFirstFromCapabilities_preferredListIsNull() {
        when(operationType.getFormats()).thenReturn(Arrays.asList("image/jpg", "image/png"));
        assertEquals("image/jpg", getRequestImageFormatBestMatch(webMapServer, null));

    }

    @Test
    public void getFormat_useFirstFromCapabilities_preferredListIsEmpty() {
        String[] emptyPreferredList = new String[] {};
        when(operationType.getFormats()).thenReturn(Arrays.asList("image/jpg", "image/png"));
        assertEquals("image/jpg", getRequestImageFormatBestMatch(webMapServer, emptyPreferredList));
    }

    @Test
    public void getFormat_useFirstFromCapabilities_noMatch() {
        when(operationType.getFormats()).thenReturn(Arrays.asList("image/jpg", "image/png"));
        assertEquals("image/jpg", getRequestImageFormatBestMatch(webMapServer, new String[] {"image/tiff8"}));
    }

    @Test
    public void getFormat_useFirstFromCapabilities_upperCaseInPreferredList() {
        when(operationType.getFormats()).thenReturn(Arrays.asList("image/jpg", "image/png"));
        assertEquals("image/jpg", getRequestImageFormatBestMatch(webMapServer, new String[] {"IMAGE/JPG"}));
    }
}
