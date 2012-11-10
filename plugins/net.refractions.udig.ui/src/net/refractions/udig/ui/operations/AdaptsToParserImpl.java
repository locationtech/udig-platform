/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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