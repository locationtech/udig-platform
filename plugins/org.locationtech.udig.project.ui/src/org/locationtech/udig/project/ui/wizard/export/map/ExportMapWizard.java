/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2013, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.wizard.export.map;

import java.util.Collection;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.locationtech.udig.core.internal.Icons;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.project.ui.wizard.export.MapSelectorPage;
import org.locationtech.udig.project.ui.wizard.export.image.MapSelectorPageWithScaleColumn;

/**
 * Wizard for exporting a project, its maps and all associated data to a project.
 * 
 * @author Jesse
 */
public class ExportMapWizard extends Wizard implements IExportWizard {

	MapSelectorPage mapSelector;
	
	/**
	 * Create a new instance
	 */
	public ExportMapWizard() {
	    setWindowTitle("Export Map");
	    String title = null; // will use default page title
	    ImageDescriptor banner = ProjectUIPlugin.getDefault().getImageDescriptor( Icons.WIZBAN +"exportselection_wiz.gif" );
	    //setDefaultPageImageDescriptor(banner);
	    mapSelector = new MapSelectorPageWithScaleColumn("Export Selection", title, banner );
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		Collection<IMap> maps = mapSelector.getMaps();
		for (IMap map : maps) {
			collect(map);
		}
		
		zip();
		return true;
	}

	private void collect(IMap map) {
		
	}

	private void zip() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		mapSelector.setSelection(selection);
	}

}
