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

/**
 * Utility class for parsing enablement children
 * @author Jesse
 * @since 1.1.0
 */
public class EnablementUtil {

    public static OpFilter parseEnablement( String extensionID, IConfigurationElement[] enablement ) {
        
        if( !validateChildren(extensionID, enablement) || enablement[0]==null ){
            if( enablement.length>0 && enablement[0]==null )
                UiPlugin.log("EnablementUtil: null enablement", null); //$NON-NLS-1$
            return OpFilter.TRUE;
        }
        
        IConfigurationElement[] children = enablement[0].getChildren();
        if ( !validateChildren( extensionID, children) ){
            UiPlugin.log("EnablementUtil: Expected child of "+extensionID+" but didn't find one...", null); //$NON-NLS-1$ //$NON-NLS-2$
            return OpFilter.TRUE;
        }
        OpFilterParser parser = new OpFilterParser(new FilterParser[]{
                new AdaptsToParserImpl(), new PropertyParser()
        });
        return parser.parseFilter(children[0]);

    }

    private static boolean  validateChildren( String extensionID, IConfigurationElement[] children ) {
        
        if( children.length<1){
            return false;
        }
        if( children.length > 1){
            UiPlugin.log("EnablementUtil: Error, more than one enablement element "+extensionID, null); //$NON-NLS-1$
            return false;
        }
        return true;
    }

    

    public static EnablesForData parseEnablesFor( String enablesFor, IConfigurationElement configElem ) {
        if( enablesFor==null ) {
            enablesFor="1"; //$NON-NLS-1$
        }
        
        enablesFor=enablesFor.trim();
        EnablesForData data=new EnablesForData();
        if( enablesFor.equals("+") ){ //$NON-NLS-1$
            data.minHits=1;
            data.exactMatch=false;
        } else if( enablesFor.equals("multiple") ){ //$NON-NLS-1$
            data.minHits=2;
            data.exactMatch=false;
        } else if( enablesFor.equals("2+") ){ //$NON-NLS-1$
            data.minHits=2;
            data.exactMatch=false;
        } else {
            try{
                data.minHits=Integer.parseInt(enablesFor);
                data.exactMatch=true;
            }catch (Exception e) {
                UiPlugin.log("Error parsing extension: "+configElem.getNamespace()+"/"+configElem.getName(), e);  //$NON-NLS-1$//$NON-NLS-2$
                data.minHits=0;
                data.exactMatch=false;
            }
        }
        return data;
    }
    
    public static class EnablesForData{
        int minHits=0;
        boolean exactMatch=false;
    }

}
