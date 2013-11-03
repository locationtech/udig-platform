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
package org.locationtech.udig.core.internal;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

/**
 * Process an Extention Point into items for a List.
 * <p>
 * This interface works with a different set of assumptions then Jesse's origional. It is influenced
 * by the guidelines in "Contributing to Eclipse", and is focused on Object (possibly proxy)
 * creation.
 * </p>
 * <p>
 * Assumptions:
 * <ul>
 * <li>Most of the time client code just wants to create an Object, or Proxy
 * <li>Good Fences Rule / Safe Platform Rule - processor makes sure to trap any exceptions, so a
 * single extention point cannot hinder others
 * <li>Conformance Rule - contributions must conform to expected interfaces, we can use Java 5
 * generics to force this point
 * <li>Is an abstract class allowing safe api extention in the future.
 * </ul>
 * </p>
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
 * @author jgarnett
 * @since 0.6
 */
public abstract class ExtensionPointItemCreator {
    /**
     * Process this extention point into a List.
     * 
     * @param xpid Extention point to process
     * @return List of items created
     */
    public List process( String xpid ) {
        return ExtensionPointUtil.list(xpid, this);
    }
    /**
     * Method called to create a List item from an extention point. This is a callback method for
     * the ExtensionPointUtil class.
     * 
     * @param extension An extension that extends the Extension point id specified in the
     *        ExtensionPointUtil.process() call.
     * @param element One of the configuration element of the extension. The process method is
     *        called once for each configuration element
     * @return Created Object (often a Proxy), throws an Exception if extention could not be
     *         processed returns true if this methods wants the ExtensionPointUtil#process() method
     *         to continue calling this method. false if no more calls need to be made.
     * @throws Exception If extention point could not be processed, a ExtentionPointUtil will create
     *         the appropriate log message
     */
    public abstract Object createItem( IExtension extension, IConfigurationElement element )
            throws Exception;

}
