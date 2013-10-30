/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
