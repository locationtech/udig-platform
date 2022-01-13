/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui.operations;

import org.eclipse.core.runtime.IConfigurationElement;
import org.locationtech.udig.core.logging.LoggingSupport;
import org.locationtech.udig.internal.ui.UiPlugin;

public class AdaptsToParserImpl implements FilterParser {

    @Override
    public OpFilter parse(IConfigurationElement element) {
        String adaptsTo = element.getAttribute("target"); //$NON-NLS-1$
        if (adaptsTo.trim().length() == 0) {
            LoggingSupport.log(UiPlugin.getDefault(),
                    "AdaptsToParserImpl:Parsing OpFilter: adapts to attribute is empty string" //$NON-NLS-1$
                            + element.getNamespaceIdentifier(),
                    null);

            return OpFilter.TRUE;
        }
        return new AdaptsToFilter(adaptsTo);
    }

    @Override
    public String getElementName() {
        return "adaptsTo"; //$NON-NLS-1$
    }
}
