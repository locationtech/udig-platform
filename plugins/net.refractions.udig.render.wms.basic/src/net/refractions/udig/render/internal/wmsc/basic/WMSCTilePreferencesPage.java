/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.render.internal.wmsc.basic;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.internal.PreferenceConstants;
import net.refractions.udig.catalog.wmsc.server.TileImageReadWriter;
import net.refractions.udig.catalog.wmsc.server.TileWorkerQueue;
import net.refractions.udig.render.wms.basic.internal.Messages;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preferences page for setting WMS-C Tile cache settings
 * 
 * @author GDavis
 *
 */
public class WMSCTilePreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private DirectoryFieldEditor diskCacheFieldEditor;
	private RadioGroupFieldEditor cachingRadioFieldEditor;
	private Button cacheClearBtn;
	
    public WMSCTilePreferencesPage() {
        super(GRID); 
        setPreferenceStore(CatalogPlugin.getDefault().getPreferenceStore());
		setDescription(Messages.WMSCTilePreferencePage_pageDescription); 

    }

	@Override
	protected void createFieldEditors() {
		
		// maximum concurrent tile requests
		IntegerFieldEditor conRequestsFieldEditor = new IntegerFieldEditor(PreferenceConstants.P_WMSCTILE_MAX_CON_REQUESTS, 
        		Messages.WMSCTilePreferencePage_maxConRequests,
                getFieldEditorParent());
		conRequestsFieldEditor.setValidRange(TileWorkerQueue.minWorkingQueueSize, TileWorkerQueue.maxWorkingQueueSize);
		conRequestsFieldEditor.setEmptyStringAllowed(true);
        addField(conRequestsFieldEditor);		
		
		// in-mem or on-disk caching?
		cachingRadioFieldEditor = new RadioGroupFieldEditor(
                PreferenceConstants.P_WMSCTILE_CACHING,
                Messages.WMSCTilePreferencePage_caching_desc,
                1,
                new String[][]{
                        {Messages.WMSCTilePreferencePage_inmemory, WMSCTileCaching.INMEMORY.toString()},
                        {Messages.WMSCTilePreferencePage_ondisk, WMSCTileCaching.ONDISK.toString()}
                        }, 
                        getFieldEditorParent(), true);
        addField(cachingRadioFieldEditor);
        String value = CatalogPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.P_WMSCTILE_CACHING);
        boolean diskOn = false;
		if (value.equals(WMSCTileCaching.ONDISK.toString())) diskOn = true;        
        
		// only enable the disk-caching preferences if disk caching is turned on
        diskCacheFieldEditor = new DirectoryFieldEditor(PreferenceConstants.P_WMSCTILE_DISKDIR,
        		Messages.WMSCTilePreferencePage_disklabel,
        		getFieldEditorParent());
        diskCacheFieldEditor.setEnabled(diskOn, getFieldEditorParent());
        addField(diskCacheFieldEditor);
        
        cacheClearBtn = new Button(getFieldEditorParent(), SWT.PUSH);
        cacheClearBtn.setText(Messages.WMSCTilePreferencePage_clearcachebtn);
        
        cacheClearBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	MessageBox mb = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
                mb.setText(Messages.WMSCTilePreferencePage_clearcachebtn);
                mb.setMessage(Messages.WMSCTilePreferencePage_clearcacheConfirm);
                int rc = mb.open();
                if (rc == SWT.OK) {
                	boolean success = clearCache();
                	mb = new MessageBox(Display.getCurrent().getActiveShell(), SWT.OK );
                	mb.setText(Messages.WMSCTilePreferencePage_clearcachebtn);
                	if (!success) {
                        mb.setMessage(Messages.WMSCTilePreferencePage_clearcacheError);
                	}
                	else {
                		mb.setMessage(Messages.WMSCTilePreferencePage_clearcacheSuccess);
                	}
                    rc = mb.open();
                }
            }
        });
        cacheClearBtn.setEnabled(diskOn);
	}
	
	/**
	 * clear the tile cache for the given dir
	 * 
	 */
	private boolean clearCache() {
		// Make a TileImageReadWriter with the base directory based on the current
		// value of the disk cache location (even if it is not "set" yet since the current
		// value is what the user sees) otherwise use the current setting.
		String dir = diskCacheFieldEditor.getStringValue();
		
		String setDir = CatalogPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.P_WMSCTILE_DISKDIR);
		if (dir.equals("") && setDir != null && !setDir.equals("") ) {
			dir = setDir;
		}
		else if (dir.equals("")) {
			return false;
		}
		TileImageReadWriter tileReadWriter = new TileImageReadWriter(null, dir);
		return tileReadWriter.clearCache();
	}	

	public void init(IWorkbench arg0) {
	}

	/**
	 * Catch events to notice when the caching type changes and de/activate the disk caching 
	 * config editors depending on what is selected.  
	 * 
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		if (event.getSource().equals(cachingRadioFieldEditor)) {
			String value = ((String)event.getNewValue()).toString();
			boolean diskOn = false;
			if (value.equals(WMSCTileCaching.ONDISK.toString())) diskOn = true;
			diskCacheFieldEditor.setEnabled(diskOn, getFieldEditorParent());
			cacheClearBtn.setEnabled(diskOn);
		}
	}
    
}