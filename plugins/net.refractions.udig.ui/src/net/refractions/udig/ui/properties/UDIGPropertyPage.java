/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.ui.properties;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * Base class containing helpful utility methods when working on your own property page.
 * 
 * @author Jody Garnett (LISAsoft)
 * @since 1.3
 */
public abstract class UDIGPropertyPage extends PropertyPage {
    
    /**
     * Access the object that owns the properties shown in this page.
     * <p>
     * This method handles both instanceof and {@link IAdaptable#getAdapter(Class)} checks in one
     * smooth method call:
     * 
     * <pre>
     * IGeoResource resource = getElement( IGeoResource.class )
     * </pre>
     * <p>
     * It can be used in conjunction with the enablesFor (as a replacement for objectClass).
     * 
     * <pre><code>
     *          &lt;enabledWhen&gt;
     *             &lt;or&gt;
     *                &lt;instanceof
     *                      value="net.refractions.udig.project.internal.Layer"&gt;
     *                &lt;/instanceof&gt;
     *                &lt;adapt
     *                      type="net.refractions.udig.project.internal.Layer"&gt;
     *                &lt;/adapt&gt;
     *             &lt;/or&gt;
     *          &lt;/enabledWhen&gt;
     * </code></pre>
     * 
     * @return the object that owns the properties shown in this page
     */
    protected <T> T getElement( Class<T> adaptor ){
        IAdaptable element = getElement();
        if( element != null ){
            if( adaptor.isInstance( element )){
                return adaptor.cast(element);
            }
            return adaptor.cast( element.getAdapter(adaptor) );
        }
        else {
            return null; // not available
        }
    }

}
