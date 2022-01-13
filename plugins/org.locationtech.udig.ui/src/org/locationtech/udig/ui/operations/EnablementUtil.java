/**
 * uDig - User Friendly Desktop Internet GIS client
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

/**
 * Utility class for parsing enablement children
 *
 * @author Jesse
 * @since 1.1.0
 */
public class EnablementUtil {

    public static OpFilter parseEnablement(String extensionID, IConfigurationElement[] enablement) {

        if (!validateChildren(extensionID, enablement) || enablement[0] == null) {
            if (enablement.length > 0 && enablement[0] == null)
                LoggingSupport.log(UiPlugin.getDefault(), "EnablementUtil: null enablement"); //$NON-NLS-1$
            return OpFilter.TRUE;
        }

        IConfigurationElement[] children = enablement[0].getChildren();
        if (!validateChildren(extensionID, children)) {
            LoggingSupport.log(UiPlugin.getDefault(),
                    "EnablementUtil: Expected child of " + extensionID + " but didn't find one..."); //$NON-NLS-1$ //$NON-NLS-2$
            return OpFilter.TRUE;
        }
        OpFilterParser parser = new OpFilterParser(
                new FilterParser[] { new AdaptsToParserImpl(), new PropertyParser() });
        return parser.parseFilter(children[0]);

    }

    private static boolean validateChildren(String extensionID, IConfigurationElement[] children) {

        if (children.length < 1) {
            return false;
        }
        if (children.length > 1) {
            LoggingSupport.log(UiPlugin.getDefault(),
                    "EnablementUtil: Error, more than one enablement element " + extensionID); //$NON-NLS-1$
            return false;
        }
        return true;
    }

    public static EnablesForData parseEnablesFor(String enablesFor,
            IConfigurationElement configElem) {
        if (enablesFor == null) {
            enablesFor = "1"; //$NON-NLS-1$
        }

        enablesFor = enablesFor.trim();
        EnablesForData data = new EnablesForData();
        if (enablesFor.equals("+")) { //$NON-NLS-1$
            data.minHits = 1;
            data.exactMatch = false;
        } else if (enablesFor.equals("multiple")) { //$NON-NLS-1$
            data.minHits = 2;
            data.exactMatch = false;
        } else if (enablesFor.equals("2+")) { //$NON-NLS-1$
            data.minHits = 2;
            data.exactMatch = false;
        } else {
            try {
                data.minHits = Integer.parseInt(enablesFor);
                data.exactMatch = true;
            } catch (Exception e) {
                LoggingSupport.log(UiPlugin.getDefault(), "Error parsing extension: " //$NON-NLS-1$
                        + configElem.getNamespace() + "/" + configElem.getName(), e); //$NON-NLS-1$
                data.minHits = 0;
                data.exactMatch = false;
            }
        }
        return data;
    }

    public static class EnablesForData {
        int minHits = 0;

        boolean exactMatch = false;
    }

}
