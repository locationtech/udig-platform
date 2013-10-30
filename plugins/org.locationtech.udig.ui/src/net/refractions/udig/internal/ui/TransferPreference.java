/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.internal.ui;

import java.util.Set;

import net.refractions.udig.ui.AbstractStrategizedTransfer;
import net.refractions.udig.ui.internal.Messages;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Jesse
 * @since 1.1.0
 */
public class TransferPreference extends FieldEditorPreferencePage
        implements
            IWorkbenchPreferencePage {

    public TransferPreference( ) {
        super(GRID);
        IPreferenceStore store = UiPlugin.getDefault().getPreferenceStore();
        setPreferenceStore(store);
        setDescription(Messages.TransferPreference_transfer_preference_description); 
    }

    @Override
    protected void createFieldEditors() {
        Set<Transfer> transfers = UDIGDNDProcessor.getTransfers();
        for( Transfer transfer : transfers ) {
            if ( transfer instanceof AbstractStrategizedTransfer ){
                AbstractStrategizedTransfer ast=(AbstractStrategizedTransfer) transfer;
                String[] names=ast.getStrategyNames();
                if( names.length<2 )
                    continue;
                
                String[][] labelsAndValues=new String[names.length][];
                for( int i = 0; i < labelsAndValues.length; i++ ) {
                    labelsAndValues[i]=new String[]{names[i], ""+i}; //$NON-NLS-1$
                    getPreferenceStore().setDefault(ast.getClass().getName(), ""); //$NON-NLS-1$
                }
                addField(new RadioGroupFieldEditor(transfer.getClass().getName(), ast.getTransferName(), 2, labelsAndValues, getFieldEditorParent(),true));
            }
        }
    }

    public void init( IWorkbench workbench ) {
    }

}
