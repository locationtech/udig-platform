/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package net.refractions.udig.core.internal;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Plugin;

/**
 * This interface should be implemented by all classes that wish to process Extensions.
 * 
 * @see net.refractions.udig.core.internal.ExtensionPointUtil
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public interface ExtensionPointProcessor {
    /**
     * This method is called by ExtensionPointUtil.process(String, ExtensionPointProcessor). This is
     * a callback method for the ExtensionPointUtil class.
     * 
     * @see net.refractions.udig.core.internal.ExtensionPointUtil#process(Plugin, String,
     *      ExtensionPointProcessor)
     * @param extension An extension that extends the Extension point id specified in the
     *        ExtensionPointUtil.process() call.
     * @param element One of the configuration element of the extension. The process method is
     *        called once for each configuration element
     * @throws Exception Exception will be reported to user in the workbench log
     */
    public void process( IExtension extension, IConfigurationElement element ) throws Exception;
}
