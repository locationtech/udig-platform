/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
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
package net.refractions.udig.catalog.internal.wmt.ui.properties;

import net.refractions.udig.style.sld.editor.DialogSettingsStyleContent;


/**
 * Persist an IMemento onto the style blackboard to save 
 * Style Dialog Settings between runs.
 * 
 * @see net.refractions.udig.style.sld.editor.DialogSettingsStyleContent
 */
public class WMTLayerStyleContent extends DialogSettingsStyleContent{

    public static final String EXTENSION_ID = "net.refractions.udig.catalog.internal.wmt.ui.properties.dialogSettings"; //$NON-NLS-1$

    @Override
    public String getId() {
        return EXTENSION_ID;
    }   
}
