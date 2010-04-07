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
import org.eclipse.core.runtime.Platform;



public class PropertyParser implements FilterParser {

    public PropertyParser( ) {
        super();
    }


    public OpFilter parse( IConfigurationElement element ) {
        String desiredPropertyId = element.getAttribute("propertyId"); //$NON-NLS-1$
        // try the deprecated one if the required new one is not there
        if( desiredPropertyId==null || desiredPropertyId.trim().length()==0 )
            desiredPropertyId = element.getAttribute("name"); //$NON-NLS-1$
        String expectedValue = element.getAttribute("expectedValue");       //$NON-NLS-1$
        
        if (desiredPropertyId == null || desiredPropertyId.length() == 0 ) {
            UiPlugin.log("EnablesFor element is not valid. PropertyId must be supplied.", null); //$NON-NLS-1$
            return OpFilter.TRUE;
        }
        
        IConfigurationElement[] configuration = Platform.getExtensionRegistry().getConfigurationElementsFor("net.refractions.udig.ui.objectProperty"); //$NON-NLS-1$
        IConfigurationElement propertyElement=null;
        String targetClass = null; 
        for( IConfigurationElement configurationElement : configuration ) {
            if( propertyElement!=null )
                break;
            IConfigurationElement[] children = configurationElement.getChildren("property"); //$NON-NLS-1$
            for( IConfigurationElement child : children ) {
                String currentPropertyID = child.getAttribute("id"); //$NON-NLS-1$
                currentPropertyID=child.getNamespaceIdentifier()+"."+currentPropertyID; //$NON-NLS-1$
                if( currentPropertyID.equals(desiredPropertyId)){
                    propertyElement = child;
                    targetClass = configurationElement.getAttribute("targetClass"); //$NON-NLS-1$
                    // try the deprecated one if the required new one is not there
                    if( targetClass==null || targetClass.trim().length()==0 )
                        targetClass = configurationElement.getAttribute("class"); //$NON-NLS-1$
                    break;
                }
            }
        }
        
        if ( propertyElement==null ){
            UiPlugin.log("PropertyParser: Parsing PropertyValue, desired Propert: "+desiredPropertyId+" not found.  Referenced in plugin: "+element.getNamespaceIdentifier(), null); //$NON-NLS-1$ //$NON-NLS-2$
            return OpFilter.TRUE;
        }
        
        if (targetClass==null ){
            UiPlugin.log("PropertyParser: Parsing PropertyValue, no target class defined in property"+desiredPropertyId, null); //$NON-NLS-1$

            return OpFilter.TRUE;
        }
        
        OpFilterPropertyValueCondition enablesFor;
        enablesFor = new OpFilterPropertyValueCondition(propertyElement, targetClass, expectedValue);

        
        return enablesFor;
    }     
    
    public String getElementName() {
        return "property"; //$NON-NLS-1$
    }
}