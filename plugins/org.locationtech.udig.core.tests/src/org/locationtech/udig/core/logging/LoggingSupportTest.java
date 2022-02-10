/**
 * uDig - User Friendly Desktop Internet GIS client
 * https://locationtech.org/projects/technology.udig
 * (C) 2022, Eclipse Foundation
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 */
package org.locationtech.udig.core.logging;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.osgi.framework.Bundle;

@RunWith(MockitoJUnitRunner.class)
public class LoggingSupportTest {

    private static final String EXPECTED_ERROR_MSG = "expect error test";
    private static final String BUNDLE_NAME = "test.bundle";

    @Mock
    Plugin plugin;

    @Mock
    ILog log;

    @Mock
    Bundle bundle;

    @Captor
    ArgumentCaptor<IStatus> statusCapture;

    @Before
    public void setUp() {
        when(plugin.getLog()).thenReturn(log);
        when(plugin.getBundle()).thenReturn(bundle);
        when(bundle.getSymbolicName()).thenReturn(BUNDLE_NAME);
    }

    @Test
    public void logErrorSeverityWhileExceptionIsGiven() {
        LoggingSupport.log(plugin, "whatever", new Exception(EXPECTED_ERROR_MSG));

        verify(log).log(statusCapture.capture());
        assertEquals(IStatus.ERROR, statusCapture.getValue().getSeverity());
    }

    @Test
    public void logWarningSeverityWhileThrowableIsGiven() {
        String errorMessage = "ErrorMessage 1";
        LoggingSupport.log(plugin, errorMessage, new Throwable(EXPECTED_ERROR_MSG));

        verify(log).log(statusCapture.capture());
        assertStatus(IStatus.WARNING, errorMessage, BUNDLE_NAME, statusCapture.getValue());
    }

    @Test
    public void logInfoSeverityWithNullThrowableIsGiven() {
        String errorMessage = "ErrorMessage 1";
        LoggingSupport.log(plugin, errorMessage, null);

        verify(log).log(statusCapture.capture());
        assertStatus(IStatus.INFO, errorMessage, BUNDLE_NAME, statusCapture.getValue());
    }

    @Test
    public void logWithNullMessage() {
        LoggingSupport.log(plugin, null, new Exception(EXPECTED_ERROR_MSG));

        verify(log).log(statusCapture.capture());
        assertStatus(IStatus.ERROR, "", BUNDLE_NAME, statusCapture.getValue());
    }

    @Test
    public void doNotLogAnythingNullMessageAndNullThrowable() {
        LoggingSupport.log(plugin, null, null);
        verifyNoMoreInteractions(log);
    }

    @Test
    public void doNotLogAnythingEmptyMessageAndNullThrowable() {
        LoggingSupport.log(plugin, "", null);
        verifyNoMoreInteractions(log);
    }

    private void assertStatus(int expectedSeverity, String expectedMessage,
            String expectedBundleName, IStatus givenStatus) {
        assertEquals(expectedSeverity, givenStatus.getSeverity());
        assertEquals(expectedMessage, givenStatus.getMessage());
        assertEquals(expectedBundleName, givenStatus.getPlugin());
    }

}
