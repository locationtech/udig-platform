package org.locationtech.udig.internal.ui.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.function.Consumer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.udig.ui.operations.PropertyValue;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OpFilterPropertyValueConditionTest {

    private static final String TEST_UNIQUE_ID = "test.id";

    @Mock
    IConfigurationElement configElement;

    @Mock
    Consumer<IStatus> consumerMock;

    @Mock
    IExtension extension;

    @Test
    public void testClassIsAnyThingElseThanPropertyValue() throws CoreException {
        when(extension.getUniqueIdentifier()).thenReturn(TEST_UNIQUE_ID);
        when(configElement.getAttribute(OpFilterPropertyValueCondition.ATTRIBUTE_CLASS))
                .thenReturn("org.locationtech.udig.ui.internal.test.TestClass");
        when(configElement.createExecutableExtension("class"))
                .thenReturn("this is obviously no " + PropertyValue.class.getName());

        when(configElement.getDeclaringExtension()).thenReturn(extension);
        consumerMock.accept(Mockito.any());

        OpFilterPropertyValueCondition myValCondition = new OpFilterPropertyValueCondition(
                configElement, Object.class.getName(), "testValue", consumerMock);

        assertFalse(myValCondition.isBlocking());

        verify(extension, times(1)).getUniqueIdentifier();
        verify(configElement, times(1))
                .getAttribute(OpFilterPropertyValueCondition.ATTRIBUTE_CLASS);
        verify(consumerMock, atLeastOnce()).accept(Mockito.any());
    }

    @Test
    public void testPropertyValue() throws CoreException {
        @SuppressWarnings("unchecked")
        PropertyValue<String> testPropertyValue = mock(PropertyValue.class);

        when(testPropertyValue.isBlocking()).thenReturn(true);
        when(testPropertyValue.canCacheResult()).thenReturn(false);

        when(configElement.getAttribute(OpFilterPropertyValueCondition.ATTRIBUTE_CLASS))
                .thenReturn(testPropertyValue.getClass().getName());
        when(configElement
                .createExecutableExtension(OpFilterPropertyValueCondition.ATTRIBUTE_CLASS))
                        .thenReturn(testPropertyValue);

        OpFilterPropertyValueCondition myValCondition = new OpFilterPropertyValueCondition(
                configElement, testPropertyValue.getClass().getName(), "testValue", consumerMock);

        assertEquals(testPropertyValue.isBlocking(), myValCondition.isBlocking());
        assertEquals(testPropertyValue.canCacheResult(), myValCondition.canCacheResult());
        assertTrue(myValCondition.accept(org.eclipse.jface.viewers.StructuredSelection.EMPTY));

        verify(configElement, times(1))
                .getAttribute(OpFilterPropertyValueCondition.ATTRIBUTE_CLASS);
        verify(configElement, times(1))
                .createExecutableExtension(OpFilterPropertyValueCondition.ATTRIBUTE_CLASS);
        verify(testPropertyValue, atLeastOnce()).isBlocking();
        verify(testPropertyValue, atLeastOnce()).canCacheResult();
    }

    @Test
    public void testClassLoadingCoreExceptionBehavior() throws CoreException {
        IStatus errorStatus = new Status(IStatus.ERROR, "test.plugin", "just an error");

        when(extension.getUniqueIdentifier()).thenReturn(TEST_UNIQUE_ID);
        when(configElement.getAttribute(OpFilterPropertyValueCondition.ATTRIBUTE_CLASS))
                .thenReturn(null);
        when(configElement
                .createExecutableExtension(OpFilterPropertyValueCondition.ATTRIBUTE_VALUE))
                        .thenThrow(new CoreException(errorStatus));
        when(configElement.getDeclaringExtension()).thenReturn(extension);

        consumerMock.accept(Mockito.any());

        OpFilterPropertyValueCondition myValCondition = new OpFilterPropertyValueCondition(
                configElement, Object.class.getName(), "testValue", consumerMock);

        assertTrue("Caching Result should be true, if class element cannot be created",
                myValCondition.canCacheResult());

        assertFalse("Should be a non blocking operation", myValCondition.isBlocking());

        verify(extension, times(1)).getUniqueIdentifier();
        verify(configElement, times(1))
                .getAttribute(OpFilterPropertyValueCondition.ATTRIBUTE_CLASS);
        verify(configElement, times(1))
                .createExecutableExtension(OpFilterPropertyValueCondition.ATTRIBUTE_VALUE);
        verify(consumerMock, atLeastOnce()).accept(Mockito.any());
    }
}
