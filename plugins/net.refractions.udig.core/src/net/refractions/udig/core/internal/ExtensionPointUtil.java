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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

/**
 * A utility class to assist in processing extensions
 *
 * @see net.refractions.udig.core.internal.ExtensionPointProcessor
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public class ExtensionPointUtil {

    /**
     * Finds all the Extension or the Extension point identified by the xpid method and calls a
     * callback method on the processor class for processing of the extension.
     *
     * @see net.refractions.udig.core.internal.ExtensionPointProcessor#process(IExtension,
     *      IConfigurationElement)
     * @param xpid The id of the ExtensionPoint for which the extensions are to be processed
     * @param processor The object that wishes to process the extension for the ExtensionPoint
     * @deprecated Please use process( Plugin, String, ExtentionPointProcessor ) - so we can
     */
    public static void process( String xpid, ExtensionPointProcessor processor ) {
        process(CorePlugin.getDefault(), xpid, processor);
    }

    /**
     * Finds all the Extension or the Extension point identified by the xpid method and calls a
     * callback method on the processor class for processing of the extension.
     *
     * @see net.refractions.udig.core.internal.ExtensionPointProcessor#process(IExtension,
     *      IConfigurationElement)
     * @param plugin plugin processing this extention point
     * @param xpid The id of the ExtensionPoint for which the extensions are to be processed
     * @param processor The object that wishes to process the extension for the ExtensionPoint
     */
    public static void process( Plugin plugin, String xpid, ExtensionPointProcessor processor ) {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint extensionPoint = registry.getExtensionPoint(xpid);
        if (extensionPoint == null)
            return;
        IExtension[] extensions = extensionPoint.getExtensions();

        // For each extension ...
        for( int i = 0; i < extensions.length; i++ ) {
            IExtension extension = extensions[i];
            IConfigurationElement[] elements = extension.getConfigurationElements();

            // For each member of the extension ...
            for( int j = 0; j < elements.length; j++ ) {
                IConfigurationElement element = elements[j];
                try {
                    processor.process(extension, element);
                } catch (Throwable exception) {
                    plugin
                            .getLog()
                            .log(
                                    new Status(
                                            IStatus.ERROR,
                                            element.getNamespaceIdentifier(),
                                            0,
                                            MessageFormat
                                                    .format(
                                                            "Error processing extension {0}", new Object[]{exception}), exception)); //$NON-NLS-1$
                }
            }
        }
    }

    /**
     * Process extentionpoint with an itemCreator.
     * <p>
     * Example Use:
     *
     * <pre><code>
     *  List&lt;Thingy&gt; stuff = ExtentionPointUtil.list( new ExtentionPointProcessor2(){
     *     public Object process( IExtention extention, IConfigurationElement element ){
     *         return new Thingy( element );
     *     }
     *  }
     * </code></pre>
     *
     * </p>
     *
     * @param xpid
     * @param itemCreator Used to process extention points into items for a list
     * @return List
     */
    public static List list( String xpid, ExtensionPointItemCreator itemCreator ) {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint extensionPoint = registry.getExtensionPoint(xpid);
        if (extensionPoint == null) {
            return Collections.EMPTY_LIST;
        }
        IExtension[] extensions = extensionPoint.getExtensions();
        List<Object> list = new ArrayList<Object>();
        // For each extension ...
        for( int i = 0; i < extensions.length; i++ ) {
            IExtension extension = extensions[i];
            IConfigurationElement[] elements = extension.getConfigurationElements();

            // For each member of the extension ...
            for( int j = 0; j < elements.length; j++ ) {
                IConfigurationElement element = elements[j];
                try {
                    Object obj = itemCreator.createItem(extension, element);
                    if (obj == null)
                        continue; // warning?

                    list.add(obj);
                } catch (Throwable exception) {
                    CorePlugin
                            .getDefault()
                            .getLog()
                            .log(
                                    new Status(
                                            IStatus.WARNING,
                                            extension.getNamespace(),
                                            0,
                                            MessageFormat
                                                    .format(
                                                            "Error processing extension {0}", new Object[]{exception}), exception)); //$NON-NLS-1$
                }
            }
        }
        return list;
    }
}
