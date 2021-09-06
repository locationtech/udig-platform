/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.internal.ui.operations;

import java.util.Iterator;
import java.util.function.Consumer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.locationtech.udig.internal.ui.UiPlugin;
import org.locationtech.udig.ui.operations.IOpFilterListener;
import org.locationtech.udig.ui.operations.OpFilter;
import org.locationtech.udig.ui.operations.PropertyValue;

/**
 * Assesses if a tools is valid for a given property value.
 *
 * @author rgould
 * @since 1.1.0
 */
public class OpFilterPropertyValueCondition implements OpFilter {

    static final String ATTRIBUTE_VALUE = "value"; //$NON-NLS-1$

    static final String ATTRIBUTE_CLASS = "class"; //$NON-NLS-1$

    private static final PropertyValue<Object> FAILED_TO_LOAD = new PropertyValue<Object>() {

        @Override
        public void addListener(IOpFilterListener listener) {
        }

        @Override
        public boolean canCacheResult() {
            return true;
        }

        @Override
        public boolean isBlocking() {
            return false;
        }

        @Override
        public boolean isTrue(Object object, String value) {
            return true;
        }

        @Override
        public void removeListener(IOpFilterListener listener) {
        }

    };

    private final String equalsValue;

    private final IConfigurationElement propertyElement;

    private final String targetObject;

    private volatile PropertyValue propertyValueInstance;

    private Class<?> targetClass;

    private Consumer<IStatus> logConsumer;

    public OpFilterPropertyValueCondition(
            IConfigurationElement propertyElement,
            String targetObject, String equalsValue) {
        this(propertyElement, targetObject, equalsValue, UiPlugin::log);
    }

    public OpFilterPropertyValueCondition(
            IConfigurationElement propertyElement,
            String targetObject, String equalsValue, Consumer<IStatus> logConsumer) {
        this.equalsValue = equalsValue;
        this.propertyElement = propertyElement;
        this.targetObject = targetObject;
        this.logConsumer = logConsumer;
    }


    @Override
    public boolean accept( Object object ) {
        if( object instanceof IStructuredSelection ){
            IStructuredSelection selection=(IStructuredSelection) object;
            for( Iterator iter = selection.iterator(); iter.hasNext(); ) {
                Object element = iter.next();
                if( !accepted( element ) )
                    return false;
            }
            return true;
        }else{
            return accepted(object);
        }
    }

    @SuppressWarnings("unchecked")
    private boolean accepted(Object object) {
        if (object == null) {
            return false;
        }

        Class<? extends Object> targetObjectClass = getTargetObject(object);
        if (targetObjectClass == null) {
            return false;
        }

        if (!targetObjectClass.isAssignableFrom(object.getClass())) {
            return false;
        }

        PropertyValue v = getValue();
        if (v == null)
            return false;
        return v.isTrue(object, this.equalsValue);
    }

    private PropertyValue getValue() {
        if (propertyValueInstance == null) {
            try {
                String classAttribute = propertyElement.getAttribute(ATTRIBUTE_CLASS);
                if (classAttribute == null || classAttribute.trim().length() == 0) {
                    // fall back case to deprecated tag
                    propertyValueInstance = (PropertyValue) propertyElement
                            .createExecutableExtension(ATTRIBUTE_VALUE);
                } else {
                    propertyValueInstance = (PropertyValue) propertyElement
                            .createExecutableExtension(ATTRIBUTE_CLASS);
                }
            } catch (ClassCastException | CoreException e) {
                propertyValueInstance = FAILED_TO_LOAD;
                log(IStatus.WARNING,
                        "Error in extension: " //$NON-NLS-1$
                        + propertyElement.getDeclaringExtension().getUniqueIdentifier(), e);

            }
        }
        return propertyValueInstance;
    }

    private Class<? extends Object> getTargetObject(Object object) {

        if (targetClass == null) {
            try {
                targetClass = object.getClass().getClassLoader().loadClass(targetObject);
            } catch (ClassNotFoundException e) {
                log(IStatus.INFO, "", e); //$NON-NLS-1$
                return null;
            }
        }

        return targetClass;
    }

    @Override
    public void addListener( IOpFilterListener listener ) {
        getValue().addListener(listener);
    }

    @Override
    public boolean canCacheResult() {
        return getValue().canCacheResult();
    }

    @Override
    public boolean isBlocking() {
        return getValue().isBlocking();
    }

    @Override
    public void removeListener( IOpFilterListener listener ) {
        getValue().removeListener(listener);
    }

    @Override
    public String toString() {
        if (propertyValueInstance == null || FAILED_TO_LOAD.equals(propertyValueInstance)) {
            return "PropertyValue " + propertyElement.getAttribute(ATTRIBUTE_CLASS); // $NON-NLS-1$
        }
        else {
            return propertyValueInstance.getClass().getSimpleName();
        }
    }

    private void log(int severity, String message, Throwable throwable) {
        if (logConsumer != null) {
            logConsumer.accept(new Status(severity, UiPlugin.ID, message, throwable));
        }
    }
}
