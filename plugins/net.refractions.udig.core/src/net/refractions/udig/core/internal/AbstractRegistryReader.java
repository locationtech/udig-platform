/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.core.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Abstract implementation of a registry reader that creates objects representing registry contents.
 * <p>
 * Typically, an extension contains one element, but this reader handles multiple elements per
 * extension.
 * </p>
 * <p>
 * To start reading the extensions from the registry for an extension point, call the method
 * <code>readRegistry</code>.
 * </p>
 * <p>
 * To read children of an IConfigurationElement, call the method <code>readElementChildren</code>
 * from your implementation of the method <code>readElement</code>, as it will not be done by
 * default.
 * </p>
 * <p>
 * This class is similar to the internal class RegistryReader provided by the framework.
 * </p>
 */
public abstract class AbstractRegistryReader {
    protected static final String TAG_DESCRIPTION = "description"; //$NON-NLS-1$
    final private ILog pluginLog;
    final private String pluginId;

    /**
     * The constructor (requires a workbench for logging).
     *
     * @param log ILog for status messages
     * @param id Plugin ID for status messages
     */
    protected AbstractRegistryReader( ILog log, String id ) {
        this.pluginLog = log;
        this.pluginId = id;
    }
    /**
     * This method extracts description as a subelement of the given element.
     *
     * @param config Configuration defining extention point
     * @return description string if defined, or empty string if not.
     */
    protected String getDescription( IConfigurationElement config ) {
        IConfigurationElement[] children = config.getChildren(TAG_DESCRIPTION);
        if (children.length >= 1) {
            return children[0].getValue();
        }
        return "";//$NON-NLS-1$
    }
    /**
     * Logs the error in the plugin log using the provided text and the information in the
     * configuration element.
     *
     * @param element
     * @param text
     */
    protected void logError( IConfigurationElement element, String text ) {
        IExtension extension = element.getDeclaringExtension();
        StringBuffer buf = new StringBuffer();
        buf
                .append("Plugin " + extension.getNamespace() + ", extension " + extension.getExtensionPointUniqueIdentifier());//$NON-NLS-2$//$NON-NLS-1$
        buf.append("\n" + text);//$NON-NLS-1$
        String message = buf.toString();

        this.pluginLog.log(newStatus(IStatus.ERROR, message, null));
        System.err.println(message);
    }
    /**
     * Logs a very common registry error when a required attribute is missing.
     *
     * @param element
     * @param attributeName
     */
    protected void logMissingAttribute( IConfigurationElement element, String attributeName ) {
        logError(element, "Required attribute '" + attributeName + "' not defined");//$NON-NLS-2$//$NON-NLS-1$
    }

    /**
     * Logs a very common registry error when a required child is missing.
     *
     * @param element
     * @param elementName
     */
    protected void logMissingElement( IConfigurationElement element, String elementName ) {
        logError(element, "Required sub element '" + elementName + "' not defined");//$NON-NLS-2$//$NON-NLS-1$
    }

    /**
     * Logs a registry error when the configuration element is unknown.
     *
     * @param element
     */
    protected void logUnknownElement( IConfigurationElement element ) {
        logError(element, "Unknown extension tag found: " + element.getName());//$NON-NLS-1$
    }
    /**
     * IStatus creations (with respect to plugin id).
     *
     * @param severity IStatus constant such as IStatus.OK, or IStatus.ERROR
     * @param message Status message
     * @param exception Cause of message, may be null
     * @return IStatus suitable for use with plugin.getLog.log( IStatus )
     */
    protected IStatus newStatus( int severity, String message, Throwable exception ) {
        String statusMessage = message;
        if (message == null || message.trim().length() == 0) {
            if (exception.getMessage() == null)
                statusMessage = exception.toString();
            else
                statusMessage = exception.getMessage();
        }
        // Not sure if this is the correct way to grab Id
        return new Status(severity, pluginId, severity, statusMessage, exception);
    }
    /**
     * Apply a reproducable order to the list of extensions provided, such that the order will not
     * change as extensions are added or removed.
     *
     * @param extensions Ordered Extentions
     * @return IExtentions
     */
    @SuppressWarnings("unchecked")
    protected IExtension[] orderExtensions( IExtension[] extensions ) {
        // By default, the order is based on plugin id sorted
        // in ascending order. The order for a plugin providing
        // more than one extension for an extension point is
        // dependent in the order listed in the XML file.
        IExtension[] sortedExtension = new IExtension[extensions.length];
        System.arraycopy(extensions, 0, sortedExtension, 0, extensions.length);
        Comparator comparer = new Comparator(){
            public int compare( Object arg0, Object arg1 ) {
                String s1 = ((IExtension) arg0).getNamespace();
                String s2 = ((IExtension) arg1).getNamespace();
                return s1.compareToIgnoreCase(s2);
            }
        };
        Collections.sort(Arrays.asList(sortedExtension), comparer);
        return sortedExtension;
    }
    /**
     * Implement this method to read element's attributes. If children should also be read, then
     * implementor is responsible for calling <code>readElementChildren</code>. Implementor is
     * also responsible for logging missing attributes.
     *
     * @param element
     * @return true if element was recognized, false if not.
     */
    protected abstract boolean readElement( IConfigurationElement element );
    /**
     * Read the element's children. This is called by the subclass' readElement method when it wants
     * to read the children of the element.
     *
     * @param element
     */
    protected void readElementChildren( IConfigurationElement element ) {
        readElements(element.getChildren());
    }
    /**
     * Read each element one at a time by calling the subclass implementation of
     * <code>readElement</code>. Logs an error if the element was not recognized.
     *
     * @param elements
     */
    protected void readElements( IConfigurationElement[] elements ) {
        for( int i = 0; i < elements.length; i++ ) {
            if (!readElement(elements[i]))
                logUnknownElement(elements[i]);
        }
    }
    /**
     * Read one extension by looping through its configuration elements.
     *
     * @param extension
     */
    protected void readExtension( IExtension extension ) {
        readElements(extension.getConfigurationElements());
    }
    /**
     * Start the registry reading process using the supplied plugin ID and extension point.
     *
     * @param registry
     * @param targetPluginId
     * @param extensionPoint
     */
    public void readRegistry( IExtensionRegistry registry, String targetPluginId,
            String extensionPoint ) {
        IExtensionPoint point = registry.getExtensionPoint(targetPluginId, extensionPoint);
        if (point == null)
            return;
        IExtension[] extensions = point.getExtensions();
        extensions = orderExtensions(extensions);
        for( int i = 0; i < extensions.length; i++ )
            readExtension(extensions[i]);
    }
}
