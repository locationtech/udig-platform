/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.ui.operations;

import net.refractions.udig.internal.ui.UiPlugin;

import org.eclipse.core.runtime.IConfigurationElement;

public class AdaptsToParserImpl implements FilterParser { 

    public OpFilter parse( IConfigurationElement element ) {
        String adaptsTo = element.getAttribute("target"); //$NON-NLS-1$
        if( adaptsTo.trim().length()==0 ){
            UiPlugin.log("AdaptsToParserImpl:Parsing OpFilter: adapts to attribute is empty string"+element.getNamespaceIdentifier(), null); //$NON-NLS-1$

            return OpFilter.TRUE;
        }
        return new AdaptsToFilter(adaptsTo);
    }

    public String getElementName() {
        return "adaptsTo"; //$NON-NLS-1$
    }
}