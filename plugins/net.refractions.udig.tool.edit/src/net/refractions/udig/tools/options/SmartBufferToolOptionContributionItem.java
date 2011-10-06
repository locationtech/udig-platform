/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
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
package net.refractions.udig.tools.options;

import net.refractions.udig.project.ui.tool.options.ToolOptionContributionItem;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.preferences.PreferenceConstants;
import net.refractions.udig.tools.edit.support.SnapBehaviour;

import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import com.vividsolutions.jts.operation.buffer.BufferOp;

/**
 * Provides a tool option contribution to the map status bar allowing users to 
 * change the snapping preferences.
 * 
 * @author leviputna
 * @since 1.2.0
 */
public class SmartBufferToolOptionContributionItem extends ToolOptionContributionItem {
    
    private Combo combo;
    
    private String[] lables = {
            "Round",
            "Butt",
            "Square"
            };
    
    private String[] behaviour = {
            //BufferOp.CAP_ROUND,
            //BufferOp.CAP_BUTT,
            //BufferOp.CAP_SQUARE
            "0",
            "1",
            "2"
            };

    private Spinner spinner;


    
    @Override
    protected IPreferenceStore fillFields( Composite parent ) {
        
        //new Label(parent, SWT.NONE).setText("Size:");
        
        spinner = new Spinner(parent, SWT.BORDER);
        //new Label(parent, SWT.NONE).setText("px");
        spinner.setDigits(3);
        spinner.setMaximum(300000);
        spinner.setMinimum(1);
        spinner.setIncrement(10);
        spinner.setTextLimit(3);
        
        StatusLineLayoutData data = new StatusLineLayoutData();
        data.heightHint=SWT.DEFAULT;
        data.widthHint=50;
        spinner.setLayoutData(data);
        addField( PreferenceConstants.P_BUFFER_DEFULT_SIZE, spinner);
        
        
        
//        combo = new Combo(parent,  SWT.READ_ONLY);
//        combo.setItems(lables);
//        StatusLineLayoutData data2 = new StatusLineLayoutData();
//        data2.heightHint=SWT.DEFAULT;
//        data2.widthHint=60;
//        combo.setLayoutData(data2);
//       
//        addField( PreferenceConstants.P_BUFFER_CAP_TYPE, combo , behaviour);
        
       
        
        return EditPlugin.getDefault().getPreferenceStore();
    }

}
