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
