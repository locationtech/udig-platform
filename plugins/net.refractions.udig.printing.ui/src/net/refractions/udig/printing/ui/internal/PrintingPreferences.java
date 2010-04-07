/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.printing.ui.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import net.refractions.udig.printing.ui.TemplateFactory;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Provides ...TODO summary sentence
 * <p>
 * TODO Description
 * </p><p>
 * Responsibilities:
 * <ul>
 * <li>
 * <li>
 * </ul>
 * </p><p>
 * Example Use:<pre><code>
 * PrintingPreferences x = new PrintingPreferences( ... );
 * TODO code example
 * </code></pre>
 * </p>
 * @author Richard Gould
 * @since 0.3
 */
public class PrintingPreferences extends PreferencePage implements IWorkbenchPreferencePage {

    private String defaultTemplate;
    private List list;
    private ArrayList templateIds;

    /**
     * TODO summary sentence for createContents ...
     * 
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     * @param parent
     * @return
     */
    protected Control createContents( Composite parent ) {
        templateIds = new ArrayList();
        
		GridData gridData;
		Composite composite = new Composite(parent, SWT.NULL);
		
		GridLayout gridLayout = new GridLayout();
		int columns = 1;
		gridLayout.numColumns = columns;
		composite.setLayout(gridLayout);
		
		gridData = new GridData();
		
		Label urlLabel = new Label(composite, SWT.NONE);
		urlLabel.setText(Messages.PrintingPreferences_label_defaultTemplate); 
		urlLabel.setLayoutData(gridData);

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		
		Map templates = PrintingPlugin.getDefault().getTemplateFactories();

		list = new List(composite, SWT.SINGLE|SWT.BORDER);
		list.setLayoutData(gridData);
		
		Iterator iter = templates.entrySet().iterator();
		for(int i = 0; iter.hasNext(); i++) {
		    Map.Entry entry = (Map.Entry) iter.next();
		    
		    TemplateFactory templateFactory = (TemplateFactory) entry.getValue();
		    
		    templateIds.add(i, entry.getKey());
		    
		    if (defaultTemplate.equals(templateFactory.getName())) {
		        list.select(i);
		    }
		    
		    list.add(templateFactory.getName());
		}
				
		return composite;
    }

    /**
     * TODO summary sentence for init ...
     * 
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     * @param workbench
     */
    public void init( IWorkbench workbench ) {
        defaultTemplate = PrintingPlugin.getDefault().getPluginPreferences().getString(PrintingPlugin.PREF_DEFAULT_TEMPLATE);
    }

    protected void performDefaults() {
        super.performDefaults();
    }
    public boolean performOk() {
        int selectionIndex = list.getSelectionIndex();
        if( selectionIndex==-1 || selectionIndex>templateIds.size()-1 )
            return super.performOk();
        defaultTemplate = (String) templateIds.get(selectionIndex);
        PrintingPlugin.getDefault().getPluginPreferences().setValue(PrintingPlugin.PREF_DEFAULT_TEMPLATE, defaultTemplate);
        PrintingPlugin.getDefault().savePluginPreferences();
        return super.performOk();
    }
}
