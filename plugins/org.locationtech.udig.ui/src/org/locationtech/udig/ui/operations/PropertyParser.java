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
import org.eclipse.core.runtime.Platform;
import org.locationtech.udig.core.logging.LoggingSupport;
import org.locationtech.udig.internal.ui.UiPlugin;
import org.locationtech.udig.internal.ui.operations.OpFilterPropertyValueCondition;

/**
 * Parser to read property definitions from plugin.xml Extensions.
 */
public class PropertyParser implements FilterParser {

    public PropertyParser() {
        super();
    }

    @Override
    public OpFilter parse(IConfigurationElement element) {
        String desiredPropertyId = element.getAttribute("propertyId"); //$NON-NLS-1$
        // try the deprecated one if the required new one is not there
        if (desiredPropertyId == null || desiredPropertyId.trim().length() == 0)
            desiredPropertyId = element.getAttribute("name"); //$NON-NLS-1$
        String expectedValue = element.getAttribute("expectedValue"); //$NON-NLS-1$

        if (desiredPropertyId == null || desiredPropertyId.length() == 0) {
            LoggingSupport.log(UiPlugin.getDefault(),
                    "EnablesFor element is not valid. PropertyId must be supplied."); //$NON-NLS-1$
            return OpFilter.TRUE;
        }

        IConfigurationElement[] configuration = Platform.getExtensionRegistry()
                .getConfigurationElementsFor("org.locationtech.udig.ui.objectProperty"); //$NON-NLS-1$
        IConfigurationElement propertyElement = null;
        String targetClass = null;
        for (IConfigurationElement configurationElement : configuration) {
            if (propertyElement != null)
                break;
            IConfigurationElement[] children = configurationElement.getChildren("property"); //$NON-NLS-1$
            for (IConfigurationElement child : children) {
                String currentPropertyID = child.getAttribute("id"); //$NON-NLS-1$
                String currentPropertyID2 = child.getNamespaceIdentifier() + "." //$NON-NLS-1$
                        + currentPropertyID;

                if (currentPropertyID.equals(desiredPropertyId)
                        || currentPropertyID2.equals(desiredPropertyId)) {
                    propertyElement = child;
                    targetClass = configurationElement.getAttribute("targetClass"); //$NON-NLS-1$
                    // try the deprecated one if the required new one is not there
                    if (targetClass == null || targetClass.trim().length() == 0)
                        targetClass = configurationElement.getAttribute("class"); //$NON-NLS-1$
                    break;
                }
            }
        }

        if (propertyElement == null) {
            LoggingSupport.log(UiPlugin.getDefault(),
                    "PropertyParser: Parsing PropertyValue, desired Propert: " + desiredPropertyId //$NON-NLS-1$
                            + " not found.  Referenced in plugin: " //$NON-NLS-1$
                            + element.getNamespaceIdentifier());
            return OpFilter.TRUE;
        }

        if (targetClass == null) {
            LoggingSupport.log(UiPlugin.getDefault(),
                    "PropertyParser: Parsing PropertyValue, no target class defined in property" //$NON-NLS-1$
                            + desiredPropertyId);

            return OpFilter.TRUE;
        }

        return new OpFilterPropertyValueCondition(propertyElement, targetClass, expectedValue);
    }

    @Override
    public String getElementName() {
        return "property"; //$NON-NLS-1$
    }
}
