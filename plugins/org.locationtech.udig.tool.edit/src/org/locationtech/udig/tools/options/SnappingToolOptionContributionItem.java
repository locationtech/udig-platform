/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.options;

import org.locationtech.udig.project.ui.tool.options.ToolOptionContributionItem;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.preferences.PreferenceConstants;
import org.locationtech.udig.tools.edit.support.SnapBehaviour;

import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * Provides a tool option contribution to the map status bar allowing users to 
 * change the snapping preferences.
 * 
 * @author leviputna
 * @since 1.2.0
 */
public class SnappingToolOptionContributionItem extends ToolOptionContributionItem {
    
    private Combo combo;
    
    private String[] lables = {
            Messages.EditToolPreferences_noSnapping,
            Messages.EditToolPreferences_selected,
            Messages.EditToolPreferences_current,
            Messages.EditToolPreferences_all,
            Messages.EditToolPreferences_grid
            };
    
    private String[] behaviour = {
            SnapBehaviour.OFF.toString(),
            SnapBehaviour.SELECTED.toString(),
            SnapBehaviour.CURRENT_LAYER.toString(),
            SnapBehaviour.ALL_LAYERS.toString(),
            SnapBehaviour.GRID.toString()
            };
    
    @Override
    protected IPreferenceStore fillFields( Composite parent ) {

        combo = new Combo(parent,  SWT.READ_ONLY);
        combo.setItems(lables);
        StatusLineLayoutData data = new StatusLineLayoutData();
        data.heightHint=SWT.DEFAULT;
        data.widthHint=100;
        combo.setLayoutData(data);
        
        addField( PreferenceConstants.P_SNAP_BEHAVIOUR, combo , behaviour);
        
        return EditPlugin.getDefault().getPreferenceStore();
    }
}
